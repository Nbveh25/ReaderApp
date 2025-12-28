package ru.kazan.itis.bikmukhametov.feature.books.domain.usecase

import ru.kazan.itis.bikmukhametov.feature.books.api.repository.BookRepository
import ru.kazan.itis.bikmukhametov.feature.books.api.usecase.GetBooksUseCase
import ru.kazan.itis.bikmukhametov.model.BookModel
import javax.inject.Inject

internal class GetBooksUseCaseImpl @Inject constructor(
    private val bookRepository: BookRepository
) : GetBooksUseCase {

    override suspend fun invoke(): List<BookModel> {
        return bookRepository.getBooks()
    }
}
