package ru.kazan.itis.bikmukhametov.feature.upload.api.usecase

import java.io.InputStream

interface SaveFileLocallyUseCase {

    suspend fun invoke(
        inputStream: InputStream,
        fileName: String
    ): Result<String>
}

