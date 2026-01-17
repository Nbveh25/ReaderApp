package ru.kazan.itis.bikmukhametov.feature.books.domain.usecase

import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Test
import ru.kazan.itis.bikmukhametov.feature.books.api.repository.BookRepository
import ru.kazan.itis.bikmukhametov.model.BookModel
import ru.kazan.itis.bikmukhametov.util.enum.BookFormat

internal class GetBooksUseCaseImplTest {

    private val bookRepository: BookRepository = mockk()
    private val useCase = GetBooksUseCaseImpl(bookRepository)

    @Test
    fun `should return list of books when everything is fine`() = runTest {
        // GIVEN
        val expectedBooks = listOf(createFakeBook(id = "101", title = "Kotlin in Action"))
        coEvery { bookRepository.getBooks() } returns Result.success(expectedBooks)

        // WHEN
        val result = useCase.invoke()

        // THEN
        assert(result.isSuccess)
        assert(result.getOrNull() == expectedBooks)


    }

    private fun createFakeBook(
        id: String = "1",
        title: String = "Test Book",
        isDownloaded: Boolean = false
    ) = BookModel(
        id = id,
        title = title,
        author = "Author",
        coverUrl = "http://...",
        format = BookFormat.PDF,
        userId = "user123",
        bucketName = "books",
        fileUrl = "http://...",
        isDownloaded = isDownloaded,
        isAvailableInFirestore = true,
        localFilePath = null
    )
}
