package ru.kazan.itis.bikmukhametov.feature.register.di

import com.google.firebase.auth.FirebaseAuth
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ru.kazan.itis.bikmukhametov.feature.register.api.repository.RegisterRepository
import ru.kazan.itis.bikmukhametov.feature.register.api.usecase.RegisterUseCase
import ru.kazan.itis.bikmukhametov.feature.register.data.repository.RegisterRepositoryImpl
import ru.kazan.itis.bikmukhametov.feature.register.domain.usecase.RegisterUseCaseImpl
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal object RegisterModule {

    @Provides
    @Singleton
    fun provideRegisterRepository(firebaseAuth: FirebaseAuth): RegisterRepository {
        return RegisterRepositoryImpl(firebaseAuth)
    }

    @Provides
    @Singleton
    fun provideRegisterUseCase(registerRepository: RegisterRepository): RegisterUseCase {
        return RegisterUseCaseImpl(registerRepository)
    }
}

