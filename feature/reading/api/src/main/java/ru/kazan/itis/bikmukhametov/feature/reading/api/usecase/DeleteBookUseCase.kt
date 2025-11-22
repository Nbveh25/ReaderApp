package ru.kazan.itis.bikmukhametov.feature.reading.api.usecase

interface DeleteBookUseCase {
    suspend operator fun invoke(bookId: String): Result<Boolean>
}

