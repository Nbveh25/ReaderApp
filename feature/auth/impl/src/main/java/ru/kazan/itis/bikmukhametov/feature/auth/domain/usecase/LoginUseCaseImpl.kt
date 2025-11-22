package ru.kazan.itis.bikmukhametov.feature.auth.domain.usecase

import ru.kazan.itis.bikmukhametov.feature.auth.api.repository.AuthRepository
import ru.kazan.itis.bikmukhametov.feature.auth.api.usecase.LoginUseCase
import javax.inject.Inject

class LoginUseCaseImpl @Inject constructor(
    private val authRepository: AuthRepository
) : LoginUseCase {

    override suspend fun invoke(email: String, password: String): Result<Unit> {
        return authRepository.signInWithEmailAndPassword(email, password)
    }
}

