package ru.kazan.itis.bikmukhametov.feature.upload.impl.data.datasource.remote

import android.util.Log
import com.amazonaws.ClientConfiguration
import com.amazonaws.auth.BasicAWSCredentials
import com.amazonaws.services.s3.AmazonS3Client
import com.amazonaws.services.s3.model.ObjectMetadata
import com.amazonaws.services.s3.model.PutObjectRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import ru.kazan.itis.bikmukhametov.core.network.config.S3Config
import ru.kazan.itis.bikmukhametov.feature.upload.api.datasource.remote.BookUploader
import java.io.ByteArrayInputStream
import java.io.IOException
import java.io.InputStream
import javax.inject.Inject
import javax.inject.Singleton

internal class BookUploaderImpl @Inject constructor(
    private val s3Config: S3Config
) : BookUploader {

    companion object {
        private const val BOOKS_FOLDER = "books"
    }

    private fun createS3Client(): AmazonS3Client {
        val credentials = BasicAWSCredentials(
            s3Config.accessKeyId,
            s3Config.secretAccessKey
        )

        val clientConfiguration = ClientConfiguration().apply {
            connectionTimeout = 60000
            socketTimeout = 300000
            maxErrorRetry = 3
        }
        
        val s3Client = AmazonS3Client(credentials, clientConfiguration)
        
        val endpointWithoutProtocol = s3Config.endpoint
            .removePrefix("https://")
            .removePrefix("http://")
        s3Client.setEndpoint(endpointWithoutProtocol)
        
        try {
            val region = com.amazonaws.regions.Region.getRegion(
                com.amazonaws.regions.Regions.fromName(s3Config.region)
            )
            s3Client.setRegion(region)
        } catch (e: Exception) {
            Log.w("BookUploaderImpl", "Регион ${s3Config.region} не найден: ${e.message}")
        }
        
        return s3Client
    }

    private fun getContentType(fileName: String): String {
        val extension = fileName.substringAfterLast('.', "").lowercase()
        return when (extension) {
            "txt" -> "text/plain"
            "epub" -> "application/epub+zip"
            "pdf" -> "application/pdf"
            else -> "application/octet-stream"
        }
    }

    override suspend fun uploadBook(
        inputStream: InputStream,
        fileName: String,
        userId: String,
        onProgress: (Float) -> Unit
    ): Result<String> = withContext(Dispatchers.IO) {
        val s3Client = createS3Client()
        
        try {

            val fileBytes = inputStream.readBytes()
            Log.d("BookUploaderImpl", "Размер файла: ${fileBytes.size} байт")
            
            val objectKey = "$BOOKS_FOLDER/$userId/$fileName"
            Log.d("BookUploaderImpl", "Object Key: $objectKey")
            
            val contentType = getContentType(fileName)
            Log.d("BookUploaderImpl", "Content-Type: $contentType")
            
            val metadata = ObjectMetadata().apply {
                contentLength = fileBytes.size.toLong()
            }
            
            val byteArrayInputStream = ByteArrayInputStream(fileBytes)
            
            val putRequest = PutObjectRequest(
                s3Config.bucketName,
                objectKey,
                byteArrayInputStream,
                metadata
            )
            
            Log.d("BookUploaderImpl", "Отправка PUT запроса через S3 SDK...")
            Log.d("BookUploaderImpl", "Bucket: ${s3Config.bucketName}")
            
            onProgress(0.3f)
            
            s3Client.putObject(putRequest)
            
            onProgress(1.0f)
            
            val publicUrl = "${s3Config.endpoint}/${s3Config.bucketName}/$objectKey"
            Log.d("BookUploaderImpl", "Файл успешно загружен: $publicUrl")
            
            Result.success(publicUrl)
        } catch (e: com.amazonaws.AmazonServiceException) {
            val errorMessage = when (e.statusCode) {
                401 -> "Ошибка аутентификации. Проверьте Access Key ID и Secret Access Key"
                403 -> "Доступ запрещен. Проверьте права доступа к бакету"
                404 -> "Бакет не найден: ${s3Config.bucketName}"
                else -> "Ошибка загрузки книги: ${e.errorCode} - ${e.message}"
            }
            Log.e("BookUploaderImpl", errorMessage, e)
            Result.failure(IOException(errorMessage, e))
        } catch (e: IOException) {
            Log.e("BookUploaderImpl", "IOException при загрузке книги: ${e.message}", e)
            Result.failure(e)
        } catch (e: Exception) {
            Log.e("BookUploaderImpl", "Exception при загрузке книги: ${e.message}", e)
            Result.failure(IOException("Ошибка загрузки книги: ${e.message}", e))
        } finally {
            s3Client.shutdown()
        }
    }
}

