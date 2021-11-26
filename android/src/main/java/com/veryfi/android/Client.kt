package com.veryfi.android

import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStream
import java.net.HttpURLConnection

/**
 * Client class to make async calls to Veryfi API.
 */
interface Client {

    /**
     * Returns a json string [String] list of documents.
     * @param onSuccess Async callback in success case.
     * @param onError Async callback in error case.
     */
    fun getDocuments(
        onSuccess: (String) -> Unit,
        onError: (String) -> Unit
    )

    /**
     * Returns a json string [String] document information
     * @param documentId ID of the document you'd like to retrieve.
     * @param onSuccess Async callback in success case.
     * @param onError Async callback in error case.
     */
    fun getDocument(
        documentId: String,
        onSuccess: (String) -> Unit,
        onError: (String) -> Unit
    )

    /**
     * Process a document and extract all the fields from it
     * @param fileStream FileInputStream of the file who contains the document
     * @param fileName Name of the file who contains the document
     * @param categories List of categories Veryfi can use to categorize the document
     * @param deleteAfterProcessing Delete this document from Veryfi after data has been extracted
     * @param parameters Additional request parameters
     * @param onSuccess Async callback in success case.
     * @param onError Async callback in error case.
     */
    fun processDocument(
        fileStream: InputStream,
        fileName: String,
        categories: List<String>,
        deleteAfterProcessing: Boolean,
        parameters: JSONObject?,
        onSuccess: (String) -> Unit,
        onError: (String) -> Unit
    )

    /**
     * Update data for a previously processed document, including almost any field like `vendor`, `date`, `notes` and etc.
     * @param documentId ID of the document you'd like to update.
     * @param parameters Additional request parameters
     * @param onSuccess Async callback in success case.
     * @param onError Async callback in error case.
     */
    fun updateDocument(
        documentId: String,
        parameters: JSONObject,
        onSuccess: (String) -> Unit,
        onError: (String) -> Unit
    )

    /**
     * Delete Document from Veryfi
     * @param documentId ID of the document you'd like to delete.
     * @param onSuccess Async callback in success case.
     * @param onError Async callback in error case.
     */
    fun deleteDocument(
        documentId: String,
        onSuccess: (String) -> Unit,
        onError: (String) -> Unit
    )

    /**
     * Process Document from url and extract all the fields from it.
     * @param fileUrl Required if file_urls isn't specified. Publicly accessible URL to a file, e.g. "https://cdn.example.com/receipt.jpg".
     * @param fileUrls Required if file_url isn't specifies. List of publicly accessible URLs to multiple files, e.g. ["https://cdn.example.com/receipt1.jpg", "https://cdn.example.com/receipt2.jpg"]
     * @param categories List of categories to use when categorizing the document
     * @param deleteAfterProcessing Delete this document from Veryfi after data has been extracted
     * @param maxPagesToProcess When sending a long document to Veryfi for processing, this parameter controls how many pages of the document will be read and processed, starting from page 1.
     * @param boostMode Flag that tells Veryfi whether boost mode should be enabled. When set to 1, Veryfi will skip data enrichment steps, but will process the document faster. Default value for this flag is 0
     * @param externalId Optional custom document identifier. Use this if you would like to assign your own ID to documents
     * @param parameters Additional request parameters
     * @param onSuccess Async callback in success case.
     * @param onError Async callback in error case.
     */
    fun processDocumentUrl(
        fileUrl: String,
        fileUrls: List<String>?,
        categories: List<String>?,
        deleteAfterProcessing: Boolean,
        maxPagesToProcess: Int,
        boostMode: Boolean,
        externalId: String?,
        parameters: JSONObject?,
        onSuccess: (String) -> Unit,
        onError: (String) -> Unit
    )

    /**
     * connect to URL
     * @param httpConnection HttpURLConnection object
     * @return A buffer with the response
     */
    fun connect(
        httpConnection: HttpURLConnection
    ): BufferedReader

}
