package ru.kazan.itis.bikmukhametov.feature.books.domain.usecase

import com.google.android.play.integrity.internal.b
import io.mockk.coEvery
import io.mockk.mockk
import junit.framework.TestCase.assertFalse
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import ru.kazan.itis.bikmukhametov.feature.books.api.datasource.local.LocalBookDataSource
import ru.kazan.itis.bikmukhametov.feature.books.api.repository.BookRepository

internal class DeleteBookByIdUseCaseTest {

    private lateinit var bookRepository: BookRepository
    private lateinit var useCase: DeleteBookByIdUseCase

    private val fakeBookId = "111"

    @Before
    fun setUp() {
        bookRepository = mockk()
        useCase = DeleteBookByIdUseCase(bookRepository)
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
    fun `should return false when data source fails to delete book`() = runTest {
        // GIVEN
        coEvery { bookRepository.deleteBook(fakeBookId) } returns Result.failure(Exception("Не удалось удалить книгу"))

        // WHEN
        val result = useCase(fakeBookId)

        // THEN
        assertTrue(result.isFailure)
        assert(result.exceptionOrNull()?.message == "Не удалось удалить книгу")
    }

}
