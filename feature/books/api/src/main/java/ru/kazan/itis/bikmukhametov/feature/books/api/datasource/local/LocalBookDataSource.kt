package ru.kazan.itis.bikmukhametov.feature.books.api.datasource.local

interface LocalBookDataSource {

    suspend fun isBookDownloaded(bookId: String): Boolean

    suspend fun getBookFilePath(bookId: String): String?

    suspend fun getAllDownloadedBookIds(): List<String>

    suspend fun deleteBook(bookId: String): Boolean
}
