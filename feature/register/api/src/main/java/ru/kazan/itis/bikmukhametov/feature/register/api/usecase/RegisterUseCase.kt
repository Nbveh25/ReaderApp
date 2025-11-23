package ru.kazan.itis.bikmukhametov.feature.register.api.usecase

interface RegisterUseCase {
    suspend fun invoke(email: String, password: String): Result<Unit>
}

