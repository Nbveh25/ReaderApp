package ru.kazan.itis.bikmukhametov.feature.books.api.usecase

interface DownloadBookUseCase {
    suspend operator fun invoke(bookId: String, fileUrl: String): Result<Unit>
}

