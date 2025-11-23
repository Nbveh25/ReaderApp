package ru.kazan.itis.bikmukhametov.feature.profile.impl.domain.usecase

import ru.kazan.itis.bikmukhametov.feature.profile.api.model.UserProfile
import ru.kazan.itis.bikmukhametov.feature.profile.api.repository.ProfileRepository
import ru.kazan.itis.bikmukhametov.feature.profile.api.usecase.GetUserProfileUseCase
import javax.inject.Inject

internal class GetUserProfileUseCaseImpl @Inject constructor(
    private val profileRepository: ProfileRepository
) : GetUserProfileUseCase {

    override suspend fun invoke(): Result<UserProfile> {
        return profileRepository.getUserProfile()
    }
}

