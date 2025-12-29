package ru.kazan.itis.bikmukhametov.feature.books.domain.usecase

import jakarta.inject.Inject
import ru.kazan.itis.bikmukhametov.feature.books.api.repository.BookRepository
import ru.kazan.itis.bikmukhametov.feature.books.api.usecase.DeleteBookByIdUseCase

class DeleteBookByIdUseCaseImpl @Inject constructor(
    private val bookRepository: BookRepository
): DeleteBookByIdUseCase {

    override suspend operator fun invoke(bookId: String): Result<Boolean> {
        return try {
            val deleted = bookRepository.deleteBook(bookId)
            if (deleted.getOrNull() == true) {
                Result.success(true)
            } else {
                Result.failure(Exception("Не удалось удалить книгу"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

}
