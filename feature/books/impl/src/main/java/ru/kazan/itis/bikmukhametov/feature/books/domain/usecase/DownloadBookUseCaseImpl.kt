package ru.kazan.itis.bikmukhametov.feature.books.domain.usecase

import ru.kazan.itis.bikmukhametov.feature.books.api.datasource.remote.BookDownloader
import ru.kazan.itis.bikmukhametov.feature.books.api.datasource.local.LocalBookDataSource
import ru.kazan.itis.bikmukhametov.feature.books.api.usecase.DownloadBookUseCase
import ru.kazan.itis.bikmukhametov.feature.books.data.util.FileStorageManager
import java.io.IOException
import javax.inject.Inject

class DownloadBookUseCaseImpl @Inject constructor(
    private val bookDownloader: BookDownloader,
    private val fileStorageManager: FileStorageManager,
    private val localBookDataSource: LocalBookDataSource
) : DownloadBookUseCase {

    override suspend fun invoke(bookId: String, fileUrl: String): Result<Unit> {
        return try {
            // Проверяем, не скачана ли книга уже
            if (localBookDataSource.isBookDownloaded(bookId)) {
                return Result.success(Unit)
            }

            // Определяем формат книги по URL
            val format = bookDownloader.getBookFormatFromUrl(fileUrl)

            // Скачиваем файл
            val downloadResult = bookDownloader.downloadBook(fileUrl)
            if (downloadResult.isFailure) {
                return Result.failure(
                    downloadResult.exceptionOrNull() ?: IOException("Ошибка загрузки")
                )
            }

            val fileBytes = downloadResult.getOrNull() ?: return Result.failure(
                IOException("Пустой файл")
            )

            // Сохраняем файл локально
            val saved = fileStorageManager.saveBookFile(bookId, fileBytes, format)
            if (!saved) {
                return Result.failure(IOException("Ошибка сохранения файла"))
            }

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(IOException("Ошибка скачивания книги: ${e.message}", e))
        }
    }
}

