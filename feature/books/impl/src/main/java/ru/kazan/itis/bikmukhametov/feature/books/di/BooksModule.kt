package ru.kazan.itis.bikmukhametov.feature.books.di

import android.content.Context
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
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
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object BooksModule {

    @Provides
    @Singleton
    fun provideFirebaseFirestore(): FirebaseFirestore {
        return FirebaseFirestore.getInstance()
    }

    @Provides
    @Singleton
    fun provideRemoteBookDataSource(
        firestore: FirebaseFirestore,
        firebaseAuth: FirebaseAuth
    ): RemoteBookDataSource {
        return RemoteBookDataSourceImpl(firestore, firebaseAuth)
    }

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
    fun provideBookRepository(
        remoteBookDataSource: RemoteBookDataSource,
        localBookDataSource: LocalBookDataSource
    ): BookRepository {
        return BookRepositoryImpl(remoteBookDataSource, localBookDataSource)
    }

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
