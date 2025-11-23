package ru.kazan.itis.bikmukhametov.feature.profile.api.repository

import ru.kazan.itis.bikmukhametov.feature.profile.api.model.UserProfile
import java.io.InputStream

/**
 * Репозиторий для работы с профилем пользователя.
 */
interface ProfileRepository {

    /**
     * Получить профиль текущего пользователя.
     */
    suspend fun getUserProfile(): Result<UserProfile>

    /**
     * Обновить имя пользователя.
     */
    suspend fun updateUserName(name: String): Result<Unit>

    /**
     * Загрузить фото профиля.
     * @param inputStream Поток данных изображения.
     * @param fileName Имя файла.
     * @return URL загруженного изображения.
     */
    suspend fun uploadProfilePhoto(
        inputStream: InputStream,
        fileName: String
    ): Result<String>

    /**
     * Выйти из аккаунта.
     */
    fun signOut()
}

