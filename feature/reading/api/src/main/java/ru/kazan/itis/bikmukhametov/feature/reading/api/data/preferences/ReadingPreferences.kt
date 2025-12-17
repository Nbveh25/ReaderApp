package ru.kazan.itis.bikmukhametov.feature.reading.api.data.preferences

interface ReadingPreferences {
    // read position
    fun saveReadingPosition(bookId: String, position: Int)

    fun getReadingPosition(bookId: String): Int

    // fonts
    fun saveFontSize(size: Int)

    fun getFontSize(): Int

    // line spacing
    fun saveLineSpacing(spacing: Int)

    fun getLineSpacing(): Int

    // theme
    fun saveThemeMode(mode: Int)

    fun getThemeMode(): Int
}
