package ru.kazan.itis.bikmukhametov.feature.profile.impl.domain.usecase

import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Test
import ru.kazan.itis.bikmukhametov.core.resources.image.ImageResourceProvider
import java.io.IOException

internal class SelectImageUseCaseImplTest {

    private val testUriString: String = "content://media/external/images/media/123"

    private val provider = mockk<ImageResourceProvider>()
    private val useCase = SelectImageUseCaseImpl(provider)

    @Test
    fun `should return success when everything succeed`() = runTest {
        // GIVEN
        val fakeName = "test.jpg"
        val fakeImageBytes = byteArrayOf(1, 2, 3)
        val fakeInputStream = fakeImageBytes.inputStream()

        every { provider.openInputStream(testUriString) } returns fakeInputStream
        every { provider.getFileName(testUriString) } returns fakeName

        // WHEN
        val result = useCase.invoke(testUriString)

        // THEN
        assert(result.isSuccess)
        val model = result.getOrThrow()
        assert(model.fileName.equals(fakeName))
        assert(model.bytes.contentEquals(fakeImageBytes))
    }

    @Test
    fun `should return success with default name when file name is null`() = runTest {
        // GIVEN
        val bytes = byteArrayOf(1, 2)
        every { provider.openInputStream(any()) } returns bytes.inputStream()
        every { provider.getFileName(testUriString) } returns null

        // WHEN
        val result = useCase.invoke(testUriString)

        // THEN
        assert(result.isSuccess)
        val name = result.getOrThrow().fileName
        assert(name.startsWith("profile_photo_"))
        assert(name.endsWith(".jpg"))
    }

    @Test
    fun `should return failure when file is too big`() = runTest {
        // GIVEN
        val fakeLargeBytes = ByteArray(10 * 1024 * 1024 + 1)
        every { provider.openInputStream(any()) } returns fakeLargeBytes.inputStream()

        // WHEN
        val result = useCase.invoke(testUriString)

        // THEN
        assert(result.isFailure)
        assert(result.exceptionOrNull() is IOException)
        assert(result.exceptionOrNull()?.message?.contains("Файл слишком большой") == true)
    }

    @Test
    fun `should return failure when file dont exist`() = runTest {
        // GIVEN
        every { provider.openInputStream(any()) } returns null

        // WHEN
        val result = useCase.invoke(testUriString)

        // THEN
        assert(result.isFailure)
        assert(result.exceptionOrNull() is IOException)
        assert(result.exceptionOrNull()?.message =="Не удалось открыть файл")
    }

}
