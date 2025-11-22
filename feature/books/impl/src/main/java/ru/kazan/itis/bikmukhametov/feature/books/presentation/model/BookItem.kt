package ru.kazan.itis.bikmukhametov.feature.books.presentation.model

/**
 * Модель книги для отображения в UI.
 */
data class BookItem(
    val id: String,
    val title: String,
    val author: String,
    val coverUrl: String? = null,
    val format: String? = null, // PDF, EPUB, TXT и т.д.
    val fileUrl: String? = null, // URL для скачивания из Yandex Cloud Storage
    val isDownloaded: Boolean = false,
    val isAvailableInFirestore: Boolean = true,
    val localFilePath: String? = null
)

