package com.veryfi.android

import org.json.JSONObject
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.mockito.kotlin.anyOrNull
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.spy
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader


class ClientTest {

    //credentials
    private val clientId = "clientId"
    private val clientSecret = "clientSecret"
    private val username = "username"
    private val apiKey = "apiKey"
    private val receiptPath = "receipt.jpeg"

    private var client = VeryfiClientFactory.createClient(clientId, clientSecret, username, apiKey)

    private val mockResponses =
        true // Change to “false” if you want to test your personal credential

    @Test
    fun getDocumentsTest() {
        if (mockResponses) {
            client = spy(VeryfiClientFactory.createClient(clientId, clientSecret, username, apiKey))
            val bufferedReader = getFileAsBufferedReader("getDocuments.json")
            doReturn(bufferedReader).`when`(client).connect(anyOrNull())
        }
        val jsonResponse = JSONObject(client.getDocuments())
        print(jsonResponse)
        assertEquals(2, jsonResponse.length())
    }

    @Test
    fun getDocumentTest() {
        val documentId: Int
        if (mockResponses) {
            client = spy(VeryfiClientFactory.createClient(clientId, clientSecret, username, apiKey))
            documentId = 31727276
            val bufferedReader = getFileAsBufferedReader("getDocument.json")
            doReturn(bufferedReader).`when`(client).connect(anyOrNull())
        } else {
            val documents = JSONObject(client.getDocuments())
            if (documents.length() < 1) {
                print ("NO DOCUMENTS IN YOUR ACCOUNT")
                assertTrue(false)
                return
            }
            documentId = documents.getJSONArray("documents").getJSONObject(0).getInt("id")
        }
        val jsonResponse = JSONObject(client.getDocument(documentId.toString()))
        assertEquals(documentId, jsonResponse.getInt("id"))
    }

    @Test
    fun processDocumentTest() {
        val categories: List<String> = listOf("Advertising & Marketing", "Automotive")
        if (mockResponses) {
            client = spy(VeryfiClientFactory.createClient(clientId, clientSecret, username, apiKey))
            val bufferedReader = getFileAsBufferedReader("processDocument.json")
            doReturn(bufferedReader).`when`(client).connect(anyOrNull())
        }
        this::class.java.classLoader?.let {
            val classLoader: ClassLoader = it
            val inputStream: InputStream = classLoader.getResourceAsStream(receiptPath)
            val jsonResponse = JSONObject(client.processDocument(inputStream, receiptPath, categories, true, null))
            assertEquals("In-n-out Burger", jsonResponse.getJSONObject("vendor").getString("name"))
        }?: run {
            throw java.lang.Exception("Can't get class loader")
        }
    }

    @Test
    fun updateDocumentTest() {
        val documentId: Int
        val notes: String
        val parameters = JSONObject()
        if (mockResponses) {
            client = spy(VeryfiClientFactory.createClient(clientId, clientSecret, username, apiKey))
            val bufferedReader = getFileAsBufferedReader("updateDocument.json")
            notes = "Note updated"
            parameters.put("notes", notes)
            documentId = 31727276
            doReturn(bufferedReader).`when`(client).connect(anyOrNull())
        } else {
            notes = generateRandomString()
            parameters.put("notes", notes)
            val documents = JSONObject(client.getDocuments())
            documentId = documents.getJSONArray("documents").getJSONObject(0).getInt("id")
        }
        val jsonResponse = JSONObject(client.updateDocument(documentId.toString(), parameters))
        assertEquals(notes, jsonResponse.getString("notes"))
    }

    @Test
    fun deleteDocumentTest() {
        val id: String
        if (mockResponses) {
            client = spy(VeryfiClientFactory.createClient(clientId, clientSecret, username, apiKey))
            val bufferedReader = getFileAsBufferedReader("deleteDocument.json")
            doReturn(bufferedReader).`when`(client).connect(anyOrNull())
            id = "46247222"
        } else {
            val url = "https://veryfi-testing-public.s3.us-west-2.amazonaws.com/receipt.jpg"
            val categories: List<String> = listOf("Advertising & Marketing", "Automotive")
            val jsonResponse = JSONObject(client.processDocumentUrl(
                url,
                null,
                categories,
                false,
                1,
                true,
                null,
                null))
            id = jsonResponse.getInt("id").toString()
        }
        val deleteJsonResponse = JSONObject(client.deleteDocument(id))
        val jsonExpectedDeleteResponse = JSONObject("{status:ok,message:Document has been deleted}")
        assertEquals(
            jsonExpectedDeleteResponse.getString("status"),
            deleteJsonResponse.getString("status")
        )
        assertEquals(
            jsonExpectedDeleteResponse.getString("message"),
            deleteJsonResponse.getString("message")
        )
    }

    @Test
    fun processDocumentUrlTest() {
        if (mockResponses) {
            client = spy(VeryfiClientFactory.createClient(clientId, clientSecret, username, apiKey))
            val bufferedReader = getFileAsBufferedReader("processDocument.json")
            doReturn(bufferedReader).`when`(client).connect(anyOrNull())
        }
        val url = "https://veryfi-testing-public.s3.us-west-2.amazonaws.com/receipt.jpg"
        val jsonResponse = JSONObject(client.processDocumentUrl(
            url,
            null,
            null,
            true,
            1,
            true,
            null,
            null
        ))
        assertEquals("In-n-out Burger", jsonResponse.getJSONObject("vendor").getString("name"))
    }

    @Test
    fun processBadCredentialsTest() {
        client = VeryfiClientFactory.createClient(
            "badClientId",
            "badClientSecret",
            "badUsername",
            "badApiKey"
        )
        val getDocumentsResponse = client.getDocuments()
        val jsonResponse = JSONObject(getDocumentsResponse)
        assertEquals("fail", jsonResponse.getString("status")) // TODO we need return status fails and the fail code.
    }

    private fun getFileAsBufferedReader(fileName: String): BufferedReader? {
        this::class.java.classLoader?.let {
            val classLoader: ClassLoader = it
            val inputStream: InputStream = classLoader.getResourceAsStream(fileName)
            return BufferedReader(InputStreamReader(inputStream, "UTF-8"))
        }
        return null
    }

    private fun generateRandomString(): String {
        val allowedChars = ('A'..'Z') + ('a'..'z') + ('0'..'9')
        return (1..10)
            .map { allowedChars.random() }
            .joinToString("")
    }
}
