package ru.kazan.itis.bikmukhametov.feature.reading.api.usecase

interface GetReadingPositionUseCase {
    suspend operator fun invoke(bookId: String): Int
}

