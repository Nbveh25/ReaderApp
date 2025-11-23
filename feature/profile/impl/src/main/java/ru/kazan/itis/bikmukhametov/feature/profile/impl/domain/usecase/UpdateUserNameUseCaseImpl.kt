package ru.kazan.itis.bikmukhametov.feature.profile.impl.domain.usecase

import ru.kazan.itis.bikmukhametov.feature.profile.api.repository.ProfileRepository
import ru.kazan.itis.bikmukhametov.feature.profile.api.usecase.UpdateUserNameUseCase
import javax.inject.Inject

class UpdateUserNameUseCaseImpl @Inject constructor(
    private val profileRepository: ProfileRepository
) : UpdateUserNameUseCase {

    override suspend fun invoke(name: String): Result<Unit> {
        return profileRepository.updateUserName(name)
    }
}

