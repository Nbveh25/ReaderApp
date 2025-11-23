package ru.kazan.itis.bikmukhametov.feature.profile.impl.domain.usecase

import ru.kazan.itis.bikmukhametov.feature.profile.api.repository.ProfileRepository
import ru.kazan.itis.bikmukhametov.feature.profile.api.usecase.UploadProfilePhotoUseCase
import java.io.InputStream
import javax.inject.Inject

class UploadProfilePhotoUseCaseImpl @Inject constructor(
    private val profileRepository: ProfileRepository
) : UploadProfilePhotoUseCase {

    override suspend fun invoke(
        inputStream: InputStream,
        fileName: String
    ): Result<String> {
        return profileRepository.uploadProfilePhoto(inputStream, fileName)
    }
}

