package ru.kazan.itis.bikmukhametov.feature.reading.presentation.screen.reading

import android.content.Context
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import ru.kazan.itis.bikmukhametov.core.ui.appuicomponent.AppTopBar
import ru.kazan.itis.bikmukhametov.feature.reading.R
import ru.kazan.itis.bikmukhametov.core.ui.theme.AvitoTheme
import ru.kazan.itis.bikmukhametov.feature.reading.presentation.screen.reading.ui.ReadingContent
import ru.kazan.itis.bikmukhametov.feature.reading.presentation.screen.reading.ui.ReadingErrorState
import ru.kazan.itis.bikmukhametov.feature.reading.presentation.screen.reading.ui.ReadingProgressIndicator
import ru.kazan.itis.bikmukhametov.feature.reading.presentation.screen.reading.ui.ReadingSettingsSheet

@Composable
fun ReadingScreen(
    bookId: String,
    onNavigateBack: () -> Unit,
    viewModel: ReadingViewModel = hiltViewModel(),
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    val bookDeletedMessage = stringResource(R.string.reading_book_deleted)

    LaunchedEffect(bookId) {
        if (uiState.bookId != bookId) {
            viewModel.initialize(bookId)
        }
    }

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is ReadingEffect.ShowMessage -> {
                    snackbarHostState.showSnackbar(effect.message)
                }
                is ReadingEffect.NavigateBack -> {
                    onNavigateBack()
                }
                is ReadingEffect.BookDeleted -> {
                    snackbarHostState.showSnackbar(message = bookDeletedMessage)
                }
            }
        }
    }

    AvitoTheme(darkTheme = uiState.themeMode == 1) {
        Scaffold(
            snackbarHost = { SnackbarHost(snackbarHostState) },
            topBar = {
                AppTopBar(
                    title = uiState.bookTitle.ifEmpty { stringResource(R.string.reading_title) },
                    navigationIcon = {
                        IconButton(onClick = onNavigateBack) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = stringResource(R.string.reading_back)
                            )
                        }
                    },
                    endContent = {
                        IconButton(
                            onClick = { viewModel.onIntent(ReadingIntent.ToggleSettings) }
                        ) {
                            Text(
                                text = stringResource(R.string.reading_settings_button),
                                style = MaterialTheme.typography.titleMedium
                            )
                        }
                    }
                )
            },
            bottomBar = {
                if (uiState.bookContent.isNotEmpty()) {
                    ReadingProgressIndicator(
                        progressPercent = uiState.progressPercent
                    )
                }
            },
            containerColor = MaterialTheme.colorScheme.background
        ) { paddingValues ->
            Box(
                modifier = modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                when {
                    uiState.isLoading -> {
                        CircularProgressIndicator(
                            modifier = Modifier.align(Alignment.Center)
                        )
                    }

                    uiState.error != null -> {
                        ReadingErrorState(
                            error = uiState.error ?: stringResource(R.string.reading_unknown_error),
                            onRetry = { viewModel.onIntent(ReadingIntent.Retry) },
                            onDelete = { viewModel.onIntent(ReadingIntent.DeleteBook) }
                        )
                    }

                    uiState.bookContent.isNotEmpty() -> {
                        ReadingContent(
                            content = uiState.bookContent,
                            scrollPosition = uiState.scrollPosition,
                            fontSize = uiState.fontSize,
                            lineSpacing = uiState.lineSpacing,
                            onScrollPositionChanged = { position ->
                                viewModel.onIntent(ReadingIntent.ScrollPositionChanged(position))
                            }
                        )
                    }
                }

                // settings bottomsheet
                if (uiState.isSettingsOpen) {
                    ReadingSettingsSheet(
                        fontSize = uiState.fontSize,
                        lineSpacing = uiState.lineSpacing,
                        themeMode = uiState.themeMode,
                        onFontSizeChanged = { size ->
                            viewModel.onIntent(ReadingIntent.FontSizeChanged(size))
                        },
                        onLineSpacingChanged = { spacing ->
                            viewModel.onIntent(ReadingIntent.LineSpacingChanged(spacing))
                        },
                        onThemeModeChanged = { mode ->
                            viewModel.onIntent(ReadingIntent.ThemeModeChanged(mode))
                        },
                        onDismiss = { viewModel.onIntent(ReadingIntent.ToggleSettings) }
                    )
                }
            }
        }
    }
}

