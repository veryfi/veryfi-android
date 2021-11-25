package com.veryfi.android

import android.os.Handler
import android.os.Looper
import android.util.Base64
import android.util.Log
import androidx.annotation.MainThread
import io.reactivex.Observable.just
import org.json.JSONObject
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import java.io.*
import java.net.HttpURLConnection
import java.net.URL
import java.nio.charset.StandardCharsets
import java.security.spec.MGF1ParameterSpec.SHA256
import java.util.*
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

open class ClientImpl(private val clientData: ClientData) : Client {

    private val baseUrl = "https://api.veryfi.com/api/"
    private val timeOut = 120000
    private val apiVersion = clientData.apiVersion
    private val disposables: CompositeDisposable = CompositeDisposable()

    override fun getDocuments(
        @MainThread onSuccess: (String) -> Unit,
        @MainThread onError: (String) -> Unit
    ) {
        val requestArguments = JSONObject()
        val httpConnection = getHttpURLConnection(requestArguments, "documents", "GET")
        asyncConnection(httpConnection, null, onSuccess, onError)
    }

    override fun getDocument(
        documentId: String,
        @MainThread onSuccess: (String) -> Unit,
        @MainThread onError: (String) -> Unit
    ) {
        val requestArguments = JSONObject()
        requestArguments.put("id", documentId)
        val httpConnection = getHttpURLConnection(requestArguments, "documents/$documentId", "GET")
        asyncConnection(httpConnection, null, onSuccess, onError)
    }

    override fun processDocument(
        fileStream: InputStream,
        fileName: String,
        categories: List<String>,
        deleteAfterProcessing: Boolean,
        parameters: JSONObject?,
        @MainThread onSuccess: (String) -> Unit,
        @MainThread onError: (String) -> Unit
    ) {
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
        asyncConnection(httpConnection, requestArguments, onSuccess, onError)
    }

    override fun updateDocument(
        documentId: String,
        parameters: JSONObject,
        @MainThread onSuccess: (String) -> Unit,
        @MainThread onError: (String) -> Unit
    ) {
        val requestArguments: JSONObject = if (parameters.length() > 0)
            JSONObject(parameters.toString())
        else
            return
        val httpConnection =
            getHttpURLConnection(requestArguments, "documents/$documentId", "PUT")
        asyncConnection(httpConnection, requestArguments, onSuccess, onError)
    }

    override fun deleteDocument(
        documentId: String,
        @MainThread onSuccess: (String) -> Unit,
        @MainThread onError: (String) -> Unit) {
        val requestArguments = JSONObject()
        requestArguments.put("id", documentId)
        val httpConnection =
            getHttpURLConnection(requestArguments, "documents/$documentId", "DELETE")
        asyncConnection(httpConnection, null, onSuccess, onError)
    }

    override fun processDocumentUrl(
        fileUrl: String,
        fileUrls: List<String>?,
        categories: List<String>?,
        deleteAfterProcessing: Boolean,
        maxPagesToProcess: Int,
        boostMode: Boolean,
        externalId: String?,
        parameters: JSONObject?,
        @MainThread onSuccess: (String) -> Unit,
        @MainThread onError: (String) -> Unit
    ) {
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
        asyncConnection(httpConnection, requestArguments, onSuccess, onError)
    }

    override fun connect(
        httpConnection: HttpURLConnection
    ): BufferedReader {
        if (httpConnection.requestMethod == "GET" || httpConnection.requestMethod == "DELETE") {
            httpConnection.connect()
        }
        val inputStream: InputStream = try {
            httpConnection.inputStream
        } catch (e: IOException) {
            print(e.message)
            "{status:fail, code:${httpConnection.responseCode}}".byteInputStream()
        }
        return BufferedReader(InputStreamReader(inputStream))
    }

