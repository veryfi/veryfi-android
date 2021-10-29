package com.veryfi.android.client

class HttpClientImpl(private val httpClientData: HttpClientData) : HttpClientInterface {

    override fun hello() : String{
        return httpClientData.clientId
    }
}