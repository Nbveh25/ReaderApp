package ru.kazan.itis.bikmukhametov.core.ui.theme

import androidx.compose.ui.graphics.Color

// Базовый цвет для акцента (Бирюзовый / Teal)
val AppTeal = Color(0xFF00897B) // Насыщенный бирюзовый для Light Theme
val AppTealDark = Color(0xFF80CBC4) // Более светлый, мягкий бирюзовый для Dark Theme

// --- Светлая Тема ---
val AppPrimary = AppTeal
val AppPrimaryDark = AppTealDark
val LightBackground = Color(0xFFFAFAFA) // Очень светлый фон
val SurfaceLight = Color(0xFFFFFFFF)     // Белая поверхность
val OnPrimaryLight = Color.White        // Белый текст на бирюзовом
val OnBackgroundLight = Color(0xFF1C1B1F) // Темный текст
val SurfaceVariantLight = Color(0xFFEEEEEE) // Для карточек

// --- Темная (Ночная) Тема ---
val DarkBackground = Color(0xFF000000) // True Black для AMOLED
val SurfaceDark = Color(0xFF0D0D0D)   // Очень темная поверхность
val OnPrimaryDark = Color.Black        // Темный текст на светлом акценте
val OnBackgroundDark = Color(0xFFE0E0E0) // Светлый текст
val SurfaceVariantDark = Color(0xFF1A1A1A) // Для карточек и TopBar