    private fun asyncConnection(
        httpConnection: HttpURLConnection,
        requestArguments: JSONObject?,
        onSuccess: (String) -> Unit,
        onError: (String) -> Unit
    ) {
        just(httpConnection)
            .doOnNext { httpURLConnection ->
                requestArguments?.let{
                    writeOutputStream(httpConnection, it)
                }
                val jsonResponse = processBufferedReader(connect(httpURLConnection))
                val mainHandler = Handler(Looper.getMainLooper())
                mainHandler.post {
                    onSuccess(jsonResponse)
                }
            }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ data ->
                Log.d(TAG, "Consuming item " + data.url)
            }, { error ->
                val mainHandler = Handler(Looper.getMainLooper())
                mainHandler.post {
                    onError("Veryfi client Error: " + error.localizedMessage)
                }
            }).let {
                disposables.add(it)
            }
    }

    /**
     * Write outputStream by POST or PUT request.
     * @param httpConnection [HttpURLConnection] httpURLConnection to process
     * @param requestArguments [JSONObject] requestArgument to send.
     */
    private fun writeOutputStream(
        httpConnection: HttpURLConnection,
        requestArguments: JSONObject
    ) {
        httpConnection.doInput = true
        httpConnection.doOutput = true
        if (requestArguments.length() == 0) return
        val outputStream: OutputStream = httpConnection.outputStream
        outputStream.write(requestArguments.toString().toByteArray(Charsets.UTF_8))
        outputStream.close()
    }

    /**
     * process the buffer to convert it to a string
     * @param bufferedReader [BufferedReader] gotten by connection.
     * @return [String] String in JSON format with the response.
     */
    private fun processBufferedReader(bufferedReader: BufferedReader): String {
        val stringBuilder = StringBuilder()
        var line: String?
        line = bufferedReader.readLine()
        while (line != null) {
            stringBuilder.append("$line\n")
            line = bufferedReader.readLine()
        }
        bufferedReader.close()
        stringBuilder.toString()
        return stringBuilder.toString()
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
        val httpConnection = url.openConnection() as HttpURLConnection
        httpConnection.requestMethod = httpVerb
        httpConnection.connectTimeout = timeOut
        httpConnection.setRequestProperty(
            Constants.USER_AGENT.value,
            Constants.USER_AGENT_KOTLIN.value
        )
        httpConnection.setRequestProperty(Constants.ACCEPT.value, Constants.APPLICATION_JSON.value)
        httpConnection.setRequestProperty(
            Constants.CONTENT_TYPE.value,
            Constants.APPLICATION_JSON.value
        )
        httpConnection.setRequestProperty(Constants.CLIENT_ID.value, clientData.clientId)
        httpConnection.setRequestProperty(Constants.AUTHORIZATION.value, getApiKey())
        httpConnection.setRequestProperty(
            Constants.X_VERYFI_REQUEST_TIMESTAMP.value,
            timeStamp.toString()
        )
        val signature = generateSignature(timeStamp, requestArguments)
        httpConnection.setRequestProperty(
            Constants.X_VERYFI_REQUEST_SIGNATURE.value,
            signature
        )
        return httpConnection
    }

    /**
     * Generate unique signature for payload params.
     * @param timeStamp Unix Long timestamp
     * @param payloadParams JSON params to be sent to API request
     * @return Unique signature generated using the client_secret and the payload
     */
    private fun generateSignature(timeStamp: Long, payloadParams: JSONObject): String? {
        val jsonPayload = JSONObject(payloadParams.toString())
        jsonPayload.put(Constants.TIMESTAMP.value, timeStamp.toString())
        val payload = jsonPayload.toString()
        val secretBytes = clientData.clientSecret.toByteArray(StandardCharsets.UTF_8)
        val payloadBytes = payload.toByteArray(StandardCharsets.UTF_8)
        val keySpec = SecretKeySpec(secretBytes, SHA256.toString())
        val mac = Mac.getInstance("HmacSHA256")
        mac.init(keySpec)
        val macBytes: ByteArray = mac.doFinal(payloadBytes)
        return Base64.encodeToString(macBytes, Base64.DEFAULT)
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
        val categories = if (categoriesIn == null || categoriesIn.isEmpty())
            LIST_CATEGORIES else categoriesIn
        val base64EncodedString = Base64.encodeToString(fileStream.readBytes(), Base64.DEFAULT)
        val requestArguments: JSONObject = if (parameters != null)
            JSONObject(parameters.toString()) else JSONObject()
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
        val requestArguments = if (parameters != null)
            JSONObject(parameters.toString()) else JSONObject()
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
        const val TAG = "VeryfiClient"
    }

}
