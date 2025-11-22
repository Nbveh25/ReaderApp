package ru.kazan.itis.bikmukhametov.feature.reading.domain.usecase

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import ru.kazan.itis.bikmukhametov.feature.reading.api.usecase.ReadBookFileUseCase
import ru.kazan.itis.bikmukhametov.util.enum.BookFormat
import java.io.File
import java.io.FileNotFoundException
import javax.inject.Inject

class ReadBookFileUseCaseImpl @Inject constructor() : ReadBookFileUseCase {
    override suspend fun invoke(filePath: String, format: BookFormat): Result<String> {
        return withContext(Dispatchers.IO) {
            try {
                val file = File(filePath)
                if (!file.exists()) {
                    return@withContext Result.failure(FileNotFoundException("Файл не найден: $filePath"))
                }

                val content = when (format) {
                    BookFormat.TXT -> {
                        file.readText(charset = Charsets.UTF_8)
                    }
                    BookFormat.PDF -> {
                        // Для PDF показываем сообщение о неподдерживаемом формате
                        // В будущем можно добавить библиотеку для чтения PDF
                        "Формат PDF пока не поддерживается. Используйте формат TXT или EPUB."
                    }
                    BookFormat.EPUB -> {
                        // Для EPUB показываем сообщение о неподдерживаемом формате
                        // В будущем можно добавить библиотеку для чтения EPUB
                        "Формат EPUB пока не поддерживается. Используйте формат TXT."
                    }
                }

                Result.success(content)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
}

