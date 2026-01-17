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

class SaveThemeModeUseCaseImplTest {

    private lateinit var readingRepository: ReadingRepository
    private lateinit var useCase: SaveThemeModeUseCaseImpl

    @Before
    fun setUp() {
        readingRepository = mockk<ReadingRepository>()
        useCase = SaveThemeModeUseCaseImpl(readingRepository)
    }

    @Test
    fun `should save theme mode if everything is fine`() = runTest {
        // GIVEN
        val fakeMode = 1
        coEvery { readingRepository.saveThemeMode(fakeMode) } just runs

        // WHEN
        useCase(fakeMode)

        // THEN
        coVerify(exactly = 1) { readingRepository.saveThemeMode(fakeMode) }

    }
}
