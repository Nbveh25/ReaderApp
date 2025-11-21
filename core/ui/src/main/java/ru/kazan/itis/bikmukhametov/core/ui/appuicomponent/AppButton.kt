package ru.kazan.itis.bikmukhametov.core.ui.appuicomponent

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/**
 * Основная кнопка приложения (Primary Button) с поддержкой состояния загрузки.
 *
 * @param text Текст на кнопке.
 * @param onClick Обработчик нажатия.
 * @param enabled Активность кнопки.
 * @param isLoading Флаг, указывающий на отображение индикатора загрузки.
 */
@Composable
fun AppPrimaryButton(
    modifier: Modifier = Modifier,
    text: String,
    onClick: () -> Unit,
    enabled: Boolean = true,
    isLoading: Boolean = false
) {
    Button(
        onClick = onClick,
        enabled = enabled && !isLoading,
        modifier = modifier
            .fillMaxWidth()
            .height(50.dp)
    ) {
        Box(contentAlignment = Alignment.Center) {
            if (isLoading) {
                // Индикатор загрузки
                CircularProgressIndicator(
                    color = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.size(24.dp)
                )
            } else {
                // Текст кнопки
                Text(text, style = MaterialTheme.typography.titleMedium)
            }
        }
    }
}

/**
 * Вспомогательная кнопка приложения (Outlined Button).
 *
 * @param text Текст на кнопке.
 * @param onClick Обработчик нажатия.
 * @param enabled Активность кнопки.
 */
@Composable
fun AppOutlinedButton(
    modifier: Modifier = Modifier,
    text: String,
    onClick: () -> Unit,
    enabled: Boolean = true,
    icon: @Composable (() -> Unit)? = null
) {
    OutlinedButton(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier
            .fillMaxWidth()
            .height(50.dp),
    ) {
        if (icon != null) {
            icon()
            Spacer(Modifier.width(8.dp))
        }
        Text(text, style = MaterialTheme.typography.titleMedium)
    }
}

