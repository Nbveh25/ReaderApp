package ru.kazan.itis.bikmukhametov.feature.books.data.datasource.cache.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import ru.kazan.itis.bikmukhametov.util.enum.BookFormat

@Entity(tableName = "books")
data class BookEntity(
    @PrimaryKey
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
