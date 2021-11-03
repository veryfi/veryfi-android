package com.veryfi.android.client

import android.content.Context
import org.json.JSONObject
import java.io.InputStream

class HttpClientMock(private val context: Context) : HttpClient {

    override fun getDocuments(): String {
        return gerFileAsStringByteArray("getDocuments.json")
    }

    override fun getDocument(documentId: String): String {
        return gerFileAsStringByteArray("getDocument.json")
    }

    override fun processDocument(
        fileStream: InputStream,
        fileName: String,
        categories: List<String>,
        deleteAfterProcessing: Boolean,
        parameters: JSONObject?
    ): String {
        return gerFileAsStringByteArray("processDocument.json")
    }

    override fun updateDocument(documentId: String, parameters: JSONObject?): String {
        return gerFileAsStringByteArray("updateDocument.json")
    }

    override fun deleteDocument(documentId: String): String {
        return gerFileAsStringByteArray("deleteDocument.json")
    }

    override fun processDocumentUrl(
        fileUrl: String,
        fileUrls: List<String>?,
        categories: List<String>?,
        deleteAfterProcessing: Boolean,
        maxPagesToProcess: Int,
        boostMode: Boolean,
        externalId: String?,
        parameters: JSONObject?
    ): String {
        return gerFileAsStringByteArray("processDocumentUrl.json")
    }

    private fun gerFileAsStringByteArray(fileName: String): String {
        val inputStream: InputStream = context.assets.open(fileName)
        val size: Int = inputStream.available()
        val buffer = ByteArray(size)
        inputStream.read(buffer)
        inputStream.close()
        return String(buffer)
    }

}