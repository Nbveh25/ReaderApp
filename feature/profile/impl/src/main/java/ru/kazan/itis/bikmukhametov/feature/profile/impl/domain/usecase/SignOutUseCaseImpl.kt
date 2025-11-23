package ru.kazan.itis.bikmukhametov.feature.profile.impl.domain.usecase

import ru.kazan.itis.bikmukhametov.feature.profile.api.repository.ProfileRepository
import ru.kazan.itis.bikmukhametov.feature.profile.api.usecase.SignOutUseCase
import javax.inject.Inject

class SignOutUseCaseImpl @Inject constructor(
    private val profileRepository: ProfileRepository
) : SignOutUseCase {

    override fun invoke() {
        profileRepository.signOut()
    }
}

