package ru.kazan.itis.bikmukhametov.feature.reading.domain.usecase

import ru.kazan.itis.bikmukhametov.feature.books.api.repository.BookRepository
import ru.kazan.itis.bikmukhametov.feature.reading.api.usecase.GetBookUseCase
import ru.kazan.itis.bikmukhametov.model.BookModel
import javax.inject.Inject

class GetBookUseCaseImpl @Inject constructor(
    private val bookRepository: BookRepository,
) : GetBookUseCase {
    override suspend fun invoke(bookId: String): Result<BookModel> {
        return try {
            val books = bookRepository.getBooks()
            val book = books.find { it.id == bookId }
            if (book != null) {
                Result.success(book)
            } else {
                Result.failure(Exception("Книга не найдена"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

