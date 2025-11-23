package ru.kazan.itis.bikmukhametov.feature.profile.api.usecase

interface UpdateUserNameUseCase {
    suspend operator fun invoke(name: String): Result<Unit>
}

