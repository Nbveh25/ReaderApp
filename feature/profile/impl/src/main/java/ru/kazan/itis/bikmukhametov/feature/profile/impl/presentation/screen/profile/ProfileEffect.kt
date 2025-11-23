package ru.kazan.itis.bikmukhametov.feature.profile.impl.presentation.screen.profile

sealed interface ProfileEffect {
    data class ShowMessage(val message: String) : ProfileEffect
    object NavigateToAuth : ProfileEffect
    object OpenImagePicker : ProfileEffect
}

