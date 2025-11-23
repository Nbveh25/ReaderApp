package ru.kazan.itis.bikmukhametov.feature.upload.impl.domain.usecase

import ru.kazan.itis.bikmukhametov.feature.upload.api.datasource.local.LocalFileStorage
import ru.kazan.itis.bikmukhametov.feature.upload.api.usecase.SaveFileLocallyUseCase
import java.io.InputStream
import javax.inject.Inject

class SaveFileLocallyUseCaseImpl @Inject constructor(
    private val localFileStorage: LocalFileStorage
) : SaveFileLocallyUseCase {

    override suspend fun invoke(inputStream: InputStream, fileName: String): Result<String> {
        return localFileStorage.saveFileLocally(inputStream, fileName)
    }
}

