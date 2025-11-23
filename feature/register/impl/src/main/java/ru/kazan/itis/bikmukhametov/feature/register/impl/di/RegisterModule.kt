package ru.kazan.itis.bikmukhametov.feature.register.impl.di

import com.google.firebase.auth.FirebaseAuth
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ru.kazan.itis.bikmukhametov.feature.register.api.repository.RegisterRepository
import ru.kazan.itis.bikmukhametov.feature.register.api.usecase.RegisterUseCase
import ru.kazan.itis.bikmukhametov.feature.register.impl.data.repository.RegisterRepositoryImpl
import ru.kazan.itis.bikmukhametov.feature.register.impl.domain.usecase.RegisterUseCaseImpl
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

