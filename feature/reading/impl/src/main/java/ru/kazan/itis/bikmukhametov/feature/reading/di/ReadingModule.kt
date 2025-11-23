package ru.kazan.itis.bikmukhametov.feature.reading.di

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import ru.kazan.itis.bikmukhametov.feature.books.api.repository.BookRepository
import ru.kazan.itis.bikmukhametov.feature.reading.api.data.preferences.ReadingPreferences
import ru.kazan.itis.bikmukhametov.feature.reading.api.repository.BookFileRepository
import ru.kazan.itis.bikmukhametov.feature.reading.api.repository.ReadingRepository
import ru.kazan.itis.bikmukhametov.feature.reading.api.usecase.DeleteBookUseCase
import ru.kazan.itis.bikmukhametov.feature.reading.api.usecase.GetBookUseCase
import ru.kazan.itis.bikmukhametov.feature.reading.api.usecase.GetReadingPositionUseCase
import ru.kazan.itis.bikmukhametov.feature.reading.api.usecase.GetReadingSettingsUseCase
import ru.kazan.itis.bikmukhametov.feature.reading.api.usecase.ReadBookFileUseCase
import ru.kazan.itis.bikmukhametov.feature.reading.api.usecase.SaveFontSizeUseCase
import ru.kazan.itis.bikmukhametov.feature.reading.api.usecase.SaveLineSpacingUseCase
import ru.kazan.itis.bikmukhametov.feature.reading.api.usecase.SaveReadingPositionUseCase
import ru.kazan.itis.bikmukhametov.feature.reading.api.usecase.SaveThemeModeUseCase
import ru.kazan.itis.bikmukhametov.feature.reading.api.data.datasource.local.BookFileDataSource
import ru.kazan.itis.bikmukhametov.feature.reading.data.datasource.local.BookFileDataSourceImpl
import ru.kazan.itis.bikmukhametov.feature.reading.data.preferences.ReadingPreferencesImpl
import ru.kazan.itis.bikmukhametov.feature.reading.data.repository.BookFileRepositoryImpl
import ru.kazan.itis.bikmukhametov.feature.reading.data.repository.ReadingRepositoryImpl
import ru.kazan.itis.bikmukhametov.feature.reading.domain.usecase.DeleteBookUseCaseImpl
import ru.kazan.itis.bikmukhametov.feature.reading.domain.usecase.GetBookUseCaseImpl
import ru.kazan.itis.bikmukhametov.feature.reading.domain.usecase.GetReadingPositionUseCaseImpl
import ru.kazan.itis.bikmukhametov.feature.reading.domain.usecase.GetReadingSettingsUseCaseImpl
import ru.kazan.itis.bikmukhametov.feature.reading.domain.usecase.ReadBookFileUseCaseImpl
import ru.kazan.itis.bikmukhametov.feature.reading.domain.usecase.SaveFontSizeUseCaseImpl
import ru.kazan.itis.bikmukhametov.feature.reading.domain.usecase.SaveLineSpacingUseCaseImpl
import ru.kazan.itis.bikmukhametov.feature.reading.domain.usecase.SaveReadingPositionUseCaseImpl
import ru.kazan.itis.bikmukhametov.feature.reading.domain.usecase.SaveThemeModeUseCaseImpl
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal object ReadingModule {

    @Provides
    @Singleton
    fun provideGetBookUseCase(
        bookRepository: BookRepository
    ): GetBookUseCase {
        return GetBookUseCaseImpl(bookRepository)
    }

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
    fun provideReadBookFileUseCase(
        bookFileRepository: BookFileRepository
    ): ReadBookFileUseCase {
        return ReadBookFileUseCaseImpl(bookFileRepository)
    }

    @Provides
    @Singleton
    fun provideDeleteBookUseCase(
        bookRepository: BookRepository
    ): DeleteBookUseCase {
        return DeleteBookUseCaseImpl(bookRepository)
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

    @Provides
    @Singleton
    fun provideGetReadingPositionUseCase(
        readingRepository: ReadingRepository
    ): GetReadingPositionUseCase {
        return GetReadingPositionUseCaseImpl(readingRepository)
    }

    @Provides
    @Singleton
    fun provideSaveReadingPositionUseCase(
        readingRepository: ReadingRepository
    ): SaveReadingPositionUseCase {
        return SaveReadingPositionUseCaseImpl(readingRepository)
    }

    @Provides
    @Singleton
    fun provideGetReadingSettingsUseCase(
        readingRepository: ReadingRepository
    ): GetReadingSettingsUseCase {
        return GetReadingSettingsUseCaseImpl(readingRepository)
    }

    @Provides
    @Singleton
    fun provideSaveFontSizeUseCase(
        readingRepository: ReadingRepository
    ): SaveFontSizeUseCase {
        return SaveFontSizeUseCaseImpl(readingRepository)
    }

    @Provides
    @Singleton
    fun provideSaveLineSpacingUseCase(
        readingRepository: ReadingRepository
    ): SaveLineSpacingUseCase {
        return SaveLineSpacingUseCaseImpl(readingRepository)
    }

    @Provides
    @Singleton
    fun provideSaveThemeModeUseCase(
        readingRepository: ReadingRepository
    ): SaveThemeModeUseCase {
        return SaveThemeModeUseCaseImpl(readingRepository)
    }
}

