package ru.kazan.itis.bikmukhametov.feature.books.presentation.screen.books

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
import ru.kazan.itis.bikmukhametov.feature.books.api.datasource.local.LocalBookDataSource
import ru.kazan.itis.bikmukhametov.feature.books.api.usecase.DownloadBookUseCase
import ru.kazan.itis.bikmukhametov.feature.books.api.usecase.GetBooksUseCase
import ru.kazan.itis.bikmukhametov.feature.books.presentation.mapper.BookMapper
import ru.kazan.itis.bikmukhametov.feature.books.presentation.model.BookItem
import javax.inject.Inject

@HiltViewModel
class BooksViewModel @Inject constructor(
    private val getBooksUseCase: GetBooksUseCase,
    private val downloadBookUseCase: DownloadBookUseCase,
    private val localBookDataSource: LocalBookDataSource
) : ViewModel() {

    private val _uiState = MutableStateFlow(BooksState())
    val uiState: StateFlow<BooksState> = _uiState.asStateFlow()

    private val _effect = MutableSharedFlow<BooksEffect>(extraBufferCapacity = 1)
    val effect: SharedFlow<BooksEffect> = _effect.asSharedFlow()

    init {
        loadBooks()
    }

    fun onIntent(intent: BooksIntent) {
        when (intent) {
            is BooksIntent.SearchQueryChanged -> handleSearchQueryChanged(intent.query)
            is BooksIntent.BookClicked -> handleBookClicked(intent.bookId)
            is BooksIntent.DeleteBookClicked -> handleDeleteBook(intent.bookId)
            is BooksIntent.DownloadBookClicked -> handleDownloadBook(intent.bookId)
            is BooksIntent.RefreshClicked -> loadBooks()
            is BooksIntent.RetryClicked -> loadBooks()
        }
    }

    private fun loadBooks() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null, processingBookId = null) }

            try {
                val bookModels = getBooksUseCase.invoke()
                val bookItems = BookMapper.toBookItemList(bookModels)

                _uiState.update {
                    it.copy(
                        books = bookItems,
                        filteredBooks = filterBooks(bookItems, it.searchQuery),
                        isLoading = false,
                        processingBookId = null
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = "Ошибка загрузки книг: ${e.message}",
                        processingBookId = null
                    )
                }
            }
        }
    }

    private fun handleSearchQueryChanged(query: String) {
        _uiState.update { state ->
            val filtered = filterBooks(state.books, query)
            state.copy(
                searchQuery = query,
                filteredBooks = filtered
            )
        }
    }

    private fun filterBooks(books: List<BookItem>, query: String): List<BookItem> {
        if (query.isBlank()) return books
        val lowerQuery = query.lowercase()
        return books.filter {
            it.title.lowercase().contains(lowerQuery) ||
                    it.author.lowercase().contains(lowerQuery)
        }
    }

    private fun handleBookClicked(bookId: String) {
        viewModelScope.launch {
            _effect.emit(BooksEffect.NavigateToReading(bookId))
        }
    }

    private fun handleDeleteBook(bookId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(processingBookId = bookId) }

            try {
                // Удаляем книгу из локального хранилища
                val deleted = localBookDataSource.deleteBook(bookId)

                if (deleted) {
                    // Перезагружаем список для синхронизации с репозиторием
                    // processingBookId будет сброшен в loadBooks()
                    loadBooks()
                    _effect.emit(BooksEffect.ShowMessage("Книга удалена"))
                } else {
                    _uiState.update { it.copy(processingBookId = null) }
                    _effect.emit(BooksEffect.ShowMessage("Ошибка удаления книги"))
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(processingBookId = null) }
                _effect.emit(BooksEffect.ShowMessage("Ошибка удаления: ${e.message}"))
            }
        }
    }

    private fun handleDownloadBook(bookId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(processingBookId = bookId) }

            try {
                // Находим книгу в текущем списке для получения fileUrl
                val book = _uiState.value.books.find { it.id == bookId }
                if (book == null) {
                    _uiState.update { it.copy(processingBookId = null) }
                    _effect.emit(BooksEffect.ShowMessage("Книга не найдена"))
                    return@launch
                }

                val fileUrl = book.fileUrl
                if (fileUrl == null) {
                    _uiState.update { it.copy(processingBookId = null) }
                    _effect.emit(BooksEffect.ShowMessage("URL для скачивания не найден"))
                    return@launch
                }

                // Скачиваем книгу из Yandex Cloud Storage
                val downloadResult = downloadBookUseCase.invoke(bookId, fileUrl)

                if (downloadResult.isSuccess) {
                    // Перезагружаем список для синхронизации с репозиторием
                    loadBooks()
                    _effect.emit(BooksEffect.ShowMessage("Книга успешно загружена"))
                } else {
                    val error = downloadResult.exceptionOrNull()
                    _uiState.update { it.copy(processingBookId = null) }
                    _effect.emit(
                        BooksEffect.ShowMessage(
                            error?.message ?: "Ошибка загрузки книги"
                        )
                    )
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(processingBookId = null) }
                _effect.emit(BooksEffect.ShowMessage("Ошибка загрузки: ${e.message}"))
            }
        }
    }
}

