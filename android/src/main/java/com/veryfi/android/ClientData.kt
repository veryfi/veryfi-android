package com.veryfi.android

data class ClientData(
    /**
     * clientId the [String] provided by Veryfi.
     */
    val clientId: String,
    /**
     * clientSecret the [String] provided by Veryfi.
     */
    val clientSecret: String,
    /**
     * username the [String] provided by Veryfi.
     */
    val username: String,
    /**
     * apiKey the [String] provided by Veryfi.
     */
    val apiKey: String,
    /**
     * apiVersion [Int] value of current api version by default 8.
     */
    val apiVersion: Int
)