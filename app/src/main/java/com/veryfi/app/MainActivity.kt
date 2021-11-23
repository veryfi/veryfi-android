package com.veryfi.app

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import com.veryfi.android.VeryfiClientFactory

class MainActivity : AppCompatActivity() {

    private var clientId = "vrfdR0ALCMGfx32RO0gMFyfZeJT7UCliNxZZLnG"
    private var clientSecret = "dNjx4jrzTxHjjn0cCLeApnz2ujHIsh3BtX6AHoRbhFLOOdctv785PItaORS1BowTeOzY67mlVj1KxR3k6h9gPvlExi4KLRAxFV8oHptHyJS7WY482Z0gYxZWvcxNMh1B"
    private var username = "devapitest"
    private var apiKey = "1171431d8e6eb8478a50d872bcb2dc51"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val client = VeryfiClientFactory.createClient(clientId, clientSecret, username, apiKey)
        client.getDocuments({ jsonString ->
            //Update UI with jsonString response
            findViewById<TextView>(R.id.response).text = jsonString
        }, {
            //handle errorMessage
        })
    }
}