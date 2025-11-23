package ru.kazan.itis.bikmukhametov.feature.upload.impl.presentation.screen.upload

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import ru.kazan.itis.bikmukhametov.core.ui.appuicomponent.AppOutlinedButton
import ru.kazan.itis.bikmukhametov.core.ui.appuicomponent.AppPrimaryButton
import ru.kazan.itis.bikmukhametov.core.ui.appuicomponent.AppTextField
import ru.kazan.itis.bikmukhametov.core.ui.appuicomponent.AppTopBar

@Composable
fun UploadScreen(
    viewModel: UploadViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    val filePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { selectedUri ->
            viewModel.onIntent(
                UploadIntent.FileSelected(
                    fileUri = selectedUri.toString()
                )
            )
        }
    }

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is UploadEffect.ShowMessage -> {
                    snackbarHostState.showSnackbar(effect.message)
                }
                is UploadEffect.OpenFilePicker -> {
                    filePickerLauncher.launch("application/*")
                }
                is UploadEffect.NavigateToBooks -> {
                    // TODO: Реализовать навигацию после успешной загрузки
                }
            }
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            AppTopBar(
                title = "Загрузка книги"
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        UploadContent(
            state = uiState,
            onTitleChange = { viewModel.onIntent(UploadIntent.TitleChanged(it)) },
            onAuthorChange = { viewModel.onIntent(UploadIntent.AuthorChanged(it)) },
            onSelectFileClick = { viewModel.onIntent(UploadIntent.SelectFileClicked) },
            onUploadClick = { viewModel.onIntent(UploadIntent.UploadClicked) },
            onRetryClick = { viewModel.onIntent(UploadIntent.RetryClicked) },
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        )
    }
}

@Composable
private fun UploadContent(
    state: UploadState,
    onTitleChange: (String) -> Unit,
    onAuthorChange: (String) -> Unit,
    onSelectFileClick: () -> Unit,
    onUploadClick: () -> Unit,
    onRetryClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Поле для выбора файла
        FileSelectionCard(
            fileName = state.selectedFileName,
            onSelectFileClick = onSelectFileClick,
            enabled = !state.isUploading
        )

        // Поле ввода названия книги
        AppTextField(
            value = state.title,
            onValueChange = onTitleChange,
            label = "Название книги",
            enabled = !state.isUploading
        )

        // Поле ввода автора
        AppTextField(
            value = state.author,
            onValueChange = onAuthorChange,
            label = "Автор",
            enabled = !state.isUploading
        )

        // Прогресс загрузки
        if (state.isUploading) {
            UploadProgress(
                progress = state.uploadProgress,
                modifier = Modifier.fillMaxWidth()
            )
        }

        // Кнопка загрузки
        AppPrimaryButton(
            text = "Загрузить",
            onClick = onUploadClick,
            enabled = state.canUpload && !state.isUploading,
            isLoading = state.isUploading,
            modifier = Modifier.fillMaxWidth()
        )

        // Сообщение об успехе
        if (state.isSuccess) {
            SuccessMessage(
                message = "Книга успешно загружена",
                modifier = Modifier.fillMaxWidth()
            )
        }

        // Сообщение об ошибке
        if (state.error != null) {
            ErrorMessage(
                error = state.error,
                onRetryClick = onRetryClick,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
private fun FileSelectionCard(
    fileName: String?,
    onSelectFileClick: () -> Unit,
    enabled: Boolean,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            AppOutlinedButton(
                text = "Выбрать файл",
                onClick = onSelectFileClick,
                enabled = enabled,
                modifier = Modifier.fillMaxWidth()
            )

            if (fileName != null) {
                Text(
                    text = "Выбранный файл: $fileName",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            } else {
                Text(
                    text = "Файл не выбран",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                )
            }
        }
    }
}

@Composable
private fun UploadProgress(
    progress: Float,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = "Загрузка: ${(progress * 100).toInt()}%",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface
        )
        LinearProgressIndicator(
            progress = { progress },
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
private fun SuccessMessage(
    message: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Text(
            text = message,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onPrimaryContainer,
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Composable
private fun ErrorMessage(
    error: String,
    onRetryClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.errorContainer
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = error,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onErrorContainer
            )
            AppOutlinedButton(
                text = "Повторить",
                onClick = onRetryClick,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
