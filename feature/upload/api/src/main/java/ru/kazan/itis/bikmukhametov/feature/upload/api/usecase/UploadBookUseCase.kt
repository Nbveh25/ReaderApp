package ru.kazan.itis.bikmukhametov.feature.upload.api.usecase

import java.io.InputStream

interface UploadBookUseCase {
    suspend operator fun invoke(
        inputStream: InputStream,
        fileName: String,
        fileSize: Long,
        title: String,
        author: String,
        userId: String,
        onProgress: (Float) -> Unit
    ): Result<Unit>
}

