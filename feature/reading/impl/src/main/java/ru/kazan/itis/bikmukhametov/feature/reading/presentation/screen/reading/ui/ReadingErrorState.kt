package ru.kazan.itis.bikmukhametov.feature.reading.presentation.screen.reading.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import ru.kazan.itis.bikmukhametov.core.ui.appuicomponent.AppPrimaryButton
import ru.kazan.itis.bikmukhametov.core.ui.appuicomponent.AppOutlinedButton
import ru.kazan.itis.bikmukhametov.feature.reading.R

@Composable
fun ReadingErrorState(
    error: String,
    onRetry: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = stringResource(R.string.reading_error_title),
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.error
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            text = error,
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurface
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        Column(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            AppPrimaryButton(
                text = stringResource(R.string.reading_error_retry),
                onClick = onRetry
            )
            
            AppOutlinedButton(
                text = stringResource(R.string.reading_error_delete),
                onClick = onDelete
            )
        }
    }
}

