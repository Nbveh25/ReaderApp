package ru.kazan.itis.bikmukhametov.feature.auth.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.tasks.await
import ru.kazan.itis.bikmukhametov.feature.auth.api.repository.AuthRepository
import ru.kazan.itis.bikmukhametov.feature.auth.data.util.FirebaseErrorMapper
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class AuthRepositoryImpl @Inject constructor(
    private val firebaseAuth: FirebaseAuth
) : AuthRepository {

    override val currentUser: Any?
        get() = firebaseAuth.currentUser

    override suspend fun signInWithEmailAndPassword(email: String, password: String): Result<Unit> {
        return try {
            firebaseAuth.signInWithEmailAndPassword(email, password).await()
            Result.success(Unit)
        } catch (e: FirebaseAuthException) {
            Result.failure(Exception(FirebaseErrorMapper.mapError(e.errorCode)))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun signInWithGoogle(idToken: String): Result<Unit> {
        return try {
            val credential = GoogleAuthProvider.getCredential(idToken, null)
            firebaseAuth.signInWithCredential(credential).await()
            Result.success(Unit)
        } catch (e: FirebaseAuthException) {
            Result.failure(Exception(FirebaseErrorMapper.mapError(e.errorCode)))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override fun signOut() {
        firebaseAuth.signOut()
    }
}

