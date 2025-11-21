package ru.kazan.itis.bikmukhametov.core.ui.appuicomponent

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/**
 * Кастомная реализация AppBar для обхода аннотации @OptIn.
 *
 * @param title Текст заголовка.
 * @param endContent Контент, который размещается справа (например, кнопка регистрации).
 */
@Composable
fun AppTopBar(
    title: String,
    endContent: @Composable () -> Unit = {}
) {
    Surface(
        color = MaterialTheme.colorScheme.surfaceVariant,
        tonalElevation = 2.dp, // Небольшая тень для визуального разделения
        modifier = Modifier.windowInsetsPadding(WindowInsets.statusBars)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp) // Стандартная высота AppBar
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Заголовок
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.weight(1f) // Занимает всё доступное пространство
            )
            // Контент справа (например, кнопки-действия)
            endContent()
        }
    }
}

