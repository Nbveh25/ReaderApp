package ru.kazan.itis.bikmukhametov.feature.reading.domain.usecase

import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.just
import io.mockk.mockk
import io.mockk.runs
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import ru.kazan.itis.bikmukhametov.feature.reading.api.repository.ReadingRepository

class SaveReadingPositionUseCaseImplTest {

    private lateinit var readingRepository: ReadingRepository
    private lateinit var useCase: SaveReadingPositionUseCaseImpl

    @Before
    fun setUp() {
        readingRepository = mockk<ReadingRepository>()
        useCase = SaveReadingPositionUseCaseImpl(readingRepository)
    }

    @Test
    fun `should save reading position if everything is fine`() = runTest {
        // GIVEN
        val fakeBookId = "fakeBookId"
        val fakePosition = 100
        coEvery { readingRepository.saveReadingPosition(fakeBookId, fakePosition) } just runs

        // WHEN
        useCase(fakeBookId, fakePosition)

        // THEN
        coVerify(exactly = 1) { readingRepository.saveReadingPosition(fakeBookId, fakePosition) }

    }
}
