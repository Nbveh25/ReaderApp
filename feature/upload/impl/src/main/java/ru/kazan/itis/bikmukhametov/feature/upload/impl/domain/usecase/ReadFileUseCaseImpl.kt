package ru.kazan.itis.bikmukhametov.feature.upload.impl.domain.usecase

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import ru.kazan.itis.bikmukhametov.core.resources.image.ImageResourceProvider
import ru.kazan.itis.bikmukhametov.feature.upload.api.model.FileData
import ru.kazan.itis.bikmukhametov.feature.upload.api.usecase.ReadFileUseCase
import java.io.IOException
import javax.inject.Inject

/**
 * Реализация use case для чтения файла.
 */
internal class ReadFileUseCaseImpl @Inject constructor(
    private val imageResourceProvider: ImageResourceProvider
) : ReadFileUseCase {

    override suspend fun invoke(fileUri: String): Result<FileData> = withContext(Dispatchers.IO) {
        try {
            val fileName = imageResourceProvider.getFileName(fileUri)
                ?: "book_${System.currentTimeMillis()}"

            val inputStream = imageResourceProvider.openInputStream(fileUri)
                ?: return@withContext Result.failure(IOException("Не удалось открыть файл"))

            val fileBytes = try {
                inputStream.use { it.readBytes() }
            } catch (e: Exception) {
                return@withContext Result.failure(IOException("Ошибка чтения файла: ${e.message}", e))
            }

            val fileData = FileData(
                fileBytes = fileBytes,
                fileName = fileName,
                fileSize = fileBytes.size.toLong()
            )

            Result.success(fileData)
        } catch (e: Exception) {
            Result.failure(IOException("Ошибка при чтении файла: ${e.message}", e))
        }
    }
}

