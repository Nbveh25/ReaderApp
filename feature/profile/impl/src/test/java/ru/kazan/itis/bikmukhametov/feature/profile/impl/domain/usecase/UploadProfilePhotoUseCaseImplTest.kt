package ru.kazan.itis.bikmukhametov.feature.profile.impl.domain.usecase

import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Test
import ru.kazan.itis.bikmukhametov.feature.profile.api.repository.ProfileRepository
import java.io.InputStream

internal class UploadProfilePhotoUseCaseImplTest {

    private val profileRepository: ProfileRepository = mockk()
    private val useCase = UploadProfilePhotoUseCaseImpl(profileRepository)

    private val fakeInputStream = mockk<InputStream>()
    private val fakeFileName = "test.jpg"
    private val fakePhotoUrl = "https://example.com/photo.jpg"

    @Test
    fun `should return success when upload is successful`() = runTest {

        // GIVEN
        coEvery { profileRepository.uploadProfilePhoto(fakeInputStream, fakeFileName) } returns Result.success(fakePhotoUrl)

        // WHEN
        val result = useCase.invoke(fakeInputStream, fakeFileName)

        // THEN
        assert(result.isSuccess)
        val photoUrl = result.getOrThrow()
        assert(photoUrl == fakePhotoUrl)
    }

    @Test
    fun `should return failure when user is null`() = runTest {

        // GIVEN
        val expectedError = Exception("Пользователь не авторизован")
        coEvery { profileRepository.uploadProfilePhoto(fakeInputStream, fakeFileName) } returns Result.failure(expectedError)

        // WHEN
        val result = useCase.invoke(fakeInputStream, fakeFileName)

        // THEN
        assert(result.isFailure)
        assert(result.exceptionOrNull()?.message == "Пользователь не авторизован")

    }

}
