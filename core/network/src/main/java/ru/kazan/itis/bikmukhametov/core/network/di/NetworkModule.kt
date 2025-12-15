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

@Module
@InstallIn(SingletonComponent::class)
internal object NetworkModule {

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        return OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()
    }

    @Provides
    @Singleton
    fun provideRetrofit() {
        //return Retr
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

