package com.veryfi.android

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.veryfi.android.client.HttpClientData
import com.veryfi.android.client.HttpClientImpl
import com.veryfi.android.client.HttpClientInterface
import com.veryfi.android.client.HttpClientMock
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.context.GlobalContext.startKoin
import org.koin.dsl.module

@RunWith(AndroidJUnit4::class)
class VeryfiAndroidOCRAPITest : KoinComponent {

    private val mockClient by inject<HttpClientMock>()
    private val realClient by inject<HttpClientImpl>()

    private val client: HttpClientInterface

    var clientId = "your_client_id"
    var clientSecret = "your_client_secret"
    var username = "your_username"
    var apiKey = "your_password"
    var mockResponses = true // Change to “false” if you want to test your personal credential

    init {
        val httpClientModule = module {
            single { HttpClientData(clientId, clientSecret, username, apiKey) }
            single { HttpClientMock(get()) }
            single { HttpClientImpl(get()) }
        }
        startKoin {
            printLogger()
            modules(httpClientModule)
        }
        client = if (mockResponses) mockClient else realClient
    }

    @Test
    fun testClientMessage() {
        Assert.assertTrue(client.hello() == "your_client_id")
    }
}