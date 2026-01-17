package ru.kazan.itis.bikmukhametov.feature.register.impl.domain.usecase

import ru.kazan.itis.bikmukhametov.feature.register.api.repository.RegisterRepository
import ru.kazan.itis.bikmukhametov.feature.register.api.usecase.RegisterUseCase
import javax.inject.Inject

internal class RegisterUseCaseImpl @Inject constructor(
    private val registerRepository: RegisterRepository
) : RegisterUseCase {

    override suspend fun invoke(email: String, password: String): Result<Unit> {
        return registerRepository.registerWithEmailAndPassword(email, password)
    }
}
