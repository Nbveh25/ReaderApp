package ru.kazan.itis.bikmukhametov.feature.upload.api.model

import java.io.InputStream

/**
 * Данные файла для загрузки.
 */
data class FileData(
    val fileBytes: ByteArray,
    val fileName: String,
    val fileSize: Long
) {
    /**
     * Создает новый InputStream из байтов файла.
     * Можно вызывать несколько раз для создания независимых потоков.
     */
    fun createInputStream(): InputStream {
        return java.io.ByteArrayInputStream(fileBytes)
    }
}

