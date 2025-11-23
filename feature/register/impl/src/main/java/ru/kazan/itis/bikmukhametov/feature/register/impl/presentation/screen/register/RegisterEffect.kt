package ru.kazan.itis.bikmukhametov.feature.register.impl.presentation.screen.register

sealed interface RegisterEffect {
    data class ShowSnackbar(val message: String) : RegisterEffect
    object NavigateToBooks : RegisterEffect
}

