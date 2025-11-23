package ru.kazan.itis.bikmukhametov.feature.profile.api.usecase

/**
 * Use case для обновления имени пользователя.
 */
interface UpdateUserNameUseCase {
    suspend operator fun invoke(name: String): Result<Unit>
}

