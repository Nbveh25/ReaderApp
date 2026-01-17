package ru.kazan.itis.bikmukhametov.feature.books.api.usecase

interface DeleteBookByIdUseCase {
    suspend operator fun invoke(bookId: String): Result<Boolean>
}
