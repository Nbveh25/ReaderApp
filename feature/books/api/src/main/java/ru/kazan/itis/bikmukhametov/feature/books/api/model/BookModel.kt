package ru.kazan.itis.bikmukhametov.feature.books.api.model

import ru.kazan.itis.bikmukhametov.feature.books.api.util.BookFormat

data class BookModel(
    val id: String,
    val title: String,
    val author: String,
    val coverUrl: String? = null,
    val format: BookFormat,
    val userId: String,
    val bucketName: String? = null,
    val fileUrl: String? = null,
    val isDownloaded: Boolean = false,
    val isAvailableInFirestore: Boolean = true,
    val localFilePath: String? = null
)

