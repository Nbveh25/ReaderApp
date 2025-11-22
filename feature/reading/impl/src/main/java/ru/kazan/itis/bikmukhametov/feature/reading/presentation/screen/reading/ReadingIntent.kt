package ru.kazan.itis.bikmukhametov.feature.reading.presentation.screen.reading

sealed interface ReadingIntent {

    object LoadBook : ReadingIntent

    data class ScrollPositionChanged(val position: Int) : ReadingIntent

    object ToggleSettings : ReadingIntent

    data class FontSizeChanged(val size: Int) : ReadingIntent

    data class LineSpacingChanged(val spacing: Int) : ReadingIntent

    data class ThemeModeChanged(val mode: Int) : ReadingIntent

    object Retry : ReadingIntent

    object DeleteBook : ReadingIntent
}

