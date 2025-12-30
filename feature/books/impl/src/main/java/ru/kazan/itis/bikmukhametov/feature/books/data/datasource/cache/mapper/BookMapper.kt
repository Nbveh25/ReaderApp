package ru.kazan.itis.bikmukhametov.feature.books.data.datasource.cache.mapper

import ru.kazan.itis.bikmukhametov.feature.books.data.datasource.cache.entity.BookEntity
import ru.kazan.itis.bikmukhametov.model.BookModel

// Из модели в Entity (для сохранения в БД)
fun BookModel.toEntity(): BookEntity {
    return BookEntity(
        id = id,
        title = title,
        author = author,
        coverUrl = coverUrl,
        format = format,
        userId = userId,
        bucketName = bucketName,
        fileUrl = fileUrl,
        isDownloaded = isDownloaded,
        isAvailableInFirestore = isAvailableInFirestore,
        localFilePath = localFilePath
    )
}

// Из Entity в модель (для использования в приложении)
fun BookEntity.toModel(): BookModel {
    return BookModel(
        id = id,
        title = title,
        author = author,
        coverUrl = coverUrl,
        format = format,
        userId = userId,
        bucketName = bucketName,
        fileUrl = fileUrl,
        isDownloaded = isDownloaded,
        isAvailableInFirestore = isAvailableInFirestore,
        localFilePath = localFilePath
    )
}