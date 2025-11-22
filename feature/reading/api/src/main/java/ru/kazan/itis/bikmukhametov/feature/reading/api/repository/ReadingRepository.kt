package ru.kazan.itis.bikmukhametov.feature.reading.api.repository

interface ReadingRepository {
    suspend fun saveReadingPosition(bookId: String, position: Int)
    
    suspend fun getReadingPosition(bookId: String): Int
    
    suspend fun saveFontSize(size: Int)
    
    suspend fun getFontSize(): Int
    
    suspend fun saveLineSpacing(spacing: Int)
    
    suspend fun getLineSpacing(): Int
    
    suspend fun saveThemeMode(mode: Int)
    
    suspend fun getThemeMode(): Int
}

