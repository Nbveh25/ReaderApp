package ru.kazan.itis.bikmukhametov.feature.auth.domain.usecase

import ru.kazan.itis.bikmukhametov.feature.auth.api.repository.AuthRepository
import ru.kazan.itis.bikmukhametov.feature.auth.api.usecase.GetCurrentUserUseCase
import javax.inject.Inject

class GetCurrentUserUseCaseImpl @Inject constructor(
    private val authRepository: AuthRepository
) : GetCurrentUserUseCase {

    override suspend fun invoke(): Any? {
        return authRepository.currentUser
    }
}

