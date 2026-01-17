package ru.kazan.itis.bikmukhametov.feature.reading.domain.usecase

import io.mockk.coEvery
import io.mockk.mockk
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import ru.kazan.itis.bikmukhametov.feature.reading.api.repository.BookFileRepository
import ru.kazan.itis.bikmukhametov.util.enum.BookFormat

internal class ReadBookFileUseCaseImplTest {

    private lateinit var bookFileRepository: BookFileRepository
    private lateinit var useCase: ReadBookFileUseCaseImpl

    @Before
    fun setUp() {
        bookFileRepository = mockk<BookFileRepository>()
        useCase = ReadBookFileUseCaseImpl(bookFileRepository)
    }

    @Test
    fun `should return book content if everything is fine`() = runTest {
        // GIVEN
        val fakeFilePath = "fake_file_path"
        val fakeBookContent = "fake_book_content"
        val fakeBookFormat = BookFormat.TXT
        coEvery { bookFileRepository.readBookFile(fakeFilePath, fakeBookFormat) } returns Result.success(fakeBookContent)


        // WHEN
        val result = useCase(fakeFilePath, fakeBookFormat)

        // THEN
        assertTrue(result.getOrNull().equals(fakeBookContent))

    }
}
