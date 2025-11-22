package ru.kazan.itis.bikmukhametov.feature.books.api.datasource.remote

import ru.kazan.itis.bikmukhametov.model.BookModel

interface RemoteBookDataSource {
    suspend fun getBooks(): Result<List<BookModel>>
}

