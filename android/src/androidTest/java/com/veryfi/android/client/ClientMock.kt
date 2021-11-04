package com.veryfi.android.client

import android.content.Context
import org.json.JSONObject
import java.io.InputStream

class ClientMock(private val context: Context) : Client {

    override fun getDocuments(): String {
        return getFileAsStringByteArray("getDocuments.json")
    }

    override fun getDocument(documentId: String): String {
        return getFileAsStringByteArray("getDocument.json")
    }

    override fun processDocument(
        fileStream: InputStream,
        fileName: String,
        categories: List<String>,
        deleteAfterProcessing: Boolean,
        parameters: JSONObject?
    ): String {
        return getFileAsStringByteArray("processDocument.json")
    }

    override fun updateDocument(documentId: String, parameters: JSONObject?): String {
        return getFileAsStringByteArray("updateDocument.json")
    }

    override fun deleteDocument(documentId: String): String {
        return getFileAsStringByteArray("deleteDocument.json")
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
        return getFileAsStringByteArray("processDocumentUrl.json")
    }

    private fun getFileAsStringByteArray(fileName: String): String {
        val inputStream: InputStream = context.assets.open(fileName)
        val size: Int = inputStream.available()
        val buffer = ByteArray(size)
        inputStream.read(buffer)
        inputStream.close()
        return String(buffer)
    }

}