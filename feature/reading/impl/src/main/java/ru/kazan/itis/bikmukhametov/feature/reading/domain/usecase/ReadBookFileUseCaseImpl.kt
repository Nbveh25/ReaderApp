package ru.kazan.itis.bikmukhametov.feature.reading.domain.usecase

import ru.kazan.itis.bikmukhametov.feature.reading.api.repository.BookFileRepository
import ru.kazan.itis.bikmukhametov.feature.reading.api.usecase.ReadBookFileUseCase
import ru.kazan.itis.bikmukhametov.util.enum.BookFormat
import javax.inject.Inject

internal class ReadBookFileUseCaseImpl @Inject constructor(
    private val bookFileRepository: BookFileRepository
) : ReadBookFileUseCase {

    override suspend fun invoke(filePath: String, format: BookFormat): Result<String> {
        return bookFileRepository.readBookFile(filePath, format)
    }
}

