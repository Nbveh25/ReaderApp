package ru.kazan.itis.bikmukhametov.feature.upload.impl.data.datasource.local

import android.content.Context
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import javax.inject.Inject
import ru.kazan.itis.bikmukhametov.feature.upload.api.datasource.local.LocalFileStorage

class LocalFileStorageImpl @Inject constructor(
    private val context: Context
) : LocalFileStorage {
    companion object {
        private const val BOOKS_FOLDER = "uploaded_books"
    }

    override suspend fun saveFileLocally(
        inputStream: InputStream,
        fileName: String
    ): Result<String> {
        return try {
            val booksDir = File(context.filesDir, BOOKS_FOLDER)
            if (!booksDir.exists()) {
                booksDir.mkdirs()
            }

            val file = File(booksDir, fileName)

            FileOutputStream(file).use { outputStream ->
                inputStream.copyTo(outputStream)
            }

            Result.success(file.absolutePath)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}