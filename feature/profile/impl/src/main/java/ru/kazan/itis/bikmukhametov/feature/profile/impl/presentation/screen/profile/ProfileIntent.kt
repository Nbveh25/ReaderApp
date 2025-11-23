package ru.kazan.itis.bikmukhametov.feature.profile.impl.presentation.screen.profile

import android.net.Uri

sealed interface ProfileIntent {
    object LoadProfile : ProfileIntent
    object PhotoClick : ProfileIntent
    data class PhotoSelected(val imageUri: Uri) : ProfileIntent
    data class NameChanged(val name: String) : ProfileIntent
    object UpdateNameClicked : ProfileIntent
    object LogoutClicked : ProfileIntent
    object RetryClicked : ProfileIntent
}

