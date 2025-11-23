package ru.kazan.itis.bikmukhametov.feature.upload.impl.di

import android.content.Context
import com.google.firebase.firestore.FirebaseFirestore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import ru.kazan.itis.bikmukhametov.core.network.config.S3Config
import ru.kazan.itis.bikmukhametov.feature.upload.api.datasource.local.LocalFileStorage
import ru.kazan.itis.bikmukhametov.feature.upload.api.datasource.remote.BookUploader
import ru.kazan.itis.bikmukhametov.feature.upload.api.repository.BookRepository
import ru.kazan.itis.bikmukhametov.feature.upload.api.usecase.ReadFileUseCase
import ru.kazan.itis.bikmukhametov.feature.upload.api.usecase.SaveFileLocallyUseCase
import ru.kazan.itis.bikmukhametov.feature.upload.api.usecase.UploadBookUseCase
import ru.kazan.itis.bikmukhametov.feature.upload.api.usecase.ValidateBookFileUseCase
import ru.kazan.itis.bikmukhametov.feature.upload.impl.data.datasource.local.LocalFileStorageImpl
import ru.kazan.itis.bikmukhametov.feature.upload.impl.data.datasource.remote.BookUploaderImpl
import ru.kazan.itis.bikmukhametov.feature.upload.impl.data.repository.BookRepositoryImpl
import ru.kazan.itis.bikmukhametov.feature.upload.impl.domain.usecase.ReadFileUseCaseImpl
import ru.kazan.itis.bikmukhametov.feature.upload.impl.domain.usecase.SaveFileLocallyUseCaseImpl
import ru.kazan.itis.bikmukhametov.feature.upload.impl.domain.usecase.UploadBookUseCaseImpl
import ru.kazan.itis.bikmukhametov.feature.upload.impl.domain.usecase.ValidateBookFileUseCaseImpl
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object UploadModule {

    @Provides
    @Singleton
    fun provideBookUploader(
        @Named("books") s3Config: S3Config
    ): BookUploader {
        return BookUploaderImpl(s3Config)
    }

    @Provides
    @Singleton
    fun provideBookRepository(
        firestore: FirebaseFirestore
    ): BookRepository {
        return BookRepositoryImpl(firestore)
    }

    @Provides
    @Singleton
    fun provideLocalFileStorage(
        @ApplicationContext context: Context
    ): LocalFileStorage {
        return LocalFileStorageImpl(context)
    }

    @Provides
    @Singleton
    fun provideValidateBookFileUseCase(): ValidateBookFileUseCase {
        return ValidateBookFileUseCaseImpl()
    }

    @Provides
    @Singleton
    fun provideReadFileUseCase(
        readFileUseCaseImpl: ReadFileUseCaseImpl
    ): ReadFileUseCase {
        return readFileUseCaseImpl
    }

    @Provides
    @Singleton
    fun provideSaveFileLocallyUseCase(
        localFileStorage: LocalFileStorage
    ): SaveFileLocallyUseCase {
        return SaveFileLocallyUseCaseImpl(localFileStorage)
    }

    @Provides
    @Singleton
    fun provideUploadBookUseCase(
        bookUploader: BookUploader,
        bookRepository: BookRepository
    ): UploadBookUseCase {
        return UploadBookUseCaseImpl(bookUploader, bookRepository)
    }
}

