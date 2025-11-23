package ru.kazan.itis.bikmukhametov.feature.reading.domain.usecase

import ru.kazan.itis.bikmukhametov.feature.reading.api.model.ReadingSettingsModel
import ru.kazan.itis.bikmukhametov.feature.reading.api.repository.ReadingRepository
import ru.kazan.itis.bikmukhametov.feature.reading.api.usecase.GetReadingSettingsUseCase
import javax.inject.Inject

internal class GetReadingSettingsUseCaseImpl @Inject constructor(
    private val readingRepository: ReadingRepository
) : GetReadingSettingsUseCase {
    override suspend fun invoke(): ReadingSettingsModel {
        return ReadingSettingsModel(
            fontSize = readingRepository.getFontSize(),
            lineSpacing = readingRepository.getLineSpacing(),
            themeMode = readingRepository.getThemeMode()
        )
    }
}

