package com.veryfi.android.client

class HttpClientMock(private val httpClientData: HttpClientData) : HttpClientInterface{
    override fun hello() : String{
        return httpClientData.clientId
    }
}