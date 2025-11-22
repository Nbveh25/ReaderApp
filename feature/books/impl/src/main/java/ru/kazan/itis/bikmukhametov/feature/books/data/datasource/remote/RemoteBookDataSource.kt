package ru.kazan.itis.bikmukhametov.feature.books.data.datasource.remote

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import ru.kazan.itis.bikmukhametov.model.BookModel
import ru.kazan.itis.bikmukhametov.util.enum.BookFormat
import ru.kazan.itis.bikmukhametov.feature.books.api.datasource.remote.RemoteBookDataSource as RemoteBookDataSourceContract
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RemoteBookDataSourceImpl @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val firebaseAuth: FirebaseAuth
) : RemoteBookDataSourceContract {
    companion object {
        private const val COLLECTION_BOOKS = "books"
        private const val FIELD_USER_ID = "userId"
        private const val FIELD_TITLE = "title"
        private const val FIELD_AUTHOR = "author"
        private const val FIELD_COVER_URL = "coverUrl"
        private const val FIELD_FORMAT = "format"
        private const val FIELD_BUCKET_NAME = "bucketName"
        private const val FIELD_FILE_URL = "fileUrl"
    }

    override suspend fun getBooks(): Result<List<BookModel>> {
        return try {
            val currentUser = firebaseAuth.currentUser
            if (currentUser == null) {
                return Result.failure(Exception("Пользователь не авторизован"))
            }

            val userId = currentUser.uid

            val snapshot = firestore.collection(COLLECTION_BOOKS)
                .whereEqualTo(FIELD_USER_ID, userId)
                .get()
                .await()

            val books = snapshot.documents.mapNotNull { document ->
                try {
                    mapDocumentToBookModel(document.id, document.data)
                } catch (e: Exception) {
                    null // Пропускаем некорректные документы
                }
            }

            // Сортируем книги по названию в памяти
            val sortedBooks = books.sortedBy { it.title }

            Result.success(sortedBooks)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Преобразует документ Firestore в BookModel.
     */
    private fun mapDocumentToBookModel(
        documentId: String,
        data: Map<String, Any>?
    ): BookModel? {
        if (data == null) return null

        val title = data[FIELD_TITLE] as? String ?: return null
        val author = data[FIELD_AUTHOR] as? String ?: return null
        val userId = data[FIELD_USER_ID] as? String ?: return null

        val coverUrl = data[FIELD_COVER_URL] as? String
        val bucketName = data[FIELD_BUCKET_NAME] as? String
        val fileUrl = data[FIELD_FILE_URL] as? String

        // Определяем формат из строки или из расширения fileUrl
        val formatString = data[FIELD_FORMAT] as? String
        val format = when {
            formatString != null -> {
                when (formatString.uppercase()) {
                    "PDF" -> BookFormat.PDF
                    "EPUB" -> BookFormat.EPUB
                    "TXT" -> BookFormat.TXT
                    else -> BookFormat.PDF
                }
            }
            fileUrl != null -> {
                BookFormat.fromFileName(fileUrl) ?: BookFormat.PDF
            }
            else -> BookFormat.PDF
        }

        return BookModel(
            id = documentId,
            title = title,
            author = author,
            coverUrl = coverUrl,
            format = format,
            userId = userId,
            bucketName = bucketName,
            fileUrl = fileUrl,
            isDownloaded = false, // Локальный статус будет обновлен в репозитории
            isAvailableInFirestore = true,
            localFilePath = null
        )
    }
}

