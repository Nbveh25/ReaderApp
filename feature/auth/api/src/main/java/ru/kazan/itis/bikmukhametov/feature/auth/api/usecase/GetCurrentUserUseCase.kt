package ru.kazan.itis.bikmukhametov.feature.auth.api.usecase

interface GetCurrentUserUseCase {

    suspend operator fun invoke(): Any?
}
