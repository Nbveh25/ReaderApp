package ru.kazan.itis.bikmukhametov.feature.books.api.usecase

import ru.kazan.itis.bikmukhametov.feature.books.api.model.BookModel

interface GetBooksUseCase {
    suspend operator fun invoke(): List<BookModel>
}

