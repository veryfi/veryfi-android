package com.veryfi.android.client

import android.util.Base64
import org.json.JSONObject
import java.io.*
import java.net.HttpURLConnection
import java.net.URL
import java.nio.charset.StandardCharsets
import java.security.InvalidKeyException
import java.security.NoSuchAlgorithmException
import java.security.spec.MGF1ParameterSpec.SHA256
import java.util.*
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

class HttpClientImpl(private val httpClientData: HttpClientData) : HttpClient {

    private val baseUrl = "https://api.veryfi.com/api/"
    private val timeOut = 120000
    private val apiVersion = 7

    override fun getDocuments(): String {
        val requestArguments = JSONObject()
        val httpConnection = getHttpURLConnection(requestArguments, "documents", "GET")
        httpConnection.connect()
        val br = BufferedReader(InputStreamReader(httpConnection.inputStream))
        val sb = StringBuilder()
        var line: String?
        line = br.readLine()
        while (line != null) {
            sb.append("$line\n")
            line = br.readLine()
        }
        br.close()
        return sb.toString()
    }

    override fun getDocument(documentId: String): String {
        val requestArguments = JSONObject()
        requestArguments.put("id", documentId)
        val httpConnection =
            getHttpURLConnection(requestArguments, "documents/$documentId", "GET")
        httpConnection.connect()
        val br = BufferedReader(InputStreamReader(httpConnection.inputStream))
        val sb = StringBuilder()
        var line: String?
        line = br.readLine()
        while (line != null) {
            sb.append("$line\n")
            line = br.readLine()
        }
        br.close()
        return sb.toString()
    }

    override fun processDocument(
        fileStream: InputStream,
        fileName: String,
        categories: List<String>,
        deleteAfterProcessing: Boolean,
        parameters: JSONObject?
    ): String {
        val requestArguments =
            getProcessDocumentArguments(
                fileStream,
                fileName,
                categories,
                deleteAfterProcessing,
                parameters
            )
        val httpConnection =
            getHttpURLConnection(requestArguments, "documents", "POST")
        httpConnection.doInput = true
        httpConnection.doOutput = true

        val os: OutputStream = httpConnection.outputStream
        os.write(requestArguments.toString().toByteArray(Charsets.UTF_8))
        os.close()

        val br = BufferedReader(InputStreamReader(httpConnection.inputStream))
        val sb = StringBuilder()
        var line: String?
        line = br.readLine()
        while (line != null) {
            sb.append("$line\n")
            line = br.readLine()
        }
        br.close()
        sb.toString()

        httpConnection.connect()
        return sb.toString()
    }

    override fun updateDocument(documentId: String, parameters: JSONObject?): String {
        TODO("Not yet implemented")
    }

    override fun deleteDocument(documentId: String): String {
        val requestArguments = JSONObject()
        requestArguments.put("id", documentId)
        val httpConnection =
            getHttpURLConnection(requestArguments, "documents/$documentId", "DELETE")
        httpConnection.connect()
        return "${httpConnection.responseCode}"
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
        TODO("Not yet implemented")
    }

    private fun getHttpURLConnection(
        requestArguments: JSONObject,
        endPoint: String,
        httpVerb: String
    ): HttpURLConnection {
        val date = Date()
        val timeStamp: Long = date.time
        val partnerURL = "${baseUrl}v${apiVersion}/partner"
        val url = URL("$partnerURL/$endPoint/")
        val httpConn = url.openConnection() as HttpURLConnection
        httpConn.requestMethod = httpVerb
        httpConn.connectTimeout = timeOut
        httpConn.setRequestProperty(Constants.USER_AGENT.value, Constants.USER_AGENT_JAVA.value)
        httpConn.setRequestProperty(Constants.ACCEPT.value, Constants.APPLICATION_JSON.value)
        httpConn.setRequestProperty(Constants.CONTENT_TYPE.value, Constants.APPLICATION_JSON.value)
        httpConn.setRequestProperty(Constants.CLIENT_ID.value, httpClientData.clientId)
        httpConn.setRequestProperty(Constants.AUTHORIZATION.value, getApiKey())
        httpConn.setRequestProperty(
            Constants.X_VERYFI_REQUEST_TIMESTAMP.value,
            timeStamp.toString()
        )
        httpConn.setRequestProperty(
            Constants.X_VERYFI_REQUEST_SIGNATURE.value,
            generateSignature(timeStamp, requestArguments)
        )

        return httpConn
    }

    private fun generateSignature(timeStamp: Long, payloadParams: JSONObject): String? {
        payloadParams.put(Constants.TIMESTAMP.value, timeStamp.toString())
        val payload = payloadParams.toString()
        val secretBytes = httpClientData.clientSecret.toByteArray(StandardCharsets.UTF_8)
        val payloadBytes = payload.toByteArray(StandardCharsets.UTF_8)
        val keySpec = SecretKeySpec(secretBytes, SHA256.toString())
        val mac = try {
            Mac.getInstance(SHA256.toString())
        } catch (e: NoSuchAlgorithmException) {
            return e.message
        }
        try {
            mac.init(keySpec)
        } catch (e: InvalidKeyException) {
            return e.message
        }
        val macBytes: ByteArray = mac.doFinal(payloadBytes)
        return String(Base64.encode(macBytes, Base64.DEFAULT))
    }

    private fun getApiKey(): String {
        return "apikey ${httpClientData.username}:${httpClientData.apiKey}"
    }

    /**
     * Creates the JSON Object for the parameters of the request
     * @param fileStream File stream from disk to a file to submit for data extraction
     * @param fileName Name of file to submit for data extraction
     * @param categoriesIn List of categories Veryfi can use to categorize the document
     * @param deleteAfterProcessing Delete this document from Veryfi after data has been extracted
     * @param parameters Additional request parameters
     * @return the JSON object of the parameters of the request
     */
    private fun getProcessDocumentArguments(
        fileStream: InputStream, fileName: String, categoriesIn: List<String?>?,
        deleteAfterProcessing: Boolean, parameters: JSONObject?
    ): JSONObject {
        var categories = categoriesIn
        if (categories == null || categories.isEmpty()) {
            categories = LIST_CATEGORIES
        }
        var base64EncodedString = ""
        try {
            base64EncodedString = String(Base64.encode(fileStream.readBytes(), Base64.DEFAULT))
        } catch (e: IOException) {
            e.printStackTrace()
        }
        val requestArguments: JSONObject = if (parameters != null)
            JSONObject(parameters.toString())
        else
            JSONObject()
        requestArguments.put(Constants.FILE_NAME.value, fileName)
        requestArguments.put(Constants.FILE_DATA.value, base64EncodedString)
        requestArguments.put(Constants.CATEGORIES.value, categories)
        requestArguments.put(Constants.AUTO_DELETE.value, deleteAfterProcessing)
        return requestArguments
    }

    companion object {
        val LIST_CATEGORIES = listOf(
            "Advertising & Marketing",
            "Automotive",
            "Bank Charges & Fees",
            "Legal & Professional Services",
            "Insurance",
            "Meals & Entertainment",
            "Office Supplies & Software",
            "Taxes & Licenses",
            "Travel",
            "Rent & Lease",
            "Repairs & Maintenance",
            "Payroll",
            "Utilities",
            "Job Supplies",
            "Grocery"
        )
    }

}