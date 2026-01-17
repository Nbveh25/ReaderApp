package ru.kazan.itis.bikmukhametov.feature.profile.impl.presentation.screen.profile

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import android.net.Uri
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.logEvent
import ru.kazan.itis.bikmukhametov.feature.profile.api.usecase.GetUserProfileUseCase
import ru.kazan.itis.bikmukhametov.feature.profile.api.usecase.SelectImageUseCase
import ru.kazan.itis.bikmukhametov.feature.profile.api.usecase.SignOutUseCase
import ru.kazan.itis.bikmukhametov.feature.profile.api.usecase.UpdateUserNameUseCase
import ru.kazan.itis.bikmukhametov.feature.profile.api.usecase.UploadProfilePhotoUseCase
import ru.kazan.itis.bikmukhametov.feature.profile.impl.R
import ru.kazan.itis.bikmukhametov.core.resources.string.StringResourceProvider
import java.io.ByteArrayInputStream
import java.io.IOException
import javax.inject.Inject

@HiltViewModel
internal class ProfileViewModel @Inject constructor(
    private val getUserProfileUseCase: GetUserProfileUseCase,
    private val updateUserNameUseCase: UpdateUserNameUseCase,
    private val uploadProfilePhotoUseCase: UploadProfilePhotoUseCase,
    private val selectImageUseCase: SelectImageUseCase,
    private val signOutUseCase: SignOutUseCase,
    private val stringResourceProvider: StringResourceProvider,
    private val analytics: FirebaseAnalytics
) : ViewModel() {

    init {
        analytics.logEvent(FirebaseAnalytics.Event.SCREEN_VIEW) {
            param(FirebaseAnalytics.Param.SCREEN_NAME, "ProfileScreen")
            param(FirebaseAnalytics.Param.SCREEN_CLASS, "ProfileViewModel")
        }
    }

    private val _uiState = MutableStateFlow(ProfileState())
    val uiState: StateFlow<ProfileState> = _uiState.asStateFlow()

    private val _effect = MutableSharedFlow<ProfileEffect>(extraBufferCapacity = 1)
    val effect: SharedFlow<ProfileEffect> = _effect.asSharedFlow()

    init {
        loadProfile()
    }

    fun onIntent(intent: ProfileIntent) {
        when (intent) {
            is ProfileIntent.LoadProfile -> loadProfile()
            is ProfileIntent.PhotoClick -> handlePhotoClick()
            is ProfileIntent.PhotoSelected -> handlePhotoSelected(intent.imageUri)
            is ProfileIntent.NameChanged -> handleNameChanged(intent.name)
            is ProfileIntent.UpdateNameClicked -> handleUpdateName()
            is ProfileIntent.LogoutClicked -> handleLogout()
            is ProfileIntent.RetryClicked -> loadProfile()
        }
    }

    private fun loadProfile() {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isLoading = true,
                    error = null
                )
            }

            try {
                getUserProfileUseCase.invoke()
                    .onSuccess { profile ->
                        _uiState.update {
                            it.copy(
                                userProfile = profile,
                                nameInput = profile.name ?: "",
                                isLoading = false
                            )
                        }
                    }
                    .onFailure { error ->
                        handleError(error)
                    }
            } catch (e: Exception) {
                handleError(e)
            } finally {
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }

    private fun handlePhotoClick() {
        viewModelScope.launch {
            _effect.emit(ProfileEffect.OpenImagePicker)
        }
    }

    private fun handlePhotoSelected(imageUri: Uri) {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isUploadingPhoto = true,
                    error = null
                )
            }

            try {
                val imageUriString = imageUri.toString()

                selectImageUseCase.invoke(imageUriString)
                    .onSuccess { imageModel ->

                        val inputStream = ByteArrayInputStream(imageModel.bytes)

                        uploadProfilePhotoUseCase.invoke(inputStream, imageModel.fileName)
                            .onSuccess { photoUrl ->

                                val currentProfile = _uiState.value.userProfile
                                Log.d("ProfileViewModel", "URL загруженного файла: $photoUrl")

                                if (currentProfile != null) {
                                    _uiState.update {
                                        it.copy(
                                            userProfile = currentProfile.copy(photoUrl = photoUrl),
                                            isUploadingPhoto = false
                                        )
                                    }
                                } else {
                                    _uiState.update { it.copy(isUploadingPhoto = false) }
                                }
                                _effect.emit(ProfileEffect.ShowMessage(stringResourceProvider.getString(R.string.profile_message_photo_uploaded)))

                                loadProfile()
                            }
                            .onFailure { error ->
                                _uiState.update { it.copy(isUploadingPhoto = false) }
                                handleError(error)
                            }
                    }
                    .onFailure { error ->
                        _uiState.update { it.copy(isUploadingPhoto = false) }
                        handleError(error)
                    }
            } catch (e: Exception) {
                _uiState.update { it.copy(isUploadingPhoto = false) }
                handleError(e)
            }
        }
    }

    private fun handleNameChanged(name: String) {
        _uiState.update { it.copy(nameInput = name) }
    }

    private fun handleUpdateName() {
        val name = _uiState.value.nameInput.trim()
        if (name.isBlank()) {
            viewModelScope.launch {
                _effect.emit(ProfileEffect.ShowMessage(stringResourceProvider.getString(R.string.profile_error_name_empty)))
            }
            return
        }

        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isUpdatingName = true,
                    error = null
                )
            }

            try {
                updateUserNameUseCase.invoke(name)
                    .onSuccess {
                        val currentProfile = _uiState.value.userProfile
                        if (currentProfile != null) {
                            _uiState.update {
                                it.copy(
                                    userProfile = currentProfile.copy(name = name),
                                    isUpdatingName = false
                                )
                            }
                        } else {
                            _uiState.update { it.copy(isUpdatingName = false) }
                        }
                        _effect.emit(ProfileEffect.ShowMessage(stringResourceProvider.getString(R.string.profile_message_name_updated)))

                        loadProfile()
                    }
                    .onFailure { error ->
                        _uiState.update { it.copy(isUpdatingName = false) }
                        handleError(error)
                    }
            } catch (e: Exception) {
                _uiState.update { it.copy(isUpdatingName = false) }
                handleError(e)
            }
        }
    }

    private fun handleLogout() {
        viewModelScope.launch {
            try {
                signOutUseCase.invoke()
                _effect.emit(ProfileEffect.NavigateToAuth)
            } catch (e: Exception) {
                handleError(e)
            }
        }
    }

    private fun handleError(error: Throwable) {
        Log.e("ProfileViewModel", "Ошибка: ${error.message}", error)
        
        val errorMsg = error.message ?: stringResourceProvider.getString(R.string.profile_error_occurred)

        _uiState.update {
            it.copy(
                error = errorMsg,
                isLoading = false,
                isUploadingPhoto = false,
                isUpdatingName = false
            )
        }

        viewModelScope.launch {
            _effect.emit(ProfileEffect.ShowMessage(errorMsg))
        }
    }
}

