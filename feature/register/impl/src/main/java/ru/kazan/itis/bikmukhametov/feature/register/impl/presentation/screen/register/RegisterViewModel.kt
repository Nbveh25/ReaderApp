package ru.kazan.itis.bikmukhametov.feature.register.impl.presentation.screen.register

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
import ru.kazan.itis.bikmukhametov.feature.auth.api.util.InputValidator
import ru.kazan.itis.bikmukhametov.feature.register.api.usecase.RegisterUseCase
import ru.kazan.itis.bikmukhametov.feature.register.R
import ru.kazan.itis.bikmukhametov.core.resources.string.StringResourceProvider
import java.io.IOException
import javax.inject.Inject

@HiltViewModel
internal class RegisterViewModel @Inject constructor(
    private val inputValidator: InputValidator,
    private val registerUseCase: RegisterUseCase,
    private val stringResourceProvider: StringResourceProvider
) : ViewModel() {

    private val _uiState = MutableStateFlow(RegisterState())
    val uiState: StateFlow<RegisterState> = _uiState.asStateFlow()

    private val _effect = MutableSharedFlow<RegisterEffect>(extraBufferCapacity = 1)
    val effect: SharedFlow<RegisterEffect> = _effect.asSharedFlow()

    fun onIntent(intent: RegisterIntent) {
        when (intent) {
            is RegisterIntent.EmailChanged -> handleEmailChanged(intent.email)
            is RegisterIntent.PasswordChanged -> handlePasswordChanged(intent.password)
            is RegisterIntent.ConfirmPasswordChanged -> handleConfirmPasswordChanged(intent.confirmPassword)
            RegisterIntent.TogglePasswordVisibility -> togglePasswordVisibility()
            RegisterIntent.ToggleConfirmPasswordVisibility -> toggleConfirmPasswordVisibility()
            RegisterIntent.RegisterButtonClicked -> handleRegister()
            RegisterIntent.RetryButtonClicked -> handleRetry()
        }
    }

    private fun handleEmailChanged(email: String) {
        val emailError = if (email.isNotBlank() && !inputValidator.isValidEmail(email)) {
            stringResourceProvider.getString(R.string.register_error_invalid_email)
        } else null

        val isFormValid = validateForm(
            email = email,
            password = _uiState.value.passwordInput,
            confirmPassword = _uiState.value.confirmPasswordInput
        )

        _uiState.update { state ->
            state.copy(
                emailInput = email,
                emailError = emailError,
                error = null,
                isButtonEnabled = isFormValid && emailError == null
            )
        }
    }

    private fun handlePasswordChanged(password: String) {
        val validationResult = inputValidator.validatePassword(password)
        val passwordError = (validationResult as? InputValidator.ValidationResult.Failure)?.message

        val isFormValid = validateForm(
            email = _uiState.value.emailInput,
            password = password,
            confirmPassword = _uiState.value.confirmPasswordInput
        )

        _uiState.update { state ->
            state.copy(
                passwordInput = password,
                passwordError = passwordError,
                error = null,
                isButtonEnabled = isFormValid && passwordError == null
            )
        }

        // Проверяем совпадение паролей
        if (_uiState.value.confirmPasswordInput.isNotBlank()) {
            handleConfirmPasswordChanged(_uiState.value.confirmPasswordInput)
        }
    }

    private fun handleConfirmPasswordChanged(confirmPassword: String) {
        val confirmPasswordError = if (confirmPassword.isNotBlank() && 
            confirmPassword != _uiState.value.passwordInput) {
            stringResourceProvider.getString(R.string.register_error_passwords_not_match)
        } else null

        val isFormValid = validateForm(
            email = _uiState.value.emailInput,
            password = _uiState.value.passwordInput,
            confirmPassword = confirmPassword
        )

        _uiState.update { state ->
            state.copy(
                confirmPasswordInput = confirmPassword,
                confirmPasswordError = confirmPasswordError,
                error = null,
                isButtonEnabled = isFormValid && confirmPasswordError == null
            )
        }
    }

    private fun validateForm(email: String, password: String, confirmPassword: String): Boolean {
        val isEmailValid = inputValidator.isValidEmail(email)
        val isPasswordValid = inputValidator.validatePassword(password) is InputValidator.ValidationResult.Success
        val isConfirmPasswordValid = confirmPassword.isNotBlank() && confirmPassword == password

        return isEmailValid && isPasswordValid && isConfirmPasswordValid
    }

    private fun togglePasswordVisibility() {
        _uiState.update { it.copy(isPasswordVisible = !it.isPasswordVisible) }
    }

    private fun toggleConfirmPasswordVisibility() {
        _uiState.update { it.copy(isConfirmPasswordVisible = !it.isConfirmPasswordVisible) }
    }

    private fun handleRegister() {
        if (!_uiState.value.isButtonEnabled) {
            viewModelScope.launch {
                _effect.tryEmit(RegisterEffect.ShowSnackbar(
                    stringResourceProvider.getString(R.string.register_message_validate_fields)
                ))
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
                registerUseCase.invoke(
                    _uiState.value.emailInput,
                    _uiState.value.passwordInput
                ).onSuccess {
                    _effect.emit(RegisterEffect.ShowSnackbar(
                        stringResourceProvider.getString(R.string.register_message_success)
                    ))
                    _effect.emit(RegisterEffect.NavigateToBooks)
                }.onFailure { error ->
                    handleRegisterError(error)
                }
            } catch (e: Exception) {
                handleRegisterError(e)
            } finally {
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }

    private fun handleRegisterError(error: Throwable) {
        val isNetworkError = error is IOException

        val errorMsg = when {
            isNetworkError -> stringResourceProvider.getString(R.string.register_error_network)
            else -> error.message ?: stringResourceProvider.getString(R.string.register_error_register)
        }

        _uiState.update {
            it.copy(
                error = errorMsg,
                isNetworkError = isNetworkError
            )
        }

        viewModelScope.launch {
            _effect.emit(RegisterEffect.ShowSnackbar(errorMsg))
        }
    }

    private fun handleRetry() {
        _uiState.update {
            it.copy(
                error = null,
                isNetworkError = false
            )
        }
        handleRegister()
    }
}

