package ru.kazan.itis.bikmukhametov.core.network.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import ru.kazan.itis.bikmukhametov.core.network.config.S3Config
import ru.kazan.itis.bikmukhametov.core.network.config.S3ConfigImpl
import java.util.concurrent.TimeUnit
import javax.inject.Named
import javax.inject.Singleton

private const val NETWORK_TIMEOUT_SECONDS = 30L

@Module
@InstallIn(SingletonComponent::class)
internal object NetworkModule {

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        return OkHttpClient.Builder()
            .connectTimeout(NETWORK_TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .readTimeout(NETWORK_TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .writeTimeout(NETWORK_TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .build()
    }

    @Provides
    @Singleton
    @Named("avatars")
    fun provideAvatarsS3Config(): S3Config {
        return S3ConfigImpl(bucketName = "avitio-avatars")
    }

    @Provides
    @Singleton
    @Named("books")
    fun provideBooksS3Config(): S3Config {
        return S3ConfigImpl(bucketName = "my-ycs-library")
    }
}

