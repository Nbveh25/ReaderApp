package ru.kazan.itis.bikmukhametov.feature.auth.domain.validation

import android.util.Patterns
import ru.kazan.itis.bikmukhametov.feature.auth.api.util.InputValidator
import javax.inject.Inject

class InputValidatorImpl @Inject constructor() : InputValidator {

    override fun isValidEmail(email: String): Boolean {
        return email.isNotBlank() && Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    override fun validatePassword(password: String): InputValidator.ValidationResult {
        val errors = mutableListOf<String>()

        if (password.length < MIN_PASSWORD_LENGTH) {
            errors.add("Минимум $MIN_PASSWORD_LENGTH символов")
        }

        if (!password.any { it.isUpperCase() }) {
            errors.add("Заглавная буква")
        }

        if (!password.any { it.isDigit() }) {
            errors.add("Цифра")
        }

        if (!password.any { it in SPECIAL_CHARACTERS }) {
            errors.add("Спецсимвол (!, @, # и т.д.)")
        }

        return if (errors.isEmpty()) {
            InputValidator.ValidationResult.Success
        } else {
            InputValidator.ValidationResult.Failure(errors.joinToString(", "))
        }
    }

    companion object {
        private const val MIN_PASSWORD_LENGTH = 8
        private const val MIN_LOGIN_PASSWORD_LENGTH = 6
        private const val SPECIAL_CHARACTERS = "!@#$%^&*()_+-="
    }
}

