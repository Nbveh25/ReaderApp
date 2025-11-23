package ru.kazan.itis.bikmukhametov.feature.upload.impl.presentation.screen.upload

/**
 * Состояние UI экрана загрузки книги.
 */
data class UploadState(
    val title: String = "",
    val author: String = "",
    val selectedFileName: String? = null,
    val selectedFileUri: String? = null,
    val isUploading: Boolean = false,
    val uploadProgress: Float = 0f,
    val isSuccess: Boolean = false,
    val error: String? = null
) {

    val canUpload: Boolean
        get() = title.isNotBlank() &&
                author.isNotBlank() &&
                selectedFileName != null &&
                !isUploading &&
                !isSuccess
}

