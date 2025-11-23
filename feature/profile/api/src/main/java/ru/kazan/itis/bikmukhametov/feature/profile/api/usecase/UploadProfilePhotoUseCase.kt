package ru.kazan.itis.bikmukhametov.feature.profile.api.usecase

import java.io.InputStream

/**
 * Use case для загрузки фото профиля.
 */
interface UploadProfilePhotoUseCase {
    suspend operator fun invoke(
        inputStream: InputStream,
        fileName: String
    ): Result<String>
}

