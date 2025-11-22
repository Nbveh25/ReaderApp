package ru.kazan.itis.bikmukhametov.feature.reading.data.preferences

import android.content.Context
import android.content.SharedPreferences
import ru.kazan.itis.bikmukhametov.feature.reading.api.data.ReadingPreferences
import androidx.core.content.edit

class ReadingPreferencesImpl(
    context: Context
) : ReadingPreferences {
    private val prefs: SharedPreferences = context.getSharedPreferences(
        PREFS_NAME,
        Context.MODE_PRIVATE
    )

    override fun saveReadingPosition(bookId: String, position: Int) {
        prefs.edit { putInt("${PREFIX_POSITION}$bookId", position) }
    }

    override fun getReadingPosition(bookId: String): Int {
        return prefs.getInt("${PREFIX_POSITION}$bookId", 0)
    }

    override fun saveFontSize(size: Int) {
        prefs.edit { putInt(KEY_FONT_SIZE, size) }
    }

    override fun getFontSize(): Int {
        return prefs.getInt(KEY_FONT_SIZE, 2)
    }

    override fun saveLineSpacing(spacing: Int) {
        prefs.edit { putInt(KEY_LINE_SPACING, spacing) }
    }

    override fun getLineSpacing(): Int {
        return prefs.getInt(KEY_LINE_SPACING, 2)
    }

    override fun saveThemeMode(mode: Int) {
        prefs.edit { putInt(KEY_THEME_MODE, mode) }
    }

    override fun getThemeMode(): Int {
        return prefs.getInt(KEY_THEME_MODE, 0)
    }

    companion object {
        private const val PREFS_NAME = "reading_preferences"
        private const val PREFIX_POSITION = "reading_position_"
        private const val KEY_FONT_SIZE = "font_size"
        private const val KEY_LINE_SPACING = "line_spacing"
        private const val KEY_THEME_MODE = "theme_mode"
    }
}

