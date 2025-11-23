package ru.kazan.itis.bikmukhametov.feature.profile.impl.di

import com.google.firebase.auth.FirebaseAuth
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ru.kazan.itis.bikmukhametov.core.network.config.S3Config
import ru.kazan.itis.bikmukhametov.core.resources.ImageResourceProvider
import ru.kazan.itis.bikmukhametov.feature.profile.api.datasource.remote.AvatarUploader
import ru.kazan.itis.bikmukhametov.feature.profile.api.repository.ProfileRepository
import ru.kazan.itis.bikmukhametov.feature.profile.api.usecase.GetUserProfileUseCase
import ru.kazan.itis.bikmukhametov.feature.profile.api.usecase.SelectImageUseCase
import ru.kazan.itis.bikmukhametov.feature.profile.api.usecase.SignOutUseCase
import ru.kazan.itis.bikmukhametov.feature.profile.api.usecase.UpdateUserNameUseCase
import ru.kazan.itis.bikmukhametov.feature.profile.api.usecase.UploadProfilePhotoUseCase
import ru.kazan.itis.bikmukhametov.feature.profile.impl.data.datasource.remote.AvatarUploaderImpl
import ru.kazan.itis.bikmukhametov.feature.profile.impl.data.repository.ProfileRepositoryImpl
import ru.kazan.itis.bikmukhametov.feature.profile.impl.domain.usecase.GetUserProfileUseCaseImpl
import ru.kazan.itis.bikmukhametov.feature.profile.impl.domain.usecase.SelectImageUseCaseImpl
import ru.kazan.itis.bikmukhametov.feature.profile.impl.domain.usecase.SignOutUseCaseImpl
import ru.kazan.itis.bikmukhametov.feature.profile.impl.domain.usecase.UpdateUserNameUseCaseImpl
import ru.kazan.itis.bikmukhametov.feature.profile.impl.domain.usecase.UploadProfilePhotoUseCaseImpl
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ProfileModule {

    @Provides
    @Singleton
    fun provideAvatarUploader(
        @Named("avatars") s3Config: S3Config
    ): AvatarUploader {
        return AvatarUploaderImpl(s3Config)
    }

    @Provides
    @Singleton
    fun provideSelectImageUseCase(
        imageResourceProvider: ImageResourceProvider
    ): SelectImageUseCase {
        return SelectImageUseCaseImpl(imageResourceProvider)
    }

    @Provides
    @Singleton
    fun provideProfileRepository(
        firebaseAuth: FirebaseAuth,
        avatarUploader: AvatarUploader
    ): ProfileRepository {
        return ProfileRepositoryImpl(firebaseAuth, avatarUploader)
    }

    @Provides
    @Singleton
    fun provideGetUserProfileUseCase(
        profileRepository: ProfileRepository
    ): GetUserProfileUseCase {
        return GetUserProfileUseCaseImpl(profileRepository)
    }

    @Provides
    @Singleton
    fun provideUpdateUserNameUseCase(
        profileRepository: ProfileRepository
    ): UpdateUserNameUseCase {
        return UpdateUserNameUseCaseImpl(profileRepository)
    }

    @Provides
    @Singleton
    fun provideUploadProfilePhotoUseCase(
        profileRepository: ProfileRepository
    ): UploadProfilePhotoUseCase {
        return UploadProfilePhotoUseCaseImpl(profileRepository)
    }

    @Provides
    @Singleton
    fun provideSignOutUseCase(
        profileRepository: ProfileRepository
    ): SignOutUseCase {
        return SignOutUseCaseImpl(profileRepository)
    }
}
