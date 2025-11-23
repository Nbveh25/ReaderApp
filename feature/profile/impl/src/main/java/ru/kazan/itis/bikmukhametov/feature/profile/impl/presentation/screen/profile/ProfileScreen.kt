package ru.kazan.itis.bikmukhametov.feature.profile.impl.presentation.screen.profile

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import ru.kazan.itis.bikmukhametov.core.ui.appuicomponent.AppOutlinedButton
import ru.kazan.itis.bikmukhametov.core.ui.appuicomponent.AppPrimaryButton
import ru.kazan.itis.bikmukhametov.core.ui.appuicomponent.AppTextField
import ru.kazan.itis.bikmukhametov.core.ui.appuicomponent.AppTopBar
import ru.kazan.itis.bikmukhametov.feature.profile.impl.presentation.screen.profile.ui.ProfileAvatar
import ru.kazan.itis.bikmukhametov.feature.profile.impl.presentation.screen.profile.ui.UserInfoCard
import java.io.InputStream

@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel = hiltViewModel(),
    onLogoutClick: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current

    // Обработка эффектов
    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is ProfileEffect.ShowMessage -> {
                    snackbarHostState.showSnackbar(effect.message)
                }
                is ProfileEffect.NavigateToAuth -> {
                    onLogoutClick()
                }
                is ProfileEffect.OpenImagePicker -> {
                    // Открывается через launcher
                }
            }
        }
    }

    // Launcher для выбора изображения
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            try {
                val inputStream: InputStream? = context.contentResolver.openInputStream(it)
                val fileName = it.lastPathSegment ?: "profile_photo.jpg"
                inputStream?.let { stream ->
                    viewModel.onIntent(
                        ProfileIntent.PhotoSelected(stream, fileName)
                    )
                }
            } catch (e: Exception) {
                viewModel.onIntent(ProfileIntent.RetryClicked)
            }
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            AppTopBar(
                title = "Профиль"
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        when {
            uiState.isLoading && uiState.userProfile == null -> {
                LoadingState()
            }
            uiState.error != null && uiState.userProfile == null -> {
                ErrorState(
                    error = uiState.error ?: "Неизвестная ошибка",
                    onRetry = { viewModel.onIntent(ProfileIntent.RetryClicked) }
                )
            }
            else -> {
                ProfileContent(
                    state = uiState,
                    onIntent = viewModel::onIntent,
                    onPhotoClick = {
                        viewModel.onIntent(ProfileIntent.PhotoClick)
                        imagePickerLauncher.launch("image/*")
                    },
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                )
            }
        }
    }
}

@Composable
private fun LoadingState() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}

@Composable
private fun ErrorState(
    error: String,
    onRetry: () -> Unit
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = error,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.error
            )
            Spacer(modifier = Modifier.height(16.dp))
            AppOutlinedButton(
                text = "Повторить",
                onClick = onRetry
            )
        }
    }
}

@Composable
private fun ProfileContent(
    state: ProfileState,
    onIntent: (ProfileIntent) -> Unit,
    onPhotoClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val profile = state.userProfile

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(32.dp))

        // Фото пользователя
        Box {
            ProfileAvatar(
                photoUrl = profile?.photoUrl,
                onClick = onPhotoClick,
                modifier = Modifier.size(120.dp),
                isLoading = state.isUploadingPhoto
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Редактирование имени
        AppTextField(
            value = state.nameInput,
            onValueChange = { onIntent(ProfileIntent.NameChanged(it)) },
            label = "Имя",
            enabled = !state.isUpdatingName,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Кнопка сохранения имени
        AppPrimaryButton(
            text = "Сохранить имя",
            onClick = { onIntent(ProfileIntent.UpdateNameClicked) },
            enabled = !state.isUpdatingName && state.nameInput.trim().isNotEmpty(),
            isLoading = state.isUpdatingName,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Информация о пользователе
        if (profile != null) {
            UserInfoCard(
                userName = profile.name ?: "Не указано",
                userEmail = profile.email ?: "Не указано",
                userPhone = profile.phone
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        // Кнопка выхода
        AppOutlinedButton(
            text = "Выйти из аккаунта",
            onClick = { onIntent(ProfileIntent.LogoutClicked) },
            modifier = Modifier.fillMaxWidth()
        )
    }
}


