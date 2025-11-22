package ru.kazan.itis.bikmukhametov.feature.reading.domain.usecase

import ru.kazan.itis.bikmukhametov.feature.reading.api.repository.ReadingRepository
import ru.kazan.itis.bikmukhametov.feature.reading.api.usecase.SaveFontSizeUseCase
import javax.inject.Inject

class SaveFontSizeUseCaseImpl @Inject constructor(
    private val readingRepository: ReadingRepository
) : SaveFontSizeUseCase {
    override suspend fun invoke(size: Int) {
        readingRepository.saveFontSize(size)
    }
}

