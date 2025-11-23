package ru.kazan.itis.bikmukhametov.feature.upload.impl.presentation.screen.upload

sealed interface UploadEffect {
    data class ShowMessage(val message: String) : UploadEffect
    object OpenFilePicker : UploadEffect
    object NavigateToBooks : UploadEffect
}

