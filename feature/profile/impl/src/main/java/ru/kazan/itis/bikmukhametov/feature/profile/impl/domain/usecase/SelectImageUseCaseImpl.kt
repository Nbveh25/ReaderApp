package ru.kazan.itis.bikmukhametov.feature.profile.impl.domain.usecase

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import ru.kazan.itis.bikmukhametov.core.resources.image.ImageResourceProvider
import ru.kazan.itis.bikmukhametov.feature.profile.api.model.ImageModel
import ru.kazan.itis.bikmukhametov.feature.profile.api.usecase.SelectImageUseCase
import java.io.IOException
import javax.inject.Inject

class SelectImageUseCaseImpl @Inject constructor(
    private val imageResourceProvider: ImageResourceProvider
) : SelectImageUseCase {

    companion object {
        private const val MAX_FILE_SIZE_BYTES = 10 * 1024 * 1024 // 10 МБ
    }

    override suspend fun invoke(imageUriString: String): Result<ImageModel> = withContext(Dispatchers.IO) {
        try {
            // Открываем поток для чтения
            val inputStream = imageResourceProvider.openInputStream(imageUriString)
                ?: return@withContext Result.failure(IOException("Не удалось открыть файл"))

            // Читаем данные из потока
            val imageBytes = try {
                inputStream.use { it.readBytes() }
            } catch (e: OutOfMemoryError) {
                return@withContext Result.failure(IOException("Файл слишком большой для обработки", e))
            } catch (e: Exception) {
                return@withContext Result.failure(IOException("Ошибка чтения файла: ${e.message}", e))
            }

            // Проверяем размер файла
            if (imageBytes.size > MAX_FILE_SIZE_BYTES) {
                return@withContext Result.failure(
                    IOException("Файл слишком большой. Максимальный размер: ${MAX_FILE_SIZE_BYTES / (1024 * 1024)} МБ")
                )
            }

            // Получаем имя файла
            val fileName = imageResourceProvider.getFileName(imageUriString)
                ?: "profile_photo_${System.currentTimeMillis()}.jpg"

            Result.success(ImageModel(imageBytes, fileName))
        } catch (e: SecurityException) {
            Result.failure(IOException("Нет доступа к файлу", e))
        } catch (e: Exception) {
            Result.failure(IOException("Ошибка при выборе изображения: ${e.message}", e))
        }
    }
}

