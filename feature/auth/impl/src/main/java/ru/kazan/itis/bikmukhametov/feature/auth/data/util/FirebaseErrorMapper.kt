package ru.kazan.itis.bikmukhametov.feature.auth.data.util

internal object FirebaseErrorMapper {
    fun mapError(errorCode: String): String {
        return when (errorCode) {
            "ERROR_INVALID_EMAIL" -> "Неверный формат email"
            "ERROR_WRONG_PASSWORD" -> "Неверный пароль"
            "ERROR_USER_NOT_FOUND" -> "Пользователь с таким email не найден"
            "ERROR_USER_DISABLED" -> "Аккаунт пользователя отключен"
            "ERROR_TOO_MANY_REQUESTS" -> "Слишком много попыток. Попробуйте позже"
            "ERROR_OPERATION_NOT_ALLOWED" -> "Операция не разрешена"
            "ERROR_EMAIL_ALREADY_IN_USE" -> "Email уже используется"
            "ERROR_WEAK_PASSWORD" -> "Пароль слишком слабый"
            "ERROR_NETWORK_REQUEST_FAILED" -> "Ошибка сети. Проверьте подключение к интернету"
            "ERROR_INTERNAL_ERROR" -> "Внутренняя ошибка сервера"
            else -> "Ошибка входа: $errorCode"
        }
    }
}

