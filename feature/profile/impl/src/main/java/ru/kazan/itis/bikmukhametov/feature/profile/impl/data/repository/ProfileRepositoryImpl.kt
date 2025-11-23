package ru.kazan.itis.bikmukhametov.feature.profile.impl.data.repository

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import kotlinx.coroutines.tasks.await
import ru.kazan.itis.bikmukhametov.feature.profile.api.datasource.remote.AvatarUploader
import ru.kazan.itis.bikmukhametov.feature.profile.api.model.UserProfile
import ru.kazan.itis.bikmukhametov.feature.profile.api.repository.ProfileRepository
import java.io.InputStream
import javax.inject.Inject
import javax.inject.Singleton
import androidx.core.net.toUri

@Singleton
class ProfileRepositoryImpl @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val avatarUploader: AvatarUploader
) : ProfileRepository {

    override suspend fun getUserProfile(): Result<UserProfile> {
        return try {
            val user = firebaseAuth.currentUser
            if (user == null) {
                Result.failure(Exception("Пользователь не авторизован"))
            } else {
                val profile = UserProfile(
                    uid = user.uid,
                    name = user.displayName,
                    email = user.email,
                    phone = user.phoneNumber,
                    photoUrl = user.photoUrl?.toString()
                )
                Result.success(profile)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateUserName(name: String): Result<Unit> {
        return try {
            val user = firebaseAuth.currentUser
            if (user == null) {
                Result.failure(Exception("Пользователь не авторизован"))
            } else {
                val profileUpdate = UserProfileChangeRequest.Builder()
                    .setDisplayName(name)
                    .build()
                user.updateProfile(profileUpdate).await()
                Result.success(Unit)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun uploadProfilePhoto(
        inputStream: InputStream,
        fileName: String
    ): Result<String> {
        return try {
            val user = firebaseAuth.currentUser
            if (user == null) {
                Result.failure(Exception("Пользователь не авторизован"))
            } else {

                val uploadResult = avatarUploader.uploadAvatar(
                    inputStream = inputStream,
                    fileName = fileName,
                    userId = user.uid
                )

                Log.d("ProfileRepositoryImpl", "URL загруженного файла: ${uploadResult.getOrNull()}")
                
                if (uploadResult.isFailure) {
                    return uploadResult
                }
                
                val photoUrl = uploadResult.getOrNull() ?: return Result.failure(
                    Exception("Не удалось получить URL загруженного файла")
                )

                val profileUpdate = UserProfileChangeRequest.Builder()
                    .setPhotoUri(photoUrl.toUri())
                    .build()
                user.updateProfile(profileUpdate).await()
                
                Result.success(photoUrl)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override fun signOut() {
        firebaseAuth.signOut()
    }
}

