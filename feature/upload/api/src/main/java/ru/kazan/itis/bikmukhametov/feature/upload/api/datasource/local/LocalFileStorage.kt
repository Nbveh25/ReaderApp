package ru.kazan.itis.bikmukhametov.feature.upload.api.datasource.local

import java.io.InputStream

interface LocalFileStorage {

    suspend fun saveFileLocally(
        inputStream: InputStream,
        fileName: String
    ): Result<String>
}

