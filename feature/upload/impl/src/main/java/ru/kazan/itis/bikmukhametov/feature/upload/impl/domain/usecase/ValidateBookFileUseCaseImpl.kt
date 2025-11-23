package ru.kazan.itis.bikmukhametov.feature.upload.impl.domain.usecase

import ru.kazan.itis.bikmukhametov.feature.upload.api.usecase.ValidateBookFileUseCase
import ru.kazan.itis.bikmukhametov.feature.upload.impl.util.BookFileValidator
import javax.inject.Inject

internal class ValidateBookFileUseCaseImpl @Inject constructor() : ValidateBookFileUseCase {

    override suspend fun invoke(fileName: String, fileSize: Long): Result<Unit> {
        return BookFileValidator.validate(fileName, fileSize)
    }
}

