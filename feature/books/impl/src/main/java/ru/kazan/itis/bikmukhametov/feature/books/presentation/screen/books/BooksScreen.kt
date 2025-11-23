package ru.kazan.itis.bikmukhametov.feature.books.presentation.screen.books

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import ru.kazan.itis.bikmukhametov.core.ui.appuicomponent.AppPrimaryButton
import ru.kazan.itis.bikmukhametov.core.ui.appuicomponent.AppTopBar
import ru.kazan.itis.bikmukhametov.feature.books.R
import ru.kazan.itis.bikmukhametov.feature.books.presentation.screen.books.ui.BooksList
import ru.kazan.itis.bikmukhametov.feature.books.presentation.screen.books.ui.EmptySearchState
import ru.kazan.itis.bikmukhametov.feature.books.presentation.screen.books.ui.SearchBar

@Composable
fun BooksScreen(
    onNavigateToReading: (String) -> Unit = {}
) {
    val viewModel: BooksViewModel = hiltViewModel()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is BooksEffect.ShowMessage -> {
                    snackbarHostState.showSnackbar(effect.message)
                }
                is BooksEffect.NavigateToReading -> {
                    onNavigateToReading(effect.bookId)
                }
            }
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            AppTopBar(
                title = stringResource(R.string.books_title),
                endContent = {
                    SearchBar(
                        query = uiState.searchQuery,
                        onQueryChange = {
                            viewModel.onIntent(
                                BooksIntent.SearchQueryChanged(it)
                            )
                        },
                        modifier = Modifier.width(200.dp)
                    )
                }
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        PullToRefreshBox(
            isRefreshing = uiState.isRefreshing,
            onRefresh = { viewModel.onIntent(BooksIntent.RefreshClicked) },
            modifier = Modifier
                .fillMaxSize()
                .padding(top = paddingValues.calculateTopPadding())
        ) {
            when {
                uiState.isLoading && uiState.books.isEmpty() -> {
                    LoadingState()
                }

                uiState.error != null && uiState.books.isEmpty() -> {
                    ErrorState(
                        error = uiState.error ?: stringResource(R.string.books_error_unknown),
                        onRetry = { viewModel.onIntent(BooksIntent.RetryClicked) }
                    )
                }

                uiState.filteredBooks.isEmpty() && uiState.searchQuery.isNotBlank() -> {
                    EmptySearchState()
                }

                uiState.filteredBooks.isEmpty() -> {
                    EmptyState()
                }

                else -> {
                    BooksList(
                        books = uiState.filteredBooks,
                        processingBookId = uiState.processingBookId,
                        onBookClick = { viewModel.onIntent(BooksIntent.BookClicked(it)) },
                        onDeleteClick = {
                            viewModel.onIntent(BooksIntent.DeleteBookClicked(it))
                        },
                        onDownloadClick = {
                            viewModel.onIntent(BooksIntent.DownloadBookClicked(it))
                        }
                    )
                }
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
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = error,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.error
            )
            AppPrimaryButton(
                text = stringResource(R.string.books_retry),
                onClick = onRetry
            )
        }
    }
}

@Composable
fun EmptyState() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.padding(16.dp)
        ) {
            Icon(
                painter = painterResource(id = android.R.drawable.ic_menu_gallery),
                contentDescription = null,
                modifier = Modifier.size(64.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = stringResource(R.string.books_empty),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}