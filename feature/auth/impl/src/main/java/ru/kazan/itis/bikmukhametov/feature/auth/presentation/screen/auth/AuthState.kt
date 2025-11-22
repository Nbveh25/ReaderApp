package ru.kazan.itis.bikmukhametov.feature.auth.presentation.screen.auth

data class AuthState(
    val emailInput: String = "",
    
    val passwordInput: String = "",
    
    val isLoading: Boolean = false,
    
    val isButtonEnabled: Boolean = false,
    
    val isPasswordVisible: Boolean = false,
    
    val rememberMe: Boolean = false,
    
    val error: String? = null,
    
    val passwordError: String? = null,
    
    val isNetworkError: Boolean = false
)

