package ru.kazan.itis.bikmukhametov.feature.upload.api.model

import java.io.ByteArrayInputStream
import java.io.InputStream

/**
 * Модель данных файла.
 * 
 * @param fileBytes Байты файла.
 * @param fileName Имя файла.
 * @param fileSize Размер файла в байтах.
 */
data class FileData(
    val fileBytes: ByteArray,
    val fileName: String,
    val fileSize: Long
) {
    /**
     * Создает InputStream из байтов файла.
     */
    fun createInputStream(): InputStream = ByteArrayInputStream(fileBytes)

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as FileData

        if (!fileBytes.contentEquals(other.fileBytes)) return false
        if (fileName != other.fileName) return false
        if (fileSize != other.fileSize) return false

        return true
    }

    override fun hashCode(): Int {
        var result = fileBytes.contentHashCode()
        result = 31 * result + fileName.hashCode()
        result = 31 * result + fileSize.hashCode()
        return result
    }
}

