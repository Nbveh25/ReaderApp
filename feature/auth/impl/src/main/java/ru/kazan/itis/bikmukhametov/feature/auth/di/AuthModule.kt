package ru.kazan.itis.bikmukhametov.feature.auth.di

import com.google.firebase.auth.FirebaseAuth
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ru.kazan.itis.bikmukhametov.feature.auth.api.repository.AuthRepository
import ru.kazan.itis.bikmukhametov.feature.auth.api.usecase.GetCurrentUserUseCase
import ru.kazan.itis.bikmukhametov.feature.auth.api.util.InputValidator
import ru.kazan.itis.bikmukhametov.feature.auth.api.usecase.LoginUseCase
import ru.kazan.itis.bikmukhametov.feature.auth.data.repository.AuthRepositoryImpl
import ru.kazan.itis.bikmukhametov.feature.auth.domain.usecase.GetCurrentUserUseCaseImpl
import ru.kazan.itis.bikmukhametov.feature.auth.domain.usecase.LoginUseCaseImpl
import ru.kazan.itis.bikmukhametov.feature.auth.domain.validation.InputValidatorImpl
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal object AuthModule {

    @Provides
    @Singleton
    fun provideFirebaseAuth(): FirebaseAuth {
        return FirebaseAuth.getInstance()
    }

    @Provides
    @Singleton
    fun provideAuthRepository(firebaseAuth: FirebaseAuth): AuthRepository {
        return AuthRepositoryImpl(firebaseAuth)
    }

    @Provides
    @Singleton
    fun provideLoginUseCase(authRepository: AuthRepository): LoginUseCase {
        return LoginUseCaseImpl(authRepository)
    }

    @Provides
    @Singleton
    fun provideGetCurrentUserUseCase(authRepository: AuthRepository): GetCurrentUserUseCase {
        return GetCurrentUserUseCaseImpl(authRepository)
    }

    @Provides
    @Singleton
    fun provideInputValidator(): InputValidator {
        return InputValidatorImpl()
    }
}

