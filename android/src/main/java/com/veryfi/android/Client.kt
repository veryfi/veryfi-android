package com.veryfi.android

import org.json.JSONObject
import java.io.InputStream

interface Client {

    /**
     * Returns a json string [String] list of documents.
     * @return the list of previously processed documents [String]
     */
    fun getDocuments(): String

    /**
     * Returns a json string [String] document information
     * @param documentId ID of the document you'd like to retrieve.
     * @return the data extracted from the Document [String]
     */
    fun getDocument(documentId: String): String

    /**
     * Process a document and extract all the fields from it
     * @param fileStream FileInputStream of the file who contains the document
     * @param fileName Name of the file who contains the document
     * @param categories List of categories Veryfi can use to categorize the document
     * @param deleteAfterProcessing Delete this document from Veryfi after data has been extracted
     * @param parameters Additional request parameters
     * @return the data extracted from the Document [String]
     */
    fun processDocument(
        fileStream: InputStream,
        fileName: String,
        categories: List<String>,
        deleteAfterProcessing: Boolean,
        parameters: JSONObject?
    ): String

    /**
     * Update data for a previously processed document, including almost any field like `vendor`, `date`, `notes` and etc.
     * @param documentId ID of the document you'd like to update.
     * @param parameters Additional request parameters
     * @return A document json with updated fields, if fields are writable. Otherwise a document with unchanged fields. [String]
     */
    fun updateDocument(documentId: String, parameters: JSONObject?): String

    /**
     * Delete Document from Veryfi
     * @param documentId ID of the document you'd like to delete.
     * @return the response data. [String]
     */
    fun deleteDocument(documentId: String): String

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
     * @return the data extracted from the Document [String]
     */
    fun processDocumentUrl(
        fileUrl: String,
        fileUrls: List<String>?,
        categories: List<String>?,
        deleteAfterProcessing: Boolean,
        maxPagesToProcess: Int,
        boostMode: Boolean,
        externalId: String?,
        parameters: JSONObject?
    ): String
}