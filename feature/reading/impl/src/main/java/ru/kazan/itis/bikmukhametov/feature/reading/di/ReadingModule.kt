package ru.kazan.itis.bikmukhametov.feature.reading.di

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import ru.kazan.itis.bikmukhametov.feature.reading.api.data.preferences.ReadingPreferences
import ru.kazan.itis.bikmukhametov.feature.reading.api.repository.BookFileRepository
import ru.kazan.itis.bikmukhametov.feature.reading.api.repository.ReadingRepository
import ru.kazan.itis.bikmukhametov.feature.reading.api.data.datasource.local.BookFileDataSource
import ru.kazan.itis.bikmukhametov.feature.reading.data.datasource.local.BookFileDataSourceImpl
import ru.kazan.itis.bikmukhametov.feature.reading.data.preferences.ReadingPreferencesImpl
import ru.kazan.itis.bikmukhametov.feature.reading.data.repository.BookFileRepositoryImpl
import ru.kazan.itis.bikmukhametov.feature.reading.data.repository.ReadingRepositoryImpl
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal object ReadingModule {

    @Provides
    @Singleton
    fun provideBookFileDataSource(): BookFileDataSource {
        return BookFileDataSourceImpl()
    }

    @Provides
    @Singleton
    fun provideBookFileRepository(
        bookFileDataSource: BookFileDataSource
    ): BookFileRepository {
        return BookFileRepositoryImpl(bookFileDataSource)
    }

    @Provides
    @Singleton
    fun provideReadingPreferences(
        @ApplicationContext context: Context
    ): ReadingPreferences {
        return ReadingPreferencesImpl(context)
    }

    @Provides
    @Singleton
    fun provideReadingRepository(
        readingPreferences: ReadingPreferences
    ): ReadingRepository {
        return ReadingRepositoryImpl(readingPreferences)
    }


}

