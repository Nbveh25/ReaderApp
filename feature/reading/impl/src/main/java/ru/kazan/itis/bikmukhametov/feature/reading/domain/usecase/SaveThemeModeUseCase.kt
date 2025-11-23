package ru.kazan.itis.bikmukhametov.feature.reading.domain.usecase

import ru.kazan.itis.bikmukhametov.feature.reading.api.repository.ReadingRepository
import ru.kazan.itis.bikmukhametov.feature.reading.api.usecase.SaveThemeModeUseCase
import javax.inject.Inject

internal class SaveThemeModeUseCaseImpl @Inject constructor(
    private val readingRepository: ReadingRepository
) : SaveThemeModeUseCase {
    override suspend fun invoke(mode: Int) {
        readingRepository.saveThemeMode(mode)
    }
}

