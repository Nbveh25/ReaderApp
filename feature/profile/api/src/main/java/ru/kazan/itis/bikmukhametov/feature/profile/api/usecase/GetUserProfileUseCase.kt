package ru.kazan.itis.bikmukhametov.feature.profile.api.usecase

import ru.kazan.itis.bikmukhametov.feature.profile.api.model.UserProfile

/**
 * Use case для получения профиля пользователя.
 */
interface GetUserProfileUseCase {
    suspend operator fun invoke(): Result<UserProfile>
}

