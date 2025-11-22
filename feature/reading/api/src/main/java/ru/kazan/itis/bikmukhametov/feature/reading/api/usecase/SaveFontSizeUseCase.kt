package ru.kazan.itis.bikmukhametov.feature.reading.api.usecase

interface SaveFontSizeUseCase {
    suspend operator fun invoke(size: Int)
}

