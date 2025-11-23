package ru.kazan.itis.bikmukhametov.core.resources.di

import android.content.ContentResolver
import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import ru.kazan.itis.bikmukhametov.core.resources.image.ImageResourceProvider
import ru.kazan.itis.bikmukhametov.core.resources.image.ImageResourceProviderImpl
import ru.kazan.itis.bikmukhametov.core.resources.string.StringResourceProvider
import ru.kazan.itis.bikmukhametov.core.resources.string.StringResourceProviderImpl
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal object ResourcesModule {

    @Provides
    @Singleton
    fun provideContentResolver(
        @ApplicationContext context: Context
    ): ContentResolver {
        return context.contentResolver
    }

    @Provides
    @Singleton
    fun provideImageResourceProvider(
        contentResolver: ContentResolver
    ): ImageResourceProvider {
        return ImageResourceProviderImpl(contentResolver)
    }

    @Provides
    @Singleton
    fun provideResourceProvider(
        @ApplicationContext context: Context
    ): StringResourceProvider {
        return StringResourceProviderImpl(context)
    }
}

