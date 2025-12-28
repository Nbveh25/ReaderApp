package ru.kazan.itis.bikmukhametov.feature.reading.domain.usecase

import io.mockk.coEvery
import io.mockk.mockk
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import ru.kazan.itis.bikmukhametov.feature.books.api.repository.BookRepository

internal class DeleteBookUseCaseImplTest {

    private lateinit var bookRepository: BookRepository
    private lateinit var useCase: DeleteBookUseCaseImpl

    private val fakeBookId = "111"

    @Before
    fun setUp() {
        bookRepository = mockk()
        useCase = DeleteBookUseCaseImpl(bookRepository)
    }

    @Test
    fun `should return true when book is deleted successfully`() = runTest {
        // GIVEN
        coEvery { bookRepository.deleteBook(fakeBookId) } returns Result.success(true)

        // WHEN
        val result = useCase(fakeBookId)

        // THEN
        assertTrue(result.isSuccess)
    }

    @Test
    fun `should return false when bookRepository fails to delete book`() = runTest {
        // GIVEN
        coEvery { bookRepository.deleteBook(fakeBookId) } returns Result.failure(Exception("Не удалось удалить книгу"))

        // WHEN
        val result = useCase(fakeBookId)

        // THEN
        assertTrue(result.isFailure)
        assert(result.exceptionOrNull()?.message == "Не удалось удалить книгу")
    }

}
