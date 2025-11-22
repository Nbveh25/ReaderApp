package ru.kazan.itis.bikmukhametov.feature.auth.presentation.screen.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ru.kazan.itis.bikmukhametov.feature.auth.api.usecase.GetCurrentUserUseCase
import ru.kazan.itis.bikmukhametov.feature.auth.api.util.InputValidator
import ru.kazan.itis.bikmukhametov.feature.auth.api.usecase.LoginUseCase
import java.io.IOException
import java.net.UnknownHostException
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val inputValidator: InputValidator,
    private val loginUseCase: LoginUseCase,
    private val getCurrentUserUseCase: GetCurrentUserUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(AuthState())
    val uiState: StateFlow<AuthState> = _uiState.asStateFlow()

    private val _effect = MutableSharedFlow<AuthEffect>(extraBufferCapacity = 1)
    val effect: SharedFlow<AuthEffect> = _effect.asSharedFlow()

    init {
        checkAutoLogin()
    }

    fun onIntent(intent: AuthIntent) {
        when (intent) {
            is AuthIntent.EmailChanged -> handleEmailChanged(intent.email)
            is AuthIntent.PasswordChanged -> handlePasswordChanged(intent.password)
            is AuthIntent.TogglePasswordVisibility -> togglePasswordVisibility()
            is AuthIntent.RememberMeChanged -> handleRememberMeChanged(intent.isChecked)
            AuthIntent.LoginButtonClicked -> handleLogin()
            AuthIntent.RegistrationButtonClicked -> navigateToRegistration()
            AuthIntent.GoogleSignInButtonClicked -> startGoogleSignIn()
            AuthIntent.RetryButtonClicked -> handleRetry()
        }
    }

    private fun checkAutoLogin() {
        viewModelScope.launch {
            val currentUser = getCurrentUserUseCase.invoke()
            if (currentUser != null) {
                _effect.emit(AuthEffect.NavigateToHome)
            }
        }
    }

    private fun handleEmailChanged(email: String) {
        val isPasswordValid = inputValidator
            .validatePassword(_uiState.value.passwordInput) is InputValidator.ValidationResult.Success

        _uiState.update { state ->
            state.copy(
                emailInput = email,
                error = null,
                isButtonEnabled = inputValidator.isValidEmail(email) && isPasswordValid
            )
        }
    }

    private fun handlePasswordChanged(password: String) {
        val validationResult = inputValidator.validatePassword(password)
        val isPasswordValid = validationResult is InputValidator.ValidationResult.Success
        val passwordError = (validationResult as? InputValidator.ValidationResult.Failure)?.message

        _uiState.update { state ->
            state.copy(
                passwordInput = password,
                error = null,
                passwordError = passwordError,
                isButtonEnabled = inputValidator.isValidEmail(state.emailInput) && isPasswordValid
            )
        }
    }

    private fun togglePasswordVisibility() {
        _uiState.update { it.copy(isPasswordVisible = !it.isPasswordVisible) }
    }

    private fun handleLogin() {
        if (!_uiState.value.isButtonEnabled) {
            viewModelScope.launch {
                _effect.tryEmit(AuthEffect.ShowSnackbar("Проверьте корректность Email и Пароля"))
            }
            return
        }

        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isLoading = true,
                    error = null,
                    isNetworkError = false
                )
            }

            try {
                loginUseCase.invoke(
                    _uiState.value.emailInput,
                    _uiState.value.passwordInput
                ).onSuccess {
                    _effect.emit(AuthEffect.ShowSnackbar("Успешный вход"))
                    _effect.emit(AuthEffect.NavigateToHome)
                }.onFailure { error ->
                    handleLoginError(error)
                }
            } catch (e: Exception) {
                handleLoginError(e)
            } finally {
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }

    private fun handleLoginError(error: Throwable) {
        val isNetworkError = error is IOException ||
                error is UnknownHostException ||
                error.message?.contains("сеть", ignoreCase = true) == true ||
                error.message?.contains("network", ignoreCase = true) == true ||
                error.message?.contains("интернет", ignoreCase = true) == true

        val errorMsg = when {
            isNetworkError -> "Ошибка сети. Проверьте подключение к интернету"
            else -> error.message ?: "Ошибка входа"
        }

        _uiState.update {
            it.copy(
                error = errorMsg,
                isNetworkError = isNetworkError
            )
        }

        viewModelScope.launch {
            _effect.emit(AuthEffect.ShowSnackbar(errorMsg))
        }
    }

    private fun handleRememberMeChanged(isChecked: Boolean) {
        _uiState.update { it.copy(rememberMe = isChecked) }
    }

    private fun handleRetry() {
        _uiState.update {
            it.copy(
                error = null,
                isNetworkError = false
            )
        }
        handleLogin()
    }

    private fun navigateToRegistration() {
        viewModelScope.launch { _effect.emit(AuthEffect.NavigateToRegistration) }
    }

    private fun startGoogleSignIn() {
        viewModelScope.launch { _effect.emit(AuthEffect.StartGoogleSignInFlow) }
    }
}

