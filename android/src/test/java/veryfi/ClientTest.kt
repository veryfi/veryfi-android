package veryfi

import org.json.JSONArray
import org.json.JSONObject
import org.junit.Assert.*
import org.junit.Test
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.context.GlobalContext.startKoin
import org.koin.dsl.module
import java.io.InputStream

class ClientTest : KoinComponent {

    private val mockClient by inject<ClientMock>()
    private val realClient by inject<ClientImpl>()

    private val client: Client

    private var clientId = "your_client_id"
    private var clientSecret = "your_client_secret"
    private var username = "your_username"
    private var apiKey = "your_password"

    private var mockResponses =
        true // Change to “false” if you want to test your personal credential

    init {
        val httpClientModule = module {
            single { ClientMock() }
            single { VeryfiClientFactory.createClient(clientId, clientSecret, username, apiKey) }
        }
        try {
            startKoin {
                printLogger()
                modules(httpClientModule)
            }
        } catch (e: Exception) {
        }
        client = if (mockResponses) mockClient else realClient
    }

    @Test
    fun getDocumentsTest() {
        val documents = JSONArray(client.getDocuments())
        assertTrue(documents.length() > 0)
    }

    @Test
    fun getDocumentTest() {
        val documentId = "31727276"
        val jsonResponse = client.getDocument(documentId)
        val document = JSONObject(jsonResponse)
        assertEquals(
            document.getInt("id"),
            documentId.toInt()
        )
    }

    @Test
    fun processDocumentTest() {
        this::class.java.classLoader?.let {
            val categories: List<String> = listOf("Advertising & Marketing", "Automotive")

            val classLoader: ClassLoader = it
            val inputStream: InputStream = classLoader.getResourceAsStream("receipt.png")

            val jsonResponse: String = client.processDocument(inputStream,"receipt.png", categories, false, null)
            val document = JSONObject(jsonResponse)
            assertEquals(
                "The Home Depot",
                document.getJSONObject("vendor").getString("name")
            )
            inputStream.close()
        }?: run{
            throw java.lang.Exception("Can't get class loader")
        }

    }

    @Test
    fun updateDocumentTest() {
        val documentId = "31727276" // Change to your document Id
        val parameters = JSONObject()
        val notes = "Note updated"
        parameters.put("notes", notes)
        val jsonResponseUpdated: String = client.updateDocument(documentId, parameters)
        val documentJson = JSONObject(jsonResponseUpdated)
        assertEquals(notes, documentJson.getString("notes"))
    }

    @Test
    fun deleteDocumentTest(){
        val documentId = "36348006" // Change to your document Id
        val jsonResponse: String = client.deleteDocument(documentId)
        assertFalse(jsonResponse.isEmpty())
    }

    @Test
    fun processDocumentUrlTest(){
        val jsonResponse: String = client.processDocumentUrl(
            "https://cdn.veryfi.com/receipts/92233902-c94a-491d-a4f9-0d61f9407cd2.pdf",
            null,
            null,
            false,
            1,
            false,
            null,
            null
        )
        val document = JSONObject(jsonResponse)
        assertEquals(
            "Rumpke",
            document.getJSONObject("vendor").getString("name")
        )
    }
}