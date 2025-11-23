package ru.kazan.itis.bikmukhametov.feature.upload.api.datasource.remote

import java.io.InputStream

interface BookUploader {

    suspend fun uploadBook(
        inputStream: InputStream,
        fileName: String,
        userId: String,
        onProgress: (Float) -> Unit
    ): Result<String>
}

