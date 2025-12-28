package ru.kazan.itis.bikmukhametov.feature.profile.impl.domain.usecase

import io.mockk.mockk
import io.mockk.verify
import org.junit.Test
import ru.kazan.itis.bikmukhametov.feature.profile.api.repository.ProfileRepository

internal class SignOutUseCaseImplTest {

    private val profileRepository: ProfileRepository = mockk(relaxed = true)
    private val useCase = SignOutUseCaseImpl(profileRepository)

    @Test
    fun `should call signOut in repository when invoke is called`() {
        // GIVEN

        // WHEN
        useCase.invoke()

        // THEN
        verify(exactly = 1) { profileRepository.signOut() }

    }
}
