package ru.kazan.itis.bikmukhametov.feature.upload.impl.presentation.screen.upload

sealed interface UploadIntent {
    data class TitleChanged(val title: String) : UploadIntent
    data class AuthorChanged(val author: String) : UploadIntent
    object SelectFileClicked : UploadIntent
    data class FileSelected(val fileUri: String, val fileName: String? = null) : UploadIntent
    object UploadClicked : UploadIntent
    object RetryClicked : UploadIntent
}

