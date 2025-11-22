package ru.kazan.itis.bikmukhametov.feature.books.presentation.screen.books

sealed interface BooksEffect {
    data class ShowMessage(val message: String) : BooksEffect
    data class NavigateToReading(val bookId: String) : BooksEffect
}

