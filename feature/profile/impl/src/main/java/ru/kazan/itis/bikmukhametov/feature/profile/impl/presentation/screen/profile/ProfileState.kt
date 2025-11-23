package ru.kazan.itis.bikmukhametov.feature.profile.impl.presentation.screen.profile

import ru.kazan.itis.bikmukhametov.feature.profile.api.model.UserProfile

data class ProfileState(
    val userProfile: UserProfile? = null,
    val isLoading: Boolean = false,
    val isUploadingPhoto: Boolean = false,
    val isUpdatingName: Boolean = false,
    val error: String? = null,
    val nameInput: String = ""
)

