package ru.kazan.itis.bikmukhametov.feature.reading.api.usecase

import ru.kazan.itis.bikmukhametov.feature.reading.api.model.ReadingSettingsModel

interface GetReadingSettingsUseCase {
    suspend operator fun invoke(): ReadingSettingsModel
}

