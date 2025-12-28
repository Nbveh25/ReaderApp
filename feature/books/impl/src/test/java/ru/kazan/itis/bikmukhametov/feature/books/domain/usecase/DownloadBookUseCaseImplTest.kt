package ru.kazan.itis.bikmukhametov.feature.books.domain.usecase

import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.test.runTest
import org.junit.Test
import ru.kazan.itis.bikmukhametov.feature.books.api.datasource.local.LocalBookDataSource
import ru.kazan.itis.bikmukhametov.feature.books.api.datasource.remote.BookDownloader
import ru.kazan.itis.bikmukhametov.feature.books.data.util.FileStorageManager
import ru.kazan.itis.bikmukhametov.util.enum.BookFormat
import java.io.IOException

internal class DownloadBookUseCaseImplTest {
    private val bookDownloader: BookDownloader = mockk()
    private val fileStorageManager: FileStorageManager = mockk()
    private val localBookDataSource: LocalBookDataSource = mockk()

    private val useCase = DownloadBookUseCaseImpl(
        bookDownloader,
        fileStorageManager,
        localBookDataSource
    )

    private val testBookId = "123"
    private val testUrl = "https://example.com/book.epub"

    @Test
    fun `should return success if book is already downloaded`() = runTest {
        // GIVEN
        coEvery { localBookDataSource.isBookDownloaded(testBookId) } returns true

        // WHEN
        val result = useCase.invoke(testBookId, testUrl)

        // THEN
        assertTrue(result.isSuccess)
        coVerify(exactly = 0) { bookDownloader.downloadBook(any()) }
    }

    @Test
    fun `should return failure when download fails`() = runTest {
        // GIVEN
        coEvery { localBookDataSource.isBookDownloaded(any()) } returns false
        every { bookDownloader.getBookFormatFromUrl(any()) } returns BookFormat.EPUB
        coEvery { bookDownloader.downloadBook(testUrl) } returns Result.failure(IOException("No internet"))

        // WHEN
        val result = useCase.invoke(testBookId, testUrl)

        // THEN
        assertTrue(result.isFailure)
        assertEquals("No internet", result.exceptionOrNull()?.message)
        coVerify(exactly = 0) { fileStorageManager.saveBookFile(any(), any(), any()) }
    }

    @Test
    fun `should return success if everything is fine`() = runTest {
        // GIVEN
        val fakeBytes = byteArrayOf(1, 2, 3)
        coEvery { localBookDataSource.isBookDownloaded(any()) } returns true
        every { bookDownloader.getBookFormatFromUrl(any()) } returns BookFormat.EPUB
        coEvery { bookDownloader.downloadBook(testUrl) } returns Result.success(fakeBytes)
        coEvery { fileStorageManager.saveBookFile(any(), any(), any()) } returns true

        // WHEN
        val result = useCase.invoke(testBookId, testUrl)

        // THEN
        assertTrue(result.isSuccess)
        coVerify {
            fileStorageManager.saveBookFile(testBookId, fakeBytes, BookFormat.EPUB)
        }
    }

}
