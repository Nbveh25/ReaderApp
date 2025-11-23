package ru.kazan.itis.bikmukhametov.feature.register.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import kotlinx.coroutines.tasks.await
import ru.kazan.itis.bikmukhametov.core.firebase.util.FirebaseErrorMapper
import ru.kazan.itis.bikmukhametov.feature.register.api.repository.RegisterRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class RegisterRepositoryImpl @Inject constructor(
    private val firebaseAuth: FirebaseAuth
) : RegisterRepository {

    override suspend fun registerWithEmailAndPassword(
        email: String,
        password: String
    ): Result<Unit> {
        return try {
            firebaseAuth.createUserWithEmailAndPassword(email, password).await()
            Result.success(Unit)
        } catch (e: FirebaseAuthException) {
            Result.failure(Exception(FirebaseErrorMapper.mapError(e.errorCode)))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

