package ru.kazan.itis.bikmukhametov.feature.books.data.util

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import ru.kazan.itis.bikmukhametov.util.enum.BookFormat
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Менеджер для работы с локальным хранилищем файлов книг.
 *
 * Поддерживаемые форматы: .pdf, .epub, .txt
 *
 * Использует Internal Storage приложения - приватное хранилище, которое:
 * - Не удаляется при обновлении приложения
 * - Удаляется только при удалении приложения
 * - Не требует разрешений на Android 10+
 * - Безопасно для хранения пользовательских данных
 *
 * Путь к файлам: /data/data/{package_name}/files/books/
 */
internal class FileStorageManager @Inject constructor(
    private val context: Context
) {
    companion object {
        private const val BOOKS_DIRECTORY = "books"
        private val SUPPORTED_EXTENSIONS = listOf(".pdf", ".epub", ".txt")
    }

    /**
     * Получает директорию для хранения книг.
     * Создает директорию, если она не существует.
     */
    private fun getBooksDirectory(): File {
        val booksDir = File(context.filesDir, BOOKS_DIRECTORY)
        if (!booksDir.exists()) {
            booksDir.mkdirs()
        }
        return booksDir
    }

    /**
     * Получает файл книги по её ID и формату.
     */
    fun getBookFile(bookId: String, format: BookFormat = BookFormat.PDF): File {
        val fileName = "$bookId${format.extension}"
        return File(getBooksDirectory(), fileName)
    }

    /**
     * Находит файл книги по ID, проверяя все поддерживаемые форматы.
     */
    suspend fun findBookFile(bookId: String): File? = withContext(Dispatchers.IO) {
        BookFormat.values().forEach { format ->
            val file = getBookFile(bookId, format)
            if (file.exists()) {
                return@withContext file
            }
        }
        null
    }

    /**
     * Получает путь к файлу книги.
     */
    fun getBookFilePath(bookId: String, format: BookFormat = BookFormat.PDF): String {
        return getBookFile(bookId, format).absolutePath
    }

    /**
     * Получает путь к файлу книги, автоматически определяя формат.
     */
    suspend fun getBookFilePathAuto(bookId: String): String? {
        return findBookFile(bookId)?.absolutePath
    }

    /**
     * Проверяет, существует ли файл книги.
     */
    suspend fun bookFileExists(bookId: String, format: BookFormat = BookFormat.PDF): Boolean =
        withContext(Dispatchers.IO) {
            getBookFile(bookId, format).exists()
        }

    /**
     * Проверяет, существует ли файл книги в любом из поддерживаемых форматов.
     */
    suspend fun bookFileExistsAny(bookId: String): Boolean = withContext(Dispatchers.IO) {
        findBookFile(bookId) != null
    }

    /**
     * Сохраняет файл книги из байтового массива.
     */
    suspend fun saveBookFile(
        bookId: String,
        data: ByteArray,
        format: BookFormat = BookFormat.PDF
    ): Boolean = withContext(Dispatchers.IO) {
        try {
            val file = getBookFile(bookId, format)
            FileOutputStream(file).use { outputStream ->
                outputStream.write(data)
                outputStream.flush()
            }
            true
        } catch (e: IOException) {
            throw IOException("Ошибка сохранения файла книги: ${e.message}", e)
        }
    }

    /**
     * Сохраняет файл книги из InputStream.
     */
    suspend fun saveBookFileFromStream(
        bookId: String,
        inputStream: java.io.InputStream,
        format: BookFormat = BookFormat.PDF
    ): Boolean = withContext(Dispatchers.IO) {
        try {
            val file = getBookFile(bookId, format)
            FileOutputStream(file).use { outputStream ->
                inputStream.copyTo(outputStream)
                outputStream.flush()
            }
            true
        } catch (e: IOException) {
            throw IOException("Ошибка сохранения файла книги: ${e.message}", e)
        }
    }

    /**
     * Удаляет файл книги.
     */
    suspend fun deleteBookFile(bookId: String, format: BookFormat? = null): Boolean =
        withContext(Dispatchers.IO) {
            try {
                if (format != null) {
                    val file = getBookFile(bookId, format)
                    if (file.exists()) {
                        file.delete()
                    } else {
                        true
                    }
                } else {
                    val file = findBookFile(bookId)
                    if (file != null && file.exists()) {
                        file.delete()
                    } else {
                        true
                    }
                }
            } catch (e: Exception) {
                false
            }
        }

    /**
     * Получает размер файла книги в байтах.
     */
    suspend fun getBookFileSize(bookId: String, format: BookFormat? = null): Long =
        withContext(Dispatchers.IO) {
            val file = if (format != null) {
                getBookFile(bookId, format)
            } else {
                findBookFile(bookId)
            }

            if (file != null && file.exists()) {
                file.length()
            } else {
                0L
            }
        }

    /**
     * Получает список всех сохраненных книг.
     */
    suspend fun getAllDownloadedBookIds(): List<String> = withContext(Dispatchers.IO) {
        val booksDir = getBooksDirectory()
        if (!booksDir.exists()) {
            return@withContext emptyList()
        }

        booksDir.listFiles()
            ?.filter { file ->
                file.isFile && SUPPORTED_EXTENSIONS.any { ext ->
                    file.name.endsWith(ext, ignoreCase = true)
                }
            }
            ?.map { file ->
                SUPPORTED_EXTENSIONS.forEach { ext ->
                    if (file.name.endsWith(ext, ignoreCase = true)) {
                        return@map file.name.substring(0, file.name.length - ext.length)
                    }
                }
                file.nameWithoutExtension
            }
            ?.distinct()
            ?: emptyList()
    }

    /**
     * Получает общий размер всех сохраненных книг в байтах.
     */
    suspend fun getTotalStorageSize(): Long = withContext(Dispatchers.IO) {
        val booksDir = getBooksDirectory()
        if (!booksDir.exists()) {
            return@withContext 0L
        }

        booksDir.listFiles()
            ?.filter { file ->
                file.isFile && SUPPORTED_EXTENSIONS.any { ext ->
                    file.name.endsWith(ext, ignoreCase = true)
                }
            }
            ?.sumOf { it.length() }
            ?: 0L
    }

    /**
     * Очищает все сохраненные книги.
     */
    suspend fun clearAllBooks(): Int = withContext(Dispatchers.IO) {
        val booksDir = getBooksDirectory()
        if (!booksDir.exists()) {
            return@withContext 0
        }

        var deletedCount = 0
        booksDir.listFiles()?.forEach { file ->
            if (file.isFile && SUPPORTED_EXTENSIONS.any { ext ->
                    file.name.endsWith(ext, ignoreCase = true)
                }) {
                if (file.delete()) {
                    deletedCount++
                }
            }
        }
        deletedCount
    }
}

