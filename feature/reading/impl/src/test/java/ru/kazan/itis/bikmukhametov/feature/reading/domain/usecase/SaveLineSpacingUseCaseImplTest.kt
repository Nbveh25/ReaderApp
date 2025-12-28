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

class SaveLineSpacingUseCaseImplTest {

    private lateinit var readingRepository: ReadingRepository
    private lateinit var useCase: SaveLineSpacingUseCaseImpl

    @Before
    fun setUp() {
        readingRepository = mockk<ReadingRepository>()
        useCase = SaveLineSpacingUseCaseImpl(readingRepository)
    }

    @Test
    fun `should save line spacing if everything is fine`() = runTest {
        // GIVEN
        val fakeSpacing = 2
        coEvery { readingRepository.saveLineSpacing(fakeSpacing) } just runs

        // WHEN
        val result = useCase(fakeSpacing)

        // THEN
        coVerify(exactly = 1) { readingRepository.saveLineSpacing(fakeSpacing) }
    }
}
