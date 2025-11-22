package ru.kazan.itis.bikmukhametov.feature.books.api.datasource.remote

import ru.kazan.itis.bikmukhametov.util.enum.BookFormat


interface BookDownloader {

    suspend fun downloadBook(fileUrl: String): Result<ByteArray>

    fun getBookFormatFromUrl(fileUrl: String): BookFormat
}

