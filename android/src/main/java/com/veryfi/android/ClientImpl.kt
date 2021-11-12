package com.veryfi.android

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

class ClientImpl(private val clientData: ClientData) : Client {

    private val baseUrl = "https://api.veryfi.com/api/"
    private val timeOut = 120000
    private val apiVersion = 7

    override fun getDocuments(): String {
        val requestArguments = JSONObject()
        val httpConnection = getHttpURLConnection(requestArguments, "documents", "GET")
        httpConnection.connect() // mockee acá
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
        val requestArguments: JSONObject = if (parameters != null)
            JSONObject(parameters.toString())
        else
            JSONObject()
        requestArguments.put("id", documentId)
        val httpConnection =
            getHttpURLConnection(requestArguments, "documents/$documentId", "PUT")
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
        val requestArguments: JSONObject = getProcessDocumentUrlArguments(
            fileUrl,
            fileUrls,
            categories,
            deleteAfterProcessing,
            maxPagesToProcess,
            boostMode,
            externalId,
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

    /**
     * Generate unique signature for payload params.
     * @param requestArguments JSON params to be sent to API request
     * @param endPoint API endpoint
     * @param httpVerb , Http verb: POST, DELETE, PUT, GET
     * @return httpURLConnection used for http requests
     */
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
        httpConn.setRequestProperty(Constants.USER_AGENT.value, Constants.USER_AGENT_KOTLIN.value)
        httpConn.setRequestProperty(Constants.ACCEPT.value, Constants.APPLICATION_JSON.value)
        httpConn.setRequestProperty(Constants.CONTENT_TYPE.value, Constants.APPLICATION_JSON.value)
        httpConn.setRequestProperty(Constants.CLIENT_ID.value, clientData.clientId)
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

    /**
     * Generate unique signature for payload params.
     * @param timeStamp Unix Long timestamp
     * @param payloadParams JSON params to be sent to API request
     * @return Unique signature generated using the client_secret and the payload
     */
    private fun generateSignature(timeStamp: Long, payloadParams: JSONObject): String? {
        payloadParams.put(Constants.TIMESTAMP.value, timeStamp.toString())
        val payload = payloadParams.toString()
        val secretBytes = clientData.clientSecret.toByteArray(StandardCharsets.UTF_8)
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
        return "apikey ${clientData.username}:${clientData.apiKey}"
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

    /**
     * Creates the JSON object of the parameters of the request
     * @param fileUrl Required if file_urls isn't specified. Publicly accessible URL to a file, e.g. "https://cdn.example.com/receipt.jpg".
     * @param fileUrls Required if file_url isn't specifies. List of publicly accessible URLs to multiple files, e.g. ["https://cdn.example.com/receipt1.jpg", "https://cdn.example.com/receipt2.jpg"]
     * @param categoriesIn List of categories to use when categorizing the document
     * @param deleteAfterProcessing Delete this document from Veryfi after data has been extracted
     * @param maxPagesToProcess When sending a long document to Veryfi for processing, this parameter controls how many pages of the document will be read and processed, starting from page 1.
     * @param boostMode Flag that tells Veryfi whether boost mode should be enabled. When set to 1, Veryfi will skip data enrichment steps, but will process the document faster. Default value for this flag is 0
     * @param externalId Optional custom document identifier. Use this if you would like to assign your own ID to documents
     * @param parameters Additional request parameters
     * @return JSON object of the request arguments
     */
    private fun getProcessDocumentUrlArguments(
        fileUrl: String?, fileUrls: List<String?>?, categoriesIn: List<String?>?,
        deleteAfterProcessing: Boolean, maxPagesToProcess: Int,
        boostMode: Boolean, externalId: String?, parameters: JSONObject?
    ): JSONObject {
        var categories = categoriesIn
        if (categories == null || categories.isEmpty()) {
            categories = LIST_CATEGORIES
        }
        val requestArguments: JSONObject = if (parameters != null)
            JSONObject(parameters.toString())
        else
            JSONObject()
        requestArguments.put(Constants.AUTO_DELETE.value, deleteAfterProcessing)
        requestArguments.put(Constants.BOOST_MODE.value, boostMode)
        requestArguments.put(Constants.CATEGORIES.value, categories)
        requestArguments.put(Constants.EXTERNAL_ID.value, externalId)
        requestArguments.put(Constants.FILE_URL.value, fileUrl)
        if (fileUrls != null) {
            requestArguments.put(Constants.FILE_URLS.value, fileUrls)
        }
        requestArguments.put(Constants.MAX_PAGES_TO_PROCESS.value, maxPagesToProcess)
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