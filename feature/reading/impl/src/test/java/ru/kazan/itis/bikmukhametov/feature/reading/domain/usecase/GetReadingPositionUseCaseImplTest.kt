package ru.kazan.itis.bikmukhametov.feature.reading.domain.usecase

import io.mockk.coEvery
import io.mockk.mockk
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import ru.kazan.itis.bikmukhametov.feature.reading.api.repository.ReadingRepository
import ru.kazan.itis.bikmukhametov.feature.reading.api.usecase.GetReadingPositionUseCase

internal class GetReadingPositionUseCaseImplTest {

    private lateinit var readingRepository: ReadingRepository
    private lateinit var useCase: GetReadingPositionUseCase

    private val fakeBookId = "111"
    private val fakeReadingPosition = 234

    @Before
    fun setUp() {
        readingRepository = mockk<ReadingRepository>()
        useCase = GetReadingPositionUseCaseImpl(readingRepository)
    }

    @Test
    fun `should return reading position by bookID`() = runTest {
        // GIVEN
        coEvery { readingRepository.getReadingPosition(fakeBookId) } returns fakeReadingPosition
        
        // WHEN
        val result = useCase(fakeBookId)

        // THEN
        assertTrue(result == fakeReadingPosition)
    }

}
