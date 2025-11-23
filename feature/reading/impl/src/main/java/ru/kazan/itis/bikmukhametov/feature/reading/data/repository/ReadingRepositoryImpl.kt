package ru.kazan.itis.bikmukhametov.feature.reading.data.repository

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import ru.kazan.itis.bikmukhametov.feature.reading.api.data.ReadingPreferences
import ru.kazan.itis.bikmukhametov.feature.reading.api.repository.ReadingRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class ReadingRepositoryImpl @Inject constructor(
    private val readingPreferences: ReadingPreferences
) : ReadingRepository {
    
    override suspend fun saveReadingPosition(bookId: String, position: Int) = withContext(Dispatchers.IO) {
        readingPreferences.saveReadingPosition(bookId, position)
    }
    
    override suspend fun getReadingPosition(bookId: String): Int = withContext(Dispatchers.IO) {
        readingPreferences.getReadingPosition(bookId)
    }
    
    override suspend fun saveFontSize(size: Int) = withContext(Dispatchers.IO) {
        readingPreferences.saveFontSize(size)
    }
    
    override suspend fun getFontSize(): Int = withContext(Dispatchers.IO) {
        readingPreferences.getFontSize()
    }
    
    override suspend fun saveLineSpacing(spacing: Int) = withContext(Dispatchers.IO) {
        readingPreferences.saveLineSpacing(spacing)
    }
    
    override suspend fun getLineSpacing(): Int = withContext(Dispatchers.IO) {
        readingPreferences.getLineSpacing()
    }
    
    override suspend fun saveThemeMode(mode: Int) = withContext(Dispatchers.IO) {
        readingPreferences.saveThemeMode(mode)
    }
    
    override suspend fun getThemeMode(): Int = withContext(Dispatchers.IO) {
        readingPreferences.getThemeMode()
    }
}

