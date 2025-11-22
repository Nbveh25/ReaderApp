package ru.kazan.itis.bikmukhametov.feature.reading.presentation.screen.reading

sealed interface ReadingEffect {

    data class ShowMessage(val message: String) : ReadingEffect

    object NavigateBack : ReadingEffect

    object BookDeleted : ReadingEffect
}

