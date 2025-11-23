package ru.kazan.itis.bikmukhametov.feature.upload.impl.presentation.screen.upload

import android.util.Log
import com.google.firebase.auth.FirebaseUser
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
import ru.kazan.itis.bikmukhametov.feature.auth.api.usecase.GetCurrentUserUseCase
import ru.kazan.itis.bikmukhametov.feature.upload.api.usecase.ReadFileUseCase
import ru.kazan.itis.bikmukhametov.feature.upload.api.usecase.SaveFileLocallyUseCase
import ru.kazan.itis.bikmukhametov.feature.upload.api.usecase.UploadBookUseCase
import ru.kazan.itis.bikmukhametov.feature.upload.api.usecase.ValidateBookFileUseCase
import ru.kazan.itis.bikmukhametov.feature.upload.impl.R
import ru.kazan.itis.bikmukhametov.core.resources.string.StringResourceProvider
import javax.inject.Inject

@HiltViewModel
class UploadViewModel @Inject constructor(
    private val uploadBookUseCase: UploadBookUseCase,
    private val validateBookFileUseCase: ValidateBookFileUseCase,
    private val readFileUseCase: ReadFileUseCase,
    private val saveFileLocallyUseCase: SaveFileLocallyUseCase,
    private val getCurrentUserUseCase: GetCurrentUserUseCase,
    private val stringResourceProvider: StringResourceProvider
) : ViewModel() {

    private val _uiState = MutableStateFlow(UploadState())
    val uiState: StateFlow<UploadState> = _uiState.asStateFlow()

    private val _effect = MutableSharedFlow<UploadEffect>(extraBufferCapacity = 1)
    val effect: SharedFlow<UploadEffect> = _effect.asSharedFlow()

    fun onIntent(intent: UploadIntent) {
        when (intent) {
            is UploadIntent.TitleChanged -> handleTitleChanged(intent.title)
            is UploadIntent.AuthorChanged -> handleAuthorChanged(intent.author)
            is UploadIntent.SelectFileClicked -> handleSelectFileClicked()
            is UploadIntent.FileSelected -> handleFileSelected(intent.fileUri, intent.fileName)
            is UploadIntent.UploadClicked -> handleUploadClicked()
            is UploadIntent.RetryClicked -> handleRetryClicked()
        }
    }

    private fun handleTitleChanged(title: String) {
        _uiState.update { it.copy(title = title, error = null, isSuccess = false) }
    }

    private fun handleAuthorChanged(author: String) {
        _uiState.update { it.copy(author = author, error = null, isSuccess = false) }
    }

    private fun handleSelectFileClicked() {
        viewModelScope.launch {
            _effect.emit(UploadEffect.OpenFilePicker)
        }
    }

    private fun handleFileSelected(fileUri: String, fileName: String?) {
        viewModelScope.launch {
            if (fileName != null) {
                Log.d("UploadViewModel", "Выбран файл: URI=$fileUri, fileName=$fileName")
                _uiState.update {
                    it.copy(
                        selectedFileUri = fileUri,
                        selectedFileName = fileName,
                        error = null,
                        isSuccess = false
                    )
                }
            } else {
                val fileDataResult = readFileUseCase.invoke(fileUri)
                fileDataResult
                    .onSuccess { fileData ->
                        Log.d("UploadViewModel", "Выбран файл: URI=$fileUri, fileName=${fileData.fileName}")
                        _uiState.update {
                            it.copy(
                                selectedFileUri = fileUri,
                                selectedFileName = fileData.fileName,
                                error = null,
                                isSuccess = false
                            )
                        }
                    }
                    .onFailure { error ->
                        Log.e("UploadViewModel", "Ошибка чтения файла: ${error.message}")
                        val errorMessage = error.message ?: stringResourceProvider.getString(R.string.upload_error_read_file)
                        _effect.emit(UploadEffect.ShowMessage(errorMessage))
                        _uiState.update {
                            it.copy(
                                error = errorMessage
                            )
                        }
                    }
            }
        }
    }

    private fun handleUploadClicked() {
        val state = _uiState.value
        val fileUri = state.selectedFileUri
        val fileName = state.selectedFileName
        val title = state.title.trim()
        val author = state.author.trim()

        if (fileUri == null || fileName == null) {
            viewModelScope.launch {
                _effect.emit(UploadEffect.ShowMessage(stringResourceProvider.getString(R.string.upload_message_select_file)))
            }
            return
        }

        viewModelScope.launch {
            val currentUser = getCurrentUserUseCase.invoke() as? FirebaseUser
            if (currentUser == null) {
                _effect.emit(UploadEffect.ShowMessage(stringResourceProvider.getString(R.string.upload_message_user_not_authorized)))
                return@launch
            }

            val user = currentUser
            _uiState.update {
                it.copy(
                    isUploading = true,
                    error = null,
                    uploadProgress = 0f,
                    isSuccess = false
                )
            }

            try {
                // Читаем файл
                val fileDataResult = readFileUseCase.invoke(fileUri)
                
                val fileData = fileDataResult.getOrElse { error ->
                    _uiState.update {
                        it.copy(
                            isUploading = false,
                            error = error.message ?: stringResourceProvider.getString(R.string.upload_error_failed_to_read_file)
                        )
                    }
                    return@launch
                }

                // Валидация файла
                Log.d("UploadViewModel", "Валидация файла: fileName=${fileData.fileName}, size=${fileData.fileSize}")
                validateBookFileUseCase.invoke(fileData.fileName, fileData.fileSize)
                    .onFailure { error ->
                        Log.e("UploadViewModel", "Ошибка валидации: ${error.message}")
                        _uiState.update {
                            it.copy(
                                isUploading = false,
                                error = error.message ?: stringResourceProvider.getString(R.string.upload_error_validation_failed)
                            )
                        }
                        return@launch
                    }

                // Сохраняем файл локально
                val localSaveResult = saveFileLocallyUseCase.invoke(
                    fileData.createInputStream(),
                    fileData.fileName
                )

                localSaveResult.onSuccess { localPath ->
                    Log.d("UploadViewModel", "Файл сохранен локально: $localPath")
                }

                // Загружаем в YCS и сохраняем метаданные в Firestore
                uploadBookUseCase.invoke(
                    inputStream = fileData.createInputStream(),
                    fileName = fileData.fileName,
                    fileSize = fileData.fileSize,
                    title = title,
                    author = author,
                    userId = user.uid,
                    onProgress = { progress ->
                        _uiState.update { it.copy(uploadProgress = progress) }
                    }
                )
                    .onSuccess {
                        _uiState.update {
                            it.copy(
                                isUploading = false,
                                isSuccess = true,
                                uploadProgress = 1f
                            )
                        }
                        _effect.emit(UploadEffect.ShowMessage(stringResourceProvider.getString(R.string.upload_message_success)))
                        _effect.emit(UploadEffect.NavigateToBooks)
                    }
                    .onFailure { error ->
                        _uiState.update {
                            it.copy(
                                isUploading = false,
                                error = error.message ?: stringResourceProvider.getString(R.string.upload_error_upload_failed)
                            )
                        }
                        _effect.emit(UploadEffect.ShowMessage(error.message ?: stringResourceProvider.getString(R.string.upload_error_upload)))
                    }
            } catch (e: Exception) {
                Log.e("UploadViewModel", "Ошибка при загрузке книги", e)
                val errorMessage = e.message ?: stringResourceProvider.getString(R.string.upload_error_occurred)
                _uiState.update {
                    it.copy(
                        isUploading = false,
                        error = errorMessage
                    )
                }
                _effect.emit(UploadEffect.ShowMessage(errorMessage))
            }
        }
    }

    private fun handleRetryClicked() {
        _uiState.update { it.copy(error = null, isSuccess = false) }
    }
}

