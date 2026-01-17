package ru.kazan.itis.bikmukhametov.core.network.config

import ru.kazan.itis.bikmukhametov.core.network.BuildConfig
import javax.inject.Inject

/**
 * Реализация S3Config.
 * Получает ключи из BuildConfig, которые настраиваются в build.gradle.kts
 * из local.properties или напрямую.
 */
internal class S3ConfigImpl @Inject constructor(
    override val bucketName: String
) : S3Config {

    override val accessKeyId: String
        get() = BuildConfig.S3_ACCESS_KEY_ID.takeIf { it.isNotBlank() }
            ?: error("S3_ACCESS_KEY_ID не настроен в BuildConfig")

    override val secretAccessKey: String
        get() = BuildConfig.S3_SECRET_ACCESS_KEY.takeIf { it.isNotBlank() }
            ?: error("S3_SECRET_ACCESS_KEY не настроен в BuildConfig")
}

