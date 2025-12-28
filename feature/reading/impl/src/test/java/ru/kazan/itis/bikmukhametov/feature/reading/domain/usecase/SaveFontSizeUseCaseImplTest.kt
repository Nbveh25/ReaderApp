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

internal class SaveFontSizeUseCaseImplTest {

    private lateinit var readingRepository: ReadingRepository
    private lateinit var useCase: SaveFontSizeUseCaseImpl

    @Before
    fun setUp() {
        readingRepository = mockk<ReadingRepository>()
        useCase = SaveFontSizeUseCaseImpl(readingRepository)
    }


    @Test
    fun `should save font size if everything is fine`() = runTest {
        // GIVEN
        val fakeFontSize = 16
        coEvery { readingRepository.saveFontSize(fakeFontSize) } just runs


        // WHEN
        val result = useCase(fakeFontSize)

        // THEN
        coVerify(exactly = 1) { readingRepository.saveFontSize(fakeFontSize) }
    }

}