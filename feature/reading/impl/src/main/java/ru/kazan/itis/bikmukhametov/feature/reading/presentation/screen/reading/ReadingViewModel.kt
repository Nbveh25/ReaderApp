package ru.kazan.itis.bikmukhametov.feature.reading.presentation.screen.reading

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
import ru.kazan.itis.bikmukhametov.core.resources.string.StringResourceProvider
import ru.kazan.itis.bikmukhametov.feature.reading.R
import ru.kazan.itis.bikmukhametov.feature.reading.api.usecase.DeleteBookUseCase
import ru.kazan.itis.bikmukhametov.feature.reading.api.usecase.GetBookUseCase
import ru.kazan.itis.bikmukhametov.feature.reading.api.usecase.GetReadingPositionUseCase
import ru.kazan.itis.bikmukhametov.feature.reading.api.usecase.GetReadingSettingsUseCase
import ru.kazan.itis.bikmukhametov.feature.reading.api.usecase.ReadBookFileUseCase
import ru.kazan.itis.bikmukhametov.feature.reading.api.usecase.SaveFontSizeUseCase
import ru.kazan.itis.bikmukhametov.feature.reading.api.usecase.SaveLineSpacingUseCase
import ru.kazan.itis.bikmukhametov.feature.reading.api.usecase.SaveReadingPositionUseCase
import ru.kazan.itis.bikmukhametov.feature.reading.api.usecase.SaveThemeModeUseCase
import javax.inject.Inject

