package ru.kazan.itis.bikmukhametov.feature.books.api.repository

import ru.kazan.itis.bikmukhametov.feature.books.api.model.BookModel

interface BookRepository {
    suspend fun getBooks(): List<BookModel>
}

