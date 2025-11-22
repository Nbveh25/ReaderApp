package ru.kazan.itis.bikmukhametov.feature.reading.api.usecase

interface SaveThemeModeUseCase {
    suspend operator fun invoke(mode: Int)
}

