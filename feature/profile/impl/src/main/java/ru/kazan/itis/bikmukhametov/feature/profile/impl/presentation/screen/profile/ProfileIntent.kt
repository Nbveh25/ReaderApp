package ru.kazan.itis.bikmukhametov.feature.profile.impl.presentation.screen.profile

sealed interface ProfileIntent {
    object LoadProfile : ProfileIntent
    object PhotoClick : ProfileIntent
    data class PhotoSelected(val inputStream: java.io.InputStream, val fileName: String) : ProfileIntent
    data class NameChanged(val name: String) : ProfileIntent
    object UpdateNameClicked : ProfileIntent
    object LogoutClicked : ProfileIntent
    object RetryClicked : ProfileIntent
}

