package ru.kazan.itis.bikmukhametov.feature.reading.presentation.screen.reading.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

@Composable
fun ReadingContent(
    content: String,
    scrollPosition: Int,
    fontSize: Int,
    lineSpacing: Int,
    onScrollPositionChanged: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()

    LaunchedEffect(Unit) {
        if (scrollPosition > 0 && content.isNotEmpty()) {
            // позиция прокрутки на основе позиции в тексте
            val ratio = scrollPosition.toFloat() / content.length.toFloat()
            delay(100)
            if (scrollState.maxValue > 0) {
                val targetScroll = (scrollState.maxValue * ratio).toInt()
                scrollState.scrollTo(targetScroll)
            }
        }
    }

    // размер шрифта
    val textSize = when (fontSize) {
        1 -> 14.sp
        2 -> 16.sp
        3 -> 18.sp
        else -> 16.sp
    }

    // интервал между строками
    val lineHeight = when (lineSpacing) {
        1 -> textSize * 1.2f
        2 -> textSize * 1.5f
        3 -> textSize * 1.8f
        else -> textSize * 1.5f
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp)
    ) {
        Text(
            text = content,
            style = MaterialTheme.typography.bodyLarge.copy(
                fontSize = textSize,
                lineHeight = lineHeight
            ),
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.fillMaxWidth()
        )
    }

    // Отслеживаем изменение позиции прокрутки
    LaunchedEffect(scrollState.value) {
        val currentScroll = scrollState.value
        val maxScroll = scrollState.maxValue
        if (maxScroll > 0) {
            val ratio = currentScroll.toFloat() / maxScroll.toFloat()
            val estimatedPosition = (content.length * ratio).toInt()
            onScrollPositionChanged(estimatedPosition)
        }
    }
}

