package ru.kazan.itis.bikmukhametov.feature.upload.impl.domain.usecase

import ru.kazan.itis.bikmukhametov.feature.upload.api.datasource.remote.BookUploader
import ru.kazan.itis.bikmukhametov.feature.upload.api.model.BookMetadata
import ru.kazan.itis.bikmukhametov.feature.upload.api.repository.BookRepository
import ru.kazan.itis.bikmukhametov.feature.upload.api.usecase.UploadBookUseCase
import java.io.InputStream
import javax.inject.Inject

/**
 * Реализация use case для загрузки книги.
 */
internal class UploadBookUseCaseImpl @Inject constructor(
    private val bookUploader: BookUploader,
    private val bookRepository: BookRepository
) : UploadBookUseCase {

    override suspend fun invoke(
        inputStream: InputStream,
        fileName: String,
        fileSize: Long,
        title: String,
        author: String,
        userId: String,
        onProgress: (Float) -> Unit
    ): Result<Unit> {
        // Загружаем кнпжку в YCS
        val uploadResult = bookUploader.uploadBook(
            inputStream = inputStream,
            fileName = fileName,
            userId = userId,
            onProgress = { progress ->
                // Прогресс загрузки (0.0 - 0.9)
                onProgress(progress * 0.9f)
            }
        )

        if (uploadResult.isFailure) {
            return uploadResult.map { }
        }

        val fileUrl = uploadResult.getOrNull() ?: return Result.failure(
            Exception("Не удалось получить URL загруженного файла")
        )

        // Определяем формат файла
        val format = fileName.substringAfterLast('.', "").lowercase()

        // Сохраняем метаданные в Firestore
        onProgress(0.95f)

        val metadata = BookMetadata(
            title = title,
            author = author,
            fileUrl = fileUrl,
            userId = userId,
            fileName = fileName,
            fileSize = fileSize,
            format = format
        )

        val saveResult = bookRepository.saveBookMetadata(metadata)

        if (saveResult.isFailure) {
            return saveResult
        }

        onProgress(1.0f)

        return Result.success(Unit)
    }
}