@HiltViewModel
class ReadingViewModel @Inject constructor(
    private val getBookUseCase: GetBookUseCase,
    private val readBookFileUseCase: ReadBookFileUseCase,
    private val deleteBookUseCase: DeleteBookUseCase,
    private val getReadingPositionUseCase: GetReadingPositionUseCase,
    private val saveReadingPositionUseCase: SaveReadingPositionUseCase,
    private val getReadingSettingsUseCase: GetReadingSettingsUseCase,
    private val saveFontSizeUseCase: SaveFontSizeUseCase,
    private val saveLineSpacingUseCase: SaveLineSpacingUseCase,
    private val saveThemeModeUseCase: SaveThemeModeUseCase,
    private val stringResourceProvider: StringResourceProvider
) : ViewModel() {

    private val _uiState = MutableStateFlow(ReadingState())
    val uiState: StateFlow<ReadingState> = _uiState.asStateFlow()

    private val _effect = MutableSharedFlow<ReadingEffect>(extraBufferCapacity = 1)
    val effect: SharedFlow<ReadingEffect> = _effect.asSharedFlow()

    fun onIntent(intent: ReadingIntent) {
        when (intent) {
            is ReadingIntent.LoadBook -> loadBook()
            is ReadingIntent.ScrollPositionChanged -> handleScrollPositionChanged(intent.position)
            is ReadingIntent.ToggleSettings -> toggleSettings()
            is ReadingIntent.FontSizeChanged -> handleFontSizeChanged(intent.size)
            is ReadingIntent.LineSpacingChanged -> handleLineSpacingChanged(intent.spacing)
            is ReadingIntent.ThemeModeChanged -> handleThemeModeChanged(intent.mode)
            is ReadingIntent.Retry -> loadBook()
            is ReadingIntent.DeleteBook -> deleteBook()
        }
    }

    fun initialize(bookId: String) {
        _uiState.update { it.copy(bookId = bookId) }
        // Загружаем сохраненные настройки
        viewModelScope.launch {
            val settings = getReadingSettingsUseCase()
            _uiState.update { state ->
                state.copy(
                    fontSize = settings.fontSize,
                    lineSpacing = settings.lineSpacing,
                    themeMode = settings.themeMode
                )
            }
        }
        loadBook()
    }

    private fun loadBook() {
        val bookId = _uiState.value.bookId
        if (bookId.isEmpty()) return

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            try {
                // Получаем информацию о книге
                val bookResult = getBookUseCase(bookId)
                if (bookResult.isFailure) {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            error = bookResult.exceptionOrNull()?.message ?: stringResourceProvider.getString(R.string.reading_error_load_book)
                        )
                    }
                    return@launch
                }

                val book = bookResult.getOrNull() ?: return@launch

                // Проверяем, скачана ли книга
                if (!book.isDownloaded || book.localFilePath == null) {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            error = stringResourceProvider.getString(R.string.reading_error_book_not_downloaded)
                        )
                    }
                    return@launch
                }

                // Читаем файл
                val contentResult = readBookFileUseCase(book.localFilePath ?: "", book.format)
                if (contentResult.isFailure) {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            error = contentResult.exceptionOrNull()?.message ?: stringResourceProvider.getString(R.string.reading_error_read_file)
                        )
                    }
                    return@launch
                }

                val content = contentResult.getOrNull() ?: return@launch
                val totalCharacters = content.length

                val savedPosition = getReadingPositionUseCase(bookId)
                val scrollPosition = savedPosition.coerceIn(0, totalCharacters)

                val progressPercent = if (totalCharacters > 0) {
                    (scrollPosition * 100 / totalCharacters).coerceIn(0, 100)
                } else {
                    0
                }

                _uiState.update {
                    it.copy(
                        isLoading = false,
                        bookTitle = book.title,
                        bookContent = content,
                        totalCharacters = totalCharacters,
                        scrollPosition = scrollPosition,
                        progressPercent = progressPercent
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = e.message ?: stringResourceProvider.getString(R.string.reading_unknown_error)
                    )
                }
            }
        }
    }

    private fun handleScrollPositionChanged(position: Int) {
        val totalCharacters = _uiState.value.totalCharacters
        val clampedPosition = position.coerceIn(0, totalCharacters)
        val progressPercent = if (totalCharacters > 0) {
            (clampedPosition * 100 / totalCharacters).coerceIn(0, 100)
        } else {
            0
        }

        _uiState.update {
            it.copy(
                scrollPosition = clampedPosition,
                progressPercent = progressPercent
            )
        }

        viewModelScope.launch {
            saveReadingPositionUseCase(_uiState.value.bookId, clampedPosition)
        }
    }

    private fun toggleSettings() {
        _uiState.update { it.copy(isSettingsOpen = !it.isSettingsOpen) }
    }

    private fun handleFontSizeChanged(size: Int) {
        val clampedSize = size.coerceIn(1, 3)
        _uiState.update { it.copy(fontSize = clampedSize) }
        viewModelScope.launch {
            saveFontSizeUseCase(clampedSize)
        }
    }

    private fun handleLineSpacingChanged(spacing: Int) {
        val clampedSpacing = spacing.coerceIn(1, 3)
        _uiState.update { it.copy(lineSpacing = clampedSpacing) }
        viewModelScope.launch {
            saveLineSpacingUseCase(clampedSpacing)
        }
    }

    private fun handleThemeModeChanged(mode: Int) {
        val clampedMode = mode.coerceIn(0, 1)
        _uiState.update { it.copy(themeMode = clampedMode) }
        viewModelScope.launch {
            saveThemeModeUseCase(clampedMode)
        }
    }

    private fun deleteBook() {
        val bookId = _uiState.value.bookId
        viewModelScope.launch {
            val result = deleteBookUseCase(bookId)
            result.onSuccess { deleted ->
                if (deleted) {
                    _effect.emit(ReadingEffect.BookDeleted)
                    _effect.emit(ReadingEffect.NavigateBack)
                } else {
                    _effect.emit(ReadingEffect.ShowMessage(stringResourceProvider.getString(R.string.reading_error_delete_failed)))
                }
            }.onFailure { error ->
                _effect.emit(ReadingEffect.ShowMessage(
                    error.message ?: stringResourceProvider.getString(R.string.reading_error_delete_failed)
                ))
            }
        }
    }
}

