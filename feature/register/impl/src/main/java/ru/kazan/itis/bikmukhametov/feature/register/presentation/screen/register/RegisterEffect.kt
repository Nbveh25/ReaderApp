package ru.kazan.itis.bikmukhametov.feature.register.presentation.screen.register

sealed interface RegisterEffect {
    data class ShowSnackbar(val message: String) : RegisterEffect
    object NavigateToBooks : RegisterEffect
}

