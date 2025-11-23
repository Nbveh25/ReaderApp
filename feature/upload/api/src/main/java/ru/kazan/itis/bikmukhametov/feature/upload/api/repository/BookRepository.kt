package ru.kazan.itis.bikmukhametov.feature.upload.api.repository

import ru.kazan.itis.bikmukhametov.feature.upload.api.model.BookMetadata

interface BookRepository {
    suspend fun saveBookMetadata(metadata: BookMetadata): Result<Unit>
}

