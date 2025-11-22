package ru.kazan.itis.bikmukhametov.feature.reading.api.usecase

import ru.kazan.itis.bikmukhametov.util.enum.BookFormat

interface ReadBookFileUseCase {
    suspend operator fun invoke(filePath: String, format: BookFormat): Result<String>
}

