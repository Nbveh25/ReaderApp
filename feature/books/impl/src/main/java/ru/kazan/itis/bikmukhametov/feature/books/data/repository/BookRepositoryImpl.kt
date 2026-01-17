package ru.kazan.itis.bikmukhametov.feature.books.data.repository

import ru.kazan.itis.bikmukhametov.feature.books.api.datasource.local.LocalBookDataSource
import ru.kazan.itis.bikmukhametov.feature.books.api.datasource.remote.RemoteBookDataSource
import ru.kazan.itis.bikmukhametov.feature.books.api.repository.BookRepository
import ru.kazan.itis.bikmukhametov.feature.books.data.datasource.cache.dao.BookDao
import ru.kazan.itis.bikmukhametov.feature.books.data.datasource.cache.mapper.toEntity
import ru.kazan.itis.bikmukhametov.feature.books.data.datasource.cache.mapper.toModel
import ru.kazan.itis.bikmukhametov.model.BookModel
import javax.inject.Inject

internal class BookRepositoryImpl @Inject constructor(
    private val remoteBookDataSource: RemoteBookDataSource,
    private val localBookDataSource: LocalBookDataSource,
    private val bookDao: BookDao
) : BookRepository {

    override suspend fun getBooks(): Result<List<BookModel>> {
        return try {
            // 1. Пытаемся взять данные из Room
            val cachedEntities = bookDao.getAllBooks()

            // 2. Если кэш не пустой, возвращаем его сразу (или реализуем логику обновления)
            if (cachedEntities.isNotEmpty()) {
                return Result.success(cachedEntities.map { it.toModel() })
            }

            // 3. Если кэша нет, идем в RemoteDataSource
            val remoteBooksResult = remoteBookDataSource.getBooks()
            val remoteBooks = remoteBooksResult.getOrNull() ?: return Result.success(emptyList())

            // 4. Обогащаем данные локальной информацией (как у вас и было)
            val downloadedBookIds = localBookDataSource.getAllDownloadedBookIds().toSet()
            val finalBooks = remoteBooks.map { book ->
                val isDownloaded = downloadedBookIds.contains(book.id)
                book.copy(
                    isDownloaded = isDownloaded,
                    localFilePath = if (isDownloaded) localBookDataSource.getBookFilePath(book.id) else null
                )
            }

            // 5. Сохраняем свежие данные в кэш для следующего раза
            bookDao.insertBooks(finalBooks.map { it.toEntity() })

            Result.success(finalBooks)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun deleteBook(bookId: String): Result<Boolean> {
        return try {
            val deleted = localBookDataSource.deleteBook(bookId)
            if (deleted) {
                Result.success(true)
            } else {
                Result.failure(Exception("Не удалось удалить книгу"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

