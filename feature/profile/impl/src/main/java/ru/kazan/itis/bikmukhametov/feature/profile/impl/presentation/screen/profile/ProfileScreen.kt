package ru.kazan.itis.bikmukhametov.feature.profile.impl.presentation.screen.profile

import android.annotation.SuppressLint
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import ru.kazan.itis.bikmukhametov.core.ui.appuicomponent.AppOutlinedButton
import ru.kazan.itis.bikmukhametov.core.ui.appuicomponent.AppPrimaryButton
import ru.kazan.itis.bikmukhametov.core.ui.appuicomponent.AppTextField
import ru.kazan.itis.bikmukhametov.core.ui.appuicomponent.AppTopBar
import ru.kazan.itis.bikmukhametov.feature.profile.impl.R
import ru.kazan.itis.bikmukhametov.feature.profile.impl.presentation.screen.profile.ui.ProfileAvatar
import ru.kazan.itis.bikmukhametov.feature.profile.impl.presentation.screen.profile.ui.UserInfoCard

@Composable
fun ProfileScreen(
    onLogoutClick: () -> Unit = {}
) {
    val viewModel: ProfileViewModel = hiltViewModel()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

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
        uri?.let { selectedUri ->
            viewModel.onIntent(ProfileIntent.PhotoSelected(selectedUri))
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            AppTopBar(
                title = stringResource(R.string.profile_title)
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
                    error = uiState.error ?: stringResource(R.string.profile_error_unknown),
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
                        .padding(top = paddingValues.calculateTopPadding())
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
                text = stringResource(R.string.profile_retry),
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
            label = stringResource(R.string.profile_name_label),
            enabled = !state.isUpdatingName,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Кнопка сохранения имени
        AppPrimaryButton(
            text = stringResource(R.string.profile_save_name),
            onClick = { onIntent(ProfileIntent.UpdateNameClicked) },
            enabled = !state.isUpdatingName && state.nameInput.trim().isNotEmpty(),
            isLoading = state.isUpdatingName,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Информация о пользователе
        if (profile != null) {
            UserInfoCard(
                userName = profile.name ?: stringResource(R.string.profile_not_specified),
                userEmail = profile.email ?: stringResource(R.string.profile_not_specified),
                userPhone = profile.phone
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        // Кнопка выхода
        AppOutlinedButton(
            text = stringResource(R.string.profile_logout),
            onClick = { onIntent(ProfileIntent.LogoutClicked) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        )
    }
}


