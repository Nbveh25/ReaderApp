package ru.kazan.itis.bikmukhametov.feature.profile.impl.config

import ru.kazan.itis.bikmukhametov.feature.profile.api.config.S3Config
import javax.inject.Inject
import javax.inject.Singleton

class S3ConfigImpl @Inject constructor() : S3Config {
    
    override val accessKeyId: String
        get() = try {
            val buildConfigClass = Class.forName("ru.kazan.itis.bikmukhametov.feature.profile.impl.BuildConfig")
            val field = buildConfigClass.getField("S3_ACCESS_KEY_ID")
            val value = field.get(null) as? String
            value?.takeIf { it.isNotBlank() } 
                ?: throw IllegalStateException("S3_ACCESS_KEY_ID не настроен в BuildConfig")
        } catch (e: ClassNotFoundException) {
            throw IllegalStateException("BuildConfig не найден. Убедитесь, что buildConfig = true в build.gradle.kts", e)
        } catch (e: NoSuchFieldException) {
            throw IllegalStateException("S3_ACCESS_KEY_ID не найден в BuildConfig. Добавьте buildConfigField в build.gradle.kts", e)
        }
    
    override val secretAccessKey: String
        get() = try {
            val buildConfigClass = Class.forName("ru.kazan.itis.bikmukhametov.feature.profile.impl.BuildConfig")
            val field = buildConfigClass.getField("S3_SECRET_ACCESS_KEY")
            val value = field.get(null) as? String
            value?.takeIf { it.isNotBlank() }
                ?: throw IllegalStateException("S3_SECRET_ACCESS_KEY не настроен в BuildConfig")
        } catch (e: ClassNotFoundException) {
            throw IllegalStateException("BuildConfig не найден. Убедитесь, что buildConfig = true в build.gradle.kts", e)
        } catch (e: NoSuchFieldException) {
            throw IllegalStateException("S3_SECRET_ACCESS_KEY не найден в BuildConfig. Добавьте buildConfigField в build.gradle.kts", e)
        }
}

