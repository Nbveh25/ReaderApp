package ru.kazan.itis.bikmukhametov.feature.upload.api.model

data class BookMetadata(
    val title: String,
    val author: String,
    val fileUrl: String,
    val userId: String,
    val fileName: String,
    val fileSize: Long,
    val format: String // txt, epub, pdf
)

