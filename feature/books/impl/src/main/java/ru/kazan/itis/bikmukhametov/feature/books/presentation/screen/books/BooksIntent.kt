package ru.kazan.itis.bikmukhametov.feature.books.presentation.screen.books

sealed interface BooksIntent {

    data class SearchQueryChanged(val query: String) : BooksIntent

    data class BookClicked(val bookId: String) : BooksIntent

    data class DeleteBookClicked(val bookId: String) : BooksIntent

    data class DownloadBookClicked(val bookId: String) : BooksIntent

    object RefreshClicked : BooksIntent

    object RetryClicked : BooksIntent
}

