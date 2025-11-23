package ru.kazan.itis.bikmukhametov.feature.profile.api.datasource.remote

import java.io.InputStream

/**
 * Интерфейс для загрузки аватаров в Yandex Cloud Object Storage.
 */
interface AvatarUploader {
    /**
     * Загружает аватар в бакет Yandex Cloud Object Storage.
     * 
     * @param inputStream Поток данных изображения.
     * @param fileName Имя файла.
     * @param userId ID пользователя для создания уникального пути.
     * @return URL загруженного файла.
     */
    suspend fun uploadAvatar(
        inputStream: InputStream,
        fileName: String,
        userId: String
    ): Result<String>
}

