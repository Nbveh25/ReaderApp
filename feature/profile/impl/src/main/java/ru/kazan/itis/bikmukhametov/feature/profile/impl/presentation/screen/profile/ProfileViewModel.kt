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
import ru.kazan.itis.bikmukhametov.feature.profile.api.usecase.GetUserProfileUseCase
import ru.kazan.itis.bikmukhametov.feature.profile.api.usecase.SignOutUseCase
import ru.kazan.itis.bikmukhametov.feature.profile.api.usecase.UpdateUserNameUseCase
import ru.kazan.itis.bikmukhametov.feature.profile.api.usecase.UploadProfilePhotoUseCase
import java.io.IOException
import java.net.UnknownHostException
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val getUserProfileUseCase: GetUserProfileUseCase,
    private val updateUserNameUseCase: UpdateUserNameUseCase,
    private val uploadProfilePhotoUseCase: UploadProfilePhotoUseCase,
    private val signOutUseCase: SignOutUseCase
) : ViewModel() {

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
            is ProfileIntent.PhotoSelected -> handlePhotoSelected(intent.inputStream, intent.fileName)
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

    private fun handlePhotoSelected(inputStream: java.io.InputStream, fileName: String) {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isUploadingPhoto = true,
                    error = null
                )
            }

            try {
                // Используем use для автоматического закрытия потока
                inputStream.use { stream ->
                    uploadProfilePhotoUseCase.invoke(stream, fileName)
                        .onSuccess { photoUrl ->
                            // Обновляем профиль с новым URL фото
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
                            _effect.emit(ProfileEffect.ShowMessage("Фото успешно загружено"))
                            // Перезагружаем профиль для синхронизации
                            loadProfile()
                        }
                        .onFailure { error ->
                            _uiState.update { it.copy(isUploadingPhoto = false) }
                            handleError(error)
                        }
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
                _effect.emit(ProfileEffect.ShowMessage("Имя не может быть пустым"))
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
                        // Обновляем профиль с новым именем
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
                        _effect.emit(ProfileEffect.ShowMessage("Имя успешно обновлено"))
                        // Перезагружаем профиль для синхронизации
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
        
        val isNetworkError = error is IOException ||
                error is UnknownHostException ||
                (error.message?.contains("сеть", ignoreCase = true) == true &&
                 error.message?.contains("аутентификации", ignoreCase = true) != true &&
                 error.message?.contains("Доступ запрещен", ignoreCase = true) != true) ||
                (error.message?.contains("network", ignoreCase = true) == true &&
                 error.message?.contains("authentication", ignoreCase = true) != true &&
                 error.message?.contains("forbidden", ignoreCase = true) != true) ||
                error.message?.contains("интернет", ignoreCase = true) == true

        val errorMsg = when {
            error.message?.contains("аутентификации", ignoreCase = true) == true ||
            error.message?.contains("authentication", ignoreCase = true) == true -> 
                error.message ?: "Ошибка аутентификации"
            error.message?.contains("Доступ запрещен", ignoreCase = true) == true ||
            error.message?.contains("forbidden", ignoreCase = true) == true -> 
                error.message ?: "Доступ запрещен"
            isNetworkError -> "Ошибка сети. Проверьте подключение к интернету"
            else -> error.message ?: "Произошла ошибка"
        }

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

