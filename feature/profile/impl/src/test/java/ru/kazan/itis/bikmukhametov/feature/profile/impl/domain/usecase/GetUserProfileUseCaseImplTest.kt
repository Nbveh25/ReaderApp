package ru.kazan.itis.bikmukhametov.feature.profile.impl.domain.usecase

import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test
import ru.kazan.itis.bikmukhametov.feature.profile.api.model.UserProfile
import ru.kazan.itis.bikmukhametov.feature.profile.api.repository.ProfileRepository

internal class GetUserProfileUseCaseImplTest {

    private val profileRepository: ProfileRepository = mockk()

    private val useCase = GetUserProfileUseCaseImpl(profileRepository)

    @Test
    fun `should return user profile when repository succeeds`() = runTest {
        // GIVEN
        val expectedProfile = UserProfile(
            uid = "111",
            name = "Megan",
            email = "fox",
            phone = "666",
            photoUrl = "photo"
        )
        coEvery { useCase.invoke() } returns Result.success(expectedProfile)

        // WHEN
        val result = useCase.invoke()

        // THEN
        assert(result.isSuccess)
        assert(result.getOrNull()?.equals(expectedProfile) ?: false)
    }

    @Test
    fun `should return failure when repository fails`() = runTest {

        // GIVEN
        val expectedError = Exception("Something went wrong")
        coEvery { useCase.invoke() } returns Result.failure(expectedError)

        // WHEN
        val result = useCase.invoke()

        // THEN
        assertEquals(result.exceptionOrNull(), expectedError)
        coVerify(exactly = 1) { profileRepository.getUserProfile() }
    }

}
