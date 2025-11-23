package ru.kazan.itis.bikmukhametov.feature.register.api.repository

interface RegisterRepository {
    suspend fun registerWithEmailAndPassword(
        email: String,
        password: String
    ): Result<Unit>
}

