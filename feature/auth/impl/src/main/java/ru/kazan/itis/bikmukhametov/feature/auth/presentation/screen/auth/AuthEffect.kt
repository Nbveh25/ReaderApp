package ru.kazan.itis.bikmukhametov.feature.auth.presentation.screen.auth

sealed interface AuthEffect {

    data class ShowSnackbar(val message: String) : AuthEffect

    object NavigateToHome : AuthEffect

    object NavigateToRegistration : AuthEffect

    object StartGoogleSignInFlow : AuthEffect
}

