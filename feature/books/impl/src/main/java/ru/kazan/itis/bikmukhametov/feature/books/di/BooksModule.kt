package ru.kazan.itis.bikmukhametov.feature.books.di

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import ru.kazan.itis.bikmukhametov.feature.books.api.datasource.remote.BookDownloader
import ru.kazan.itis.bikmukhametov.feature.books.api.datasource.local.LocalBookDataSource
import ru.kazan.itis.bikmukhametov.feature.books.api.datasource.remote.RemoteBookDataSource
import ru.kazan.itis.bikmukhametov.feature.books.api.repository.BookRepository
import ru.kazan.itis.bikmukhametov.feature.books.api.usecase.DownloadBookUseCase
import ru.kazan.itis.bikmukhametov.feature.books.api.usecase.GetBooksUseCase
import ru.kazan.itis.bikmukhametov.feature.books.data.datasource.local.LocalBookDataSourceImpl
import ru.kazan.itis.bikmukhametov.feature.books.data.datasource.remote.BookDownloaderImpl
import ru.kazan.itis.bikmukhametov.feature.books.data.datasource.remote.RemoteBookDataSourceImpl
import ru.kazan.itis.bikmukhametov.feature.books.data.repository.BookRepositoryImpl
import ru.kazan.itis.bikmukhametov.feature.books.data.util.FileStorageManager
import ru.kazan.itis.bikmukhametov.feature.books.domain.usecase.DownloadBookUseCaseImpl
import ru.kazan.itis.bikmukhametov.feature.books.domain.usecase.GetBooksUseCaseImpl
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal object BooksModule {

    @Provides
    @Singleton
    fun provideFileStorageManager(
        @ApplicationContext context: Context
    ): FileStorageManager {
        return FileStorageManager(context)
    }

    @Provides
    @Singleton
    fun provideLocalBookDataSource(
        fileStorageManager: FileStorageManager
    ): LocalBookDataSource {
        return LocalBookDataSourceImpl(fileStorageManager)
    }

    @Provides
    @Singleton
    fun provideRemoteBookDataSource(
        remoteBookDataSourceImpl: RemoteBookDataSourceImpl
    ): RemoteBookDataSource {
        return remoteBookDataSourceImpl
    }

    @Provides
    @Singleton
    fun provideBookRepository(
        remoteBookDataSource: RemoteBookDataSource,
        localBookDataSource: LocalBookDataSource
    ): BookRepository {
        return BookRepositoryImpl(remoteBookDataSource, localBookDataSource)
    }

    @Provides
    @Singleton
    fun provideBookDownloader(
        okHttpClient: OkHttpClient
    ): BookDownloader {
        return BookDownloaderImpl(okHttpClient)
    }

    @Provides
    @Singleton
    fun provideGetBooksUseCase(
        bookRepository: BookRepository
    ): GetBooksUseCase {
        return GetBooksUseCaseImpl(bookRepository)
    }

    @Provides
    @Singleton
    fun provideDownloadBookUseCase(
        bookDownloader: BookDownloader,
        fileStorageManager: FileStorageManager,
        localBookDataSource: LocalBookDataSource
    ): DownloadBookUseCase {
        return DownloadBookUseCaseImpl(bookDownloader, fileStorageManager, localBookDataSource)
    }
}
