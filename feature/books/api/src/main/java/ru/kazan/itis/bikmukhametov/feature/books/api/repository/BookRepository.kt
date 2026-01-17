package ru.kazan.itis.bikmukhametov.feature.books.api.repository

import ru.kazan.itis.bikmukhametov.model.BookModel

interface BookRepository {
    suspend fun getBooks(): Result<List<BookModel>>
    
    suspend fun deleteBook(bookId: String): Result<Boolean>
}

