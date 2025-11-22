package ru.kazan.itis.bikmukhametov.feature.auth.api.usecase

interface LoginUseCase {
    suspend operator fun invoke(email: String, password: String): Result<Unit>
}

