package ru.kazan.itis.bikmukhametov.feature.reading.domain.usecase

import android.util.Log
import io.mockk.coEvery
import io.mockk.mockk
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import ru.kazan.itis.bikmukhametov.feature.books.api.repository.BookRepository
import ru.kazan.itis.bikmukhametov.model.BookModel
import ru.kazan.itis.bikmukhametov.util.enum.BookFormat

internal class GetBookUseCaseImplTest {

    private lateinit var bookRepository: BookRepository
    private lateinit var useCase: GetBookUseCaseImpl

    private val fakeBookId = "111"
    private val fakeBook = BookModel(
        id = "111",
        title = "Clean Architecture",
        author = "Robert Martin",
        coverUrl = "https://example.com/cover.jpg",
        format = BookFormat.EPUB,
        userId = "user_777",
        bucketName = "books-bucket",
        fileUrl = "https://storage.com/book.epub",
        isDownloaded = false,
        isAvailableInFirestore = true,
        localFilePath = null
    )
    private val expectedFakeBooks = listOf(fakeBook, fakeBook)

    @Before
    fun setUp() {
        bookRepository = mockk<BookRepository>()
        useCase = GetBookUseCaseImpl(bookRepository)
    }

    @Test
    fun `should return book when book is found`() = runTest {
        // GIVEN
        coEvery { bookRepository.getBooks() } returns Result.success(expectedFakeBooks)

        // WHEN
        val result = useCase(fakeBookId)

        // THEN
        assertTrue(result.getOrNull() == fakeBook)
    }

    @Test
    fun `should return null when book is not found`() = runTest {
        // GIVEN
        coEvery { bookRepository.getBooks() } returns Result.failure(Exception("Книга не найдена"))

        // WHEN
        val result = useCase(fakeBookId)

        // THEN
        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull()?.message == "Книга не найдена")
    }

}