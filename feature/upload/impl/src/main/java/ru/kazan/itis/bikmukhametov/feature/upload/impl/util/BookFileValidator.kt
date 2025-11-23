package ru.kazan.itis.bikmukhametov.feature.upload.impl.util

import java.io.IOException

internal object BookFileValidator {

    private val ALLOWED_EXTENSIONS = listOf("txt", "epub", "pdf")
    private const val MAX_FILE_SIZE_BYTES = 100 * 1024 * 1024
    private const val MIN_FILE_SIZE_BYTES = 1

    fun validate(fileName: String, fileSize: Long): Result<Unit> {
        val extension = fileName.substringAfterLast('.', "").lowercase().trim()

        if (extension.isEmpty()) {
            return Result.failure(
                IOException(
                    "Не удалось определить формат файла. Убедитесь, что файл имеет расширение (.txt, .epub, .pdf)"
                )
            )
        }

        if (extension !in ALLOWED_EXTENSIONS) {
            return Result.failure(
                IOException(
                    "Неподдерживаемый формат файла: .$extension. Поддерживаемые форматы: ${ALLOWED_EXTENSIONS.joinToString(", ")}"
                )
            )
        }

        if (fileSize < MIN_FILE_SIZE_BYTES) {
            return Result.failure(IOException("Файл слишком маленький"))
        }

        if (fileSize > MAX_FILE_SIZE_BYTES) {
            return Result.failure(
                IOException("Файл слишком большой. Максимальный размер: ${MAX_FILE_SIZE_BYTES / (1024 * 1024)} МБ")
            )
        }

        return Result.success(Unit)
    }
}

