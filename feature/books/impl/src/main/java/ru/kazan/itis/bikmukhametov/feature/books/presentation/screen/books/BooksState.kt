package ru.kazan.itis.bikmukhametov.feature.books.presentation.screen.books

import ru.kazan.itis.bikmukhametov.feature.books.presentation.model.BookItem

data class BooksState(
    val books: List<BookItem> = emptyList(),

    val filteredBooks: List<BookItem> = emptyList(),

    val searchQuery: String = "",

    val isLoading: Boolean = false,

    val isRefreshing: Boolean = false,

    val error: String? = null,

    val processingBookId: String? = null
)

