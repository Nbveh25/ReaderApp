package ru.kazan.itis.bikmukhametov.feature.register.impl.presentation.screen.register

sealed interface RegisterIntent {
    data class EmailChanged(val email: String) : RegisterIntent
    data class PasswordChanged(val password: String) : RegisterIntent
    data class ConfirmPasswordChanged(val confirmPassword: String) : RegisterIntent
    object TogglePasswordVisibility : RegisterIntent
    object ToggleConfirmPasswordVisibility : RegisterIntent
    object RegisterButtonClicked : RegisterIntent
    object RetryButtonClicked : RegisterIntent
}

