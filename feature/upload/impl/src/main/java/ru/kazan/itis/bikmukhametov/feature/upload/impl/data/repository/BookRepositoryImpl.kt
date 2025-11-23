package ru.kazan.itis.bikmukhametov.feature.upload.impl.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import ru.kazan.itis.bikmukhametov.feature.upload.api.model.BookMetadata
import ru.kazan.itis.bikmukhametov.feature.upload.api.repository.BookRepository
import javax.inject.Inject
import javax.inject.Singleton

class BookRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : BookRepository {

    companion object {
        private const val COLLECTION_BOOKS = "books"
    }

    override suspend fun saveBookMetadata(metadata: BookMetadata): Result<Unit> {
        return try {
            val bookData = hashMapOf(
                "title" to metadata.title,
                "author" to metadata.author,
                "fileUrl" to metadata.fileUrl,
                "userId" to metadata.userId,
                "fileName" to metadata.fileName,
                "fileSize" to metadata.fileSize,
                "format" to metadata.format.lowercase()
            )

            firestore.collection(COLLECTION_BOOKS)
                .add(bookData)
                .await()

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

