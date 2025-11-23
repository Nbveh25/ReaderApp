package ru.kazan.itis.bikmukhametov.feature.upload.api.usecase

interface ValidateBookFileUseCase {
    suspend operator fun invoke(fileName: String, fileSize: Long): Result<Unit>
}

