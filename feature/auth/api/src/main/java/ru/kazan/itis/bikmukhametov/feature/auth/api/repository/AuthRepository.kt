package ru.kazan.itis.bikmukhametov.feature.auth.api.repository


interface AuthRepository {

    val currentUser: Any?

    suspend fun signInWithEmailAndPassword(email: String, password: String): Result<Unit>

    suspend fun signInWithGoogle(idToken: String): Result<Unit>

    fun signOut()
}
