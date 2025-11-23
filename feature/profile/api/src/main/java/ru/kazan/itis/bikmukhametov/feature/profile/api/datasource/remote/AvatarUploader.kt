package ru.kazan.itis.bikmukhametov.feature.profile.api.datasource.remote

import java.io.InputStream

interface AvatarUploader {
    suspend fun uploadAvatar(
        inputStream: InputStream,
        fileName: String,
        userId: String
    ): Result<String>
}

