package ru.kazan.itis.bikmukhametov.feature.profile.api.usecase

import ru.kazan.itis.bikmukhametov.feature.profile.api.model.UserProfile

interface GetUserProfileUseCase {
    suspend operator fun invoke(): Result<UserProfile>
}

