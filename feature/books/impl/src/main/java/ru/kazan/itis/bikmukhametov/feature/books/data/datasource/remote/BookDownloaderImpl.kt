package ru.kazan.itis.bikmukhametov.feature.books.data.datasource.remote

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import ru.kazan.itis.bikmukhametov.util.enum.BookFormat
import ru.kazan.itis.bikmukhametov.feature.books.api.datasource.remote.BookDownloader as BookDownloaderContract
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class BookDownloaderImpl @Inject constructor(
    private val okHttpClient: OkHttpClient
) : BookDownloaderContract {

    override suspend fun downloadBook(fileUrl: String): Result<ByteArray> = withContext(Dispatchers.IO) {
        try {
            //Log.d("BookDownloader", "Downloading book from URL: $fileUrl")
            val request = Request.Builder()
                .url(fileUrl)
                .get()
                .build()

            val response = okHttpClient.newCall(request).execute()

            if (!response.isSuccessful) {
                return@withContext Result.failure(
                    IOException("Ошибка загрузки: HTTP ${response.code} ${response.message}")
                )
            }

            val body = response.body

            val bytes = body.bytes()
            Result.success(bytes)
        } catch (e: IOException) {
            Result.failure(e)
        } catch (e: Exception) {
            Result.failure(IOException("Ошибка загрузки: ${e.message}", e))
        }
    }

    override fun getBookFormatFromUrl(fileUrl: String): BookFormat {
        return BookFormat.fromFileName(fileUrl) ?: BookFormat.PDF
    }
}

