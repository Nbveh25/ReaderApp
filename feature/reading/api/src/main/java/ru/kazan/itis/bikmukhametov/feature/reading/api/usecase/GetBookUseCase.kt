package ru.kazan.itis.bikmukhametov.feature.reading.api.usecase

import ru.kazan.itis.bikmukhametov.model.BookModel

interface GetBookUseCase {
    suspend operator fun invoke(bookId: String): Result<BookModel>
}

