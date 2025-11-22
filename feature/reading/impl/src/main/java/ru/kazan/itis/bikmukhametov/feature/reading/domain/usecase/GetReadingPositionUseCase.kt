package ru.kazan.itis.bikmukhametov.feature.reading.domain.usecase

import ru.kazan.itis.bikmukhametov.feature.reading.api.repository.ReadingRepository
import ru.kazan.itis.bikmukhametov.feature.reading.api.usecase.GetReadingPositionUseCase
import javax.inject.Inject

class GetReadingPositionUseCaseImpl @Inject constructor(
    private val readingRepository: ReadingRepository
) : GetReadingPositionUseCase {
    override suspend fun invoke(bookId: String): Int {
        return readingRepository.getReadingPosition(bookId)
    }
}

