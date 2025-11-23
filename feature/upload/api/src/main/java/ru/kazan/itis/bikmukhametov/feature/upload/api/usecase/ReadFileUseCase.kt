package ru.kazan.itis.bikmukhametov.feature.upload.api.usecase

import ru.kazan.itis.bikmukhametov.feature.upload.api.model.FileData

/**
 * Use case для чтения файла по URI.
 */
interface ReadFileUseCase {
    /**
     * Читает файл по URI и возвращает его данные.
     * 
     * @param fileUri URI файла
     * @return Result<FileData> - данные файла при успехе
     */
    suspend operator fun invoke(fileUri: String): Result<FileData>
}

