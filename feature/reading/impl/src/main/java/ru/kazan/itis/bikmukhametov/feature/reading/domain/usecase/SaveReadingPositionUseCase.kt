package ru.kazan.itis.bikmukhametov.feature.reading.domain.usecase

import ru.kazan.itis.bikmukhametov.feature.reading.api.repository.ReadingRepository
import ru.kazan.itis.bikmukhametov.feature.reading.api.usecase.SaveReadingPositionUseCase
import javax.inject.Inject

class SaveReadingPositionUseCaseImpl @Inject constructor(
    private val readingRepository: ReadingRepository
) : SaveReadingPositionUseCase {
    override suspend fun invoke(bookId: String, position: Int) {
        readingRepository.saveReadingPosition(bookId, position)
    }
}

