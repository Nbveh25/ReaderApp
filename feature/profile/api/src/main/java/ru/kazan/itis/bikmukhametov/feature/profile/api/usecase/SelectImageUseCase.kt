package ru.kazan.itis.bikmukhametov.feature.profile.api.usecase

import ru.kazan.itis.bikmukhametov.feature.profile.api.model.ImageModel

interface SelectImageUseCase {
    suspend operator fun invoke(imageUriString: String): Result<ImageModel>
}



