package ru.kazan.itis.bikmukhametov.feature.profile.api.model

data class UserProfile(
    val uid: String,
    val name: String?,
    val email: String?,
    val phone: String?,
    val photoUrl: String?
)

