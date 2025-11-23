package ru.kazan.itis.bikmukhametov.feature.profile.api.repository

import ru.kazan.itis.bikmukhametov.feature.profile.api.model.UserProfile
import java.io.InputStream

interface ProfileRepository {

    suspend fun getUserProfile(): Result<UserProfile>

    suspend fun updateUserName(name: String): Result<Unit>

    suspend fun uploadProfilePhoto(
        inputStream: InputStream,
        fileName: String
    ): Result<String>

    fun signOut()
}

