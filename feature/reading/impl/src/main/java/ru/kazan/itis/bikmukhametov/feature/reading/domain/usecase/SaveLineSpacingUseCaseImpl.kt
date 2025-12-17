package ru.kazan.itis.bikmukhametov.feature.reading.domain.usecase

import ru.kazan.itis.bikmukhametov.feature.reading.api.repository.ReadingRepository
import ru.kazan.itis.bikmukhametov.feature.reading.api.usecase.SaveLineSpacingUseCase
import javax.inject.Inject

internal class SaveLineSpacingUseCaseImpl @Inject constructor(
    private val readingRepository: ReadingRepository
) : SaveLineSpacingUseCase {
    override suspend fun invoke(spacing: Int) {
        readingRepository.saveLineSpacing(spacing)
    }
}

