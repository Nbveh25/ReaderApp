package ru.kazan.itis.bikmukhametov.feature.reading.domain.usecase

import io.mockk.coEvery
import io.mockk.mockk
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import ru.kazan.itis.bikmukhametov.feature.reading.api.model.ReadingSettingsModel
import ru.kazan.itis.bikmukhametov.feature.reading.api.repository.ReadingRepository

internal class GetReadingSettingsUseCaseImplTest {

    private lateinit var readingRepository: ReadingRepository
    private lateinit var useCase: GetReadingSettingsUseCaseImpl

    @Before
    fun setUp() {
        readingRepository = mockk<ReadingRepository>()
        useCase = GetReadingSettingsUseCaseImpl(readingRepository)
    }

    @Test
    fun `should return ReadingSettingsModel if everything is fine`() = runTest {
        // GIVEN
        val expectedReadingSettingsModel = ReadingSettingsModel(
            fontSize = 16,
            lineSpacing = 2,
            themeMode = 1
        )
        coEvery { readingRepository.getFontSize() } returns 16
        coEvery { readingRepository.getLineSpacing() } returns 2
        coEvery { readingRepository.getThemeMode() } returns 1

        // WHEN
        val result = useCase()

        // THEN
        assertTrue(result == expectedReadingSettingsModel)
    }

}
