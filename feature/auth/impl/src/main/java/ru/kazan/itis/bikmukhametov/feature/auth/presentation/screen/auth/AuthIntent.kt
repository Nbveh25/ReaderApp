package ru.kazan.itis.bikmukhametov.feature.auth.presentation.screen.auth

sealed interface AuthIntent {

    data class EmailChanged(val email: String) : AuthIntent

    data class PasswordChanged(val password: String) : AuthIntent

    object LoginButtonClicked : AuthIntent

    object RegistrationButtonClicked : AuthIntent

    object TogglePasswordVisibility : AuthIntent

    object GoogleSignInButtonClicked : AuthIntent

    object RetryButtonClicked : AuthIntent

    data class RememberMeChanged(val isChecked: Boolean) : AuthIntent
}

