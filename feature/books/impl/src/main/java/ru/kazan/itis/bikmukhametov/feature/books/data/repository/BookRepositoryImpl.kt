package ru.kazan.itis.bikmukhametov.feature.books.data.repository

import ru.kazan.itis.bikmukhametov.feature.books.api.datasource.local.LocalBookDataSource
import ru.kazan.itis.bikmukhametov.feature.books.api.datasource.remote.RemoteBookDataSource
import ru.kazan.itis.bikmukhametov.feature.books.api.model.BookModel
import ru.kazan.itis.bikmukhametov.feature.books.api.repository.BookRepository
import javax.inject.Inject

class BookRepositoryImpl @Inject constructor(
    private val remoteBookDataSource: RemoteBookDataSource,
    private val localBookDataSource: LocalBookDataSource
) : BookRepository {

    override suspend fun getBooks(): List<BookModel> {
        // Получаем книги из Firestore
        val remoteBooksResult = remoteBookDataSource.getBooks()

        if (remoteBooksResult.isFailure) {
            // В случае ошибки возвращаем пустой список
            return emptyList()
        }

        val remoteBooks = remoteBooksResult.getOrNull() ?: return emptyList()

        // Получаем список ID локально скачанных книг
        val downloadedBookIds = localBookDataSource.getAllDownloadedBookIds().toSet()

        // Объединяем данные: обновляем статус скачивания и путь к файлу для локальных книг
        return remoteBooks.map { book ->
            val isDownloaded = downloadedBookIds.contains(book.id)
            val localFilePath = if (isDownloaded) {
                localBookDataSource.getBookFilePath(book.id)
            } else {
                null
            }

            book.copy(
                isDownloaded = isDownloaded,
                localFilePath = localFilePath
            )
        }
    }
}

