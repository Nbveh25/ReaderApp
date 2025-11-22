package ru.kazan.itis.bikmukhametov.feature.auth.api.util

interface InputValidator {

    fun isValidEmail(email: String): Boolean

    fun validatePassword(password: String): ValidationResult

    sealed class ValidationResult {
        object Success : ValidationResult()
        data class Failure(val message: String) : ValidationResult()
    }
}