package ru.kazan.itis.bikmukhametov.feature.profile.api.config

/**
 * Конфигурация для доступа к Yandex Cloud Object Storage (S3-совместимое хранилище).
 */
interface S3Config {
    /**
     * Access Key ID для аутентификации
     */
    val accessKeyId: String
    
    /**
     * Secret Access Key для аутентификации
     */
    val secretAccessKey: String
    
    /**
     * Регион Yandex Cloud (обычно "ru-central1")
     */
    val region: String
        get() = "ru-central1"
    
    /**
     * Имя бакета
     */
    val bucketName: String
        get() = "avitio-avatars"
    
    /**
     * Endpoint Yandex Cloud Object Storage
     */
    val endpoint: String
        get() = "https://storage.yandexcloud.net"
}

