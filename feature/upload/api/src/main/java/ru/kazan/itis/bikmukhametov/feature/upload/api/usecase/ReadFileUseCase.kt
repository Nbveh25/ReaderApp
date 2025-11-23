package ru.kazan.itis.bikmukhametov.feature.upload.api.usecase

import ru.kazan.itis.bikmukhametov.feature.upload.api.model.FileData

/**
 * Use case для чтения файла по URI.
 */
interface ReadFileUseCase {

    /**
     * Читает файл по URI и возвращает его данные.
     * 
     * @param fileUri URI файла для чтения.
     * @return [Result] с данными файла ([FileData]) в случае успеха,
     *         или [Result.failure] с ошибкой в случае неудачи.
     */
    suspend fun invoke(fileUri: String): Result<FileData>
}

