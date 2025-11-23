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
import javax.inject.Inject

@HiltViewModel
class UploadViewModel @Inject constructor(
    private val uploadBookUseCase: UploadBookUseCase,
    private val validateBookFileUseCase: ValidateBookFileUseCase,
    private val readFileUseCase: ReadFileUseCase,
    private val saveFileLocallyUseCase: SaveFileLocallyUseCase,
    private val getCurrentUserUseCase: GetCurrentUserUseCase
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
            // Если имя файла не передано, получаем его через use case
            val actualFileName = if (fileName != null) {
                fileName
            } else {
                readFileUseCase.invoke(fileUri)
                    .fold(
                        onSuccess = { it.fileName },
                        onFailure = { "book_${System.currentTimeMillis()}" }
                    )
            }
            
            Log.d("UploadViewModel", "Выбран файл: URI=$fileUri, fileName=$actualFileName")
            
            _uiState.update {
                it.copy(
                    selectedFileUri = fileUri,
                    selectedFileName = actualFileName,
                    error = null,
                    isSuccess = false
                )
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
                _effect.emit(UploadEffect.ShowMessage("Выберите файл для загрузки"))
            }
            return
        }

        viewModelScope.launch {
            val currentUser = getCurrentUserUseCase.invoke() as? FirebaseUser
            if (currentUser == null) {
                _effect.emit(UploadEffect.ShowMessage("Пользователь не авторизован"))
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
                // Читаем файл через use case
                val fileDataResult = readFileUseCase.invoke(fileUri)
                
                if (fileDataResult.isFailure) {
                    val errorMessage = fileDataResult.exceptionOrNull()?.message ?: "Не удалось прочитать файл"
                    _uiState.update {
                        it.copy(
                            isUploading = false,
                            error = errorMessage
                        )
                    }
                    return@launch
                }

                val fileData = fileDataResult.getOrThrow()
                val actualFileName = fileData.fileName
                val fileSize = fileData.fileSize

                Log.d("UploadViewModel", "Валидация файла: fileName=$actualFileName, size=$fileSize")
                validateBookFileUseCase.invoke(actualFileName, fileSize)
                    .onFailure { error ->
                        Log.e("UploadViewModel", "Ошибка валидации: ${error.message}")
                        _uiState.update {
                            it.copy(
                                isUploading = false,
                                error = error.message ?: "Ошибка валидации файла"
                            )
                        }
                        return@launch
                    }

                val localSaveResult = saveFileLocallyUseCase.invoke(
                    fileData.createInputStream(),
                    actualFileName
                )

                localSaveResult.onSuccess { localPath ->
                    Log.d("UploadViewModel", "Файл сохранен локально: $localPath")
                }

                // Загружаем в YCS и сохраняем метаданные в Firestore (создаем новый поток)
                uploadBookUseCase.invoke(
                    inputStream = fileData.createInputStream(),
                    fileName = actualFileName,
                    fileSize = fileSize,
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
                        _effect.emit(UploadEffect.ShowMessage("Книга успешно загружена"))
                        _effect.emit(UploadEffect.NavigateToBooks)
                    }
                    .onFailure { error ->
                        _uiState.update {
                            it.copy(
                                isUploading = false,
                                error = error.message ?: "Ошибка загрузки книги"
                            )
                        }
                        _effect.emit(UploadEffect.ShowMessage(error.message ?: "Ошибка загрузки"))
                    }
            } catch (e: Exception) {
                Log.e("UploadViewModel", "Ошибка при загрузке книги", e)
                _uiState.update {
                    it.copy(
                        isUploading = false,
                        error = e.message ?: "Произошла ошибка"
                    )
                }
                _effect.emit(UploadEffect.ShowMessage(e.message ?: "Произошла ошибка"))
            }
        }
    }

    private fun handleRetryClicked() {
        _uiState.update { it.copy(error = null, isSuccess = false) }
    }
}

