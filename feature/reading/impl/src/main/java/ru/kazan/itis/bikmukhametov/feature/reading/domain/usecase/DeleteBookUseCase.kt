package ru.kazan.itis.bikmukhametov.feature.reading.domain.usecase

import ru.kazan.itis.bikmukhametov.feature.books.api.repository.BookRepository
import ru.kazan.itis.bikmukhametov.feature.reading.api.usecase.DeleteBookUseCase
import javax.inject.Inject

class DeleteBookUseCaseImpl @Inject constructor(
    private val bookRepository: BookRepository
) : DeleteBookUseCase {
    override suspend fun invoke(bookId: String): Result<Boolean> {
        return bookRepository.deleteBook(bookId)
    }
}

