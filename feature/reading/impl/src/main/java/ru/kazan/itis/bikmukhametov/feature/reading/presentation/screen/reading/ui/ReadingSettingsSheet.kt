package ru.kazan.itis.bikmukhametov.feature.reading.presentation.screen.reading.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import ru.kazan.itis.bikmukhametov.feature.reading.R

/**
 * Шторка с настройками чтения.
 */
@Composable
fun ReadingSettingsSheet(
    fontSize: Int,
    lineSpacing: Int,
    themeMode: Int,
    onFontSizeChanged: (Int) -> Unit,
    onLineSpacingChanged: (Int) -> Unit,
    onThemeModeChanged: (Int) -> Unit,
    onDismiss: () -> Unit
) {
    // Фон для затемнения
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.5f))
            .clickable(onClick = onDismiss)
    ) {
        // Сама шторка
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 8.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                Text(
                    text = stringResource(R.string.reading_settings_title),
                    style = MaterialTheme.typography.titleLarge
                )

                // Размер шрифта
                Column {
                    Text(
                        text = stringResource(R.string.reading_settings_font_size),
                        style = MaterialTheme.typography.titleMedium
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf(1, 2, 3).forEach { size ->
                            FilterChip(
                                selected = fontSize == size,
                                onClick = { onFontSizeChanged(size) },
                                label = { Text("${size}") },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }

                // Интервал между строками
                Column {
                    Text(
                        text = stringResource(R.string.reading_settings_line_spacing),
                        style = MaterialTheme.typography.titleMedium
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf(1, 2, 3).forEach { spacing ->
                            FilterChip(
                                selected = lineSpacing == spacing,
                                onClick = { onLineSpacingChanged(spacing) },
                                label = { Text("${spacing}") },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }

                // Тема
                Column {
                    Text(
                        text = stringResource(R.string.reading_settings_theme),
                        style = MaterialTheme.typography.titleMedium
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf(
                            stringResource(R.string.reading_settings_theme_light) to 0,
                            stringResource(R.string.reading_settings_theme_dark) to 1
                        ).forEach { (label, mode) ->
                            FilterChip(
                                selected = themeMode == mode,
                                onClick = { onThemeModeChanged(mode) },
                                label = { Text(label) },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

