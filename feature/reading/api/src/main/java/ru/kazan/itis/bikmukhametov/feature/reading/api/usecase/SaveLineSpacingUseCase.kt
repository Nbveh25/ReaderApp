package ru.kazan.itis.bikmukhametov.feature.reading.api.usecase

interface SaveLineSpacingUseCase {
    suspend operator fun invoke(spacing: Int)
}

