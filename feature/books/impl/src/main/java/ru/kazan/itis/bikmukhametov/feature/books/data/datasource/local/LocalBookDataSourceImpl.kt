package ru.kazan.itis.bikmukhametov.feature.books.data.datasource.local

import ru.kazan.itis.bikmukhametov.feature.books.api.datasource.local.LocalBookDataSource
import ru.kazan.itis.bikmukhametov.feature.books.data.util.FileStorageManager
import javax.inject.Inject

class LocalBookDataSourceImpl @Inject constructor(
    private val fileStorageManager: FileStorageManager
) : LocalBookDataSource {

    override suspend fun isBookDownloaded(bookId: String): Boolean {
        return fileStorageManager.bookFileExistsAny(bookId)
    }

    override suspend fun getBookFilePath(bookId: String): String? {
        return fileStorageManager.getBookFilePathAuto(bookId)
    }

    override suspend fun getAllDownloadedBookIds(): List<String> {
        return fileStorageManager.getAllDownloadedBookIds()
    }

    override suspend fun deleteBook(bookId: String): Boolean {
        return fileStorageManager.deleteBookFile(bookId)
    }
}

