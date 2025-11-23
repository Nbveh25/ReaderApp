package ru.kazan.itis.bikmukhametov.feature.reading.api.repository

import ru.kazan.itis.bikmukhametov.util.enum.BookFormat

/**
 * Репозиторий для чтения файлов книг.
 *
 * Предоставляет абстракцию для чтения файлов различных форматов (TXT, PDF, EPUB).
 */
interface BookFileRepository {

    /**
     * Читает содержимое файла книги.
     *
     * @param filePath Путь к файлу книги.
     * @param format Формат файла (TXT, PDF, EPUB).
     * @return [Result] с текстовым содержимым файла в случае успеха,
     *         или [Result.failure] с ошибкой в случае неудачи.
     */
    suspend fun readBookFile(filePath: String, format: BookFormat): Result<String>
}

