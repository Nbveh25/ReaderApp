package ru.kazan.itis.bikmukhametov.feature.profile.impl.domain.usecase

import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Test
import ru.kazan.itis.bikmukhametov.feature.profile.api.repository.ProfileRepository

internal class UpdateUserNameUseCaseImplTest {

    private val profileRepository: ProfileRepository = mockk()
    private val useCase = UpdateUserNameUseCaseImpl(profileRepository)

    private val name = "New Name"

    @Test
    fun `should return success when name is updated successfully`() = runTest {
        // GIVEN
        coEvery { profileRepository.updateUserName(name) } returns Result.success(Unit)

        // WHEN
        val result = useCase.invoke(name)

        // THEN
        assert(result.isSuccess)

    }

    @Test
    fun `should return failure when user equals null`() = runTest {
        // GIVEN
        val expectedError = Exception("Пользователь не авторизован")
        coEvery { profileRepository.updateUserName(name) } returns Result.failure(expectedError)

        // WHEN
        val result = useCase.invoke(name)

        // THEN
        assert(result.isFailure)
        assert(result.exceptionOrNull()?.message == "Пользователь не авторизован")

    }
}
