package ru.kazan.itis.bikmukhametov.feature.reading.api.usecase

interface SaveReadingPositionUseCase {
    suspend operator fun invoke(bookId: String, position: Int)
}

