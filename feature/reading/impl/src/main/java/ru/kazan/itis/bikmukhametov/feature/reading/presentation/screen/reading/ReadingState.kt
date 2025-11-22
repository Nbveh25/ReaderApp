package ru.kazan.itis.bikmukhametov.feature.reading.presentation.screen.reading

data class ReadingState(

    val bookId: String = "",

    val bookTitle: String = "",

    val bookContent: String = "",

    val scrollPosition: Int = 0,

    val totalCharacters: Int = 0,

    val progressPercent: Int = 0,

    val isLoading: Boolean = false,

    val error: String? = null,

    val isSettingsOpen: Boolean = false,

    val fontSize: Int = 2,

    val lineSpacing: Int = 2,

    val themeMode: Int = 0
)

