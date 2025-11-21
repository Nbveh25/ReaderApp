package ru.kazan.itis.bikmukhametov.core.ui.appuicomponent

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation

/**
 * Универсальное поле ввода текста (OutlinedTextField) для форм.
 *
 * @param value Текущее значение поля.
 * @param onValueChange Обработчик изменения значения.
 * @param label Текст метки.
 * @param keyboardOptions Опции клавиатуры (тип, действие).
 * @param keyboardActions Действия клавиатуры.
 * @param isError Флаг ошибки.
 * @param enabled Флаг активности поля.
 * @param visualTransformation Трансформация текста (например, для пароля).
 * @param trailingIcon Иконка в конце поля (опционально).
 */
@Composable
fun AppTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    isError: Boolean = false,
    enabled: Boolean = true,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    trailingIcon: @Composable (() -> Unit)? = null,
    supportingText: @Composable (() -> Unit)? = null
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        keyboardOptions = keyboardOptions,
        keyboardActions = keyboardActions,
        isError = isError,
        enabled = enabled,
        visualTransformation = visualTransformation,
        trailingIcon = trailingIcon,
        supportingText = supportingText,
        modifier = modifier.fillMaxWidth()
    )
}

/**
 * Специализированное поле ввода для пароля с функцией переключения видимости.
 * Использует внутреннее локальное состояние для управления видимостью пароля.
 *
 * @param value Текущее значение поля.
 * @param onValueChange Обработчик изменения значения.
 * @param label Текст метки.
 * @param keyboardOptions Опции клавиатуры (тип, действие).
 * @param keyboardActions Действия клавиатуры.
 * @param isError Флаг ошибки.
 * @param enabled Флаг активности поля.
 */
@Composable
fun AppPasswordTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    isPasswordVisible: Boolean,
    onToggleVisibility: () -> Unit,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    isError: Boolean = false,
    enabled: Boolean = true,
    supportingText: @Composable (() -> Unit)? = null
) {
    AppTextField(
        value = value,
        onValueChange = onValueChange,
        label = label,
        keyboardOptions = keyboardOptions,
        keyboardActions = keyboardActions,
        isError = isError,
        enabled = enabled,
        visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
        trailingIcon = {
            val icon = if (isPasswordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility
            val description = if (isPasswordVisible) "Скрыть пароль" else "Показать пароль"
            Icon(
                imageVector = icon,
                contentDescription = description,
                modifier = Modifier.clickable { onToggleVisibility() }
            )
        },
        supportingText = supportingText
    )
}

