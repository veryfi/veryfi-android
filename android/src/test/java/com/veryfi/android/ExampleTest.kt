package com.veryfi.android

import org.junit.Test
import veryfi.VeryfiClientFactory

class ExampleTest {
    private var clientId = "vrfdR0ALCMGfx32RO0gMFyfZeJT7UCliNxZZLnG"
    private var clientSecret = "dNjx4jrzTxHjjn0cCLeApnz2ujHIsh3BtX6AHoRbhFLOOdctv785PItaORS1BowTeOzY67mlVj1KxR3k6h9gPvlExi4KLRAxFV8oHptHyJS7WY482Z0gYxZWvcxNMh1B"
    private var username = "devapitest"
    private var apiKey = "1171431d8e6eb8478a50d872bcb2dc51"

    @Test
    fun sampleTest(){
        val client= VeryfiClientFactory.createClient(clientId, clientSecret, username, apiKey)

    }
}