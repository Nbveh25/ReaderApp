package ru.kazan.itis.bikmukhametov.feature.reading.data.repository

import ru.kazan.itis.bikmukhametov.feature.reading.api.repository.BookFileRepository
import ru.kazan.itis.bikmukhametov.feature.reading.api.data.datasource.local.BookFileDataSource
import ru.kazan.itis.bikmukhametov.util.enum.BookFormat
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Реализация [BookFileRepository].
 */
@Singleton
internal class BookFileRepositoryImpl @Inject constructor(
    private val bookFileDataSource: BookFileDataSource
) : BookFileRepository {

    override suspend fun readBookFile(filePath: String, format: BookFormat): Result<String> {
        return try {
            val content = bookFileDataSource.readBookFile(filePath, format)
            Result.success(content)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

