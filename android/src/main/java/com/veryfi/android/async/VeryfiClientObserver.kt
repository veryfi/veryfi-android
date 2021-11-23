package com.veryfi.android.async

import android.util.Log
import io.reactivex.Observer
import io.reactivex.disposables.Disposable
import java.io.BufferedReader
import java.net.HttpURLConnection

class VeryfiClientObserver(
    val onSuccess: (String) -> Unit,
    val onClientError: (String) -> Unit,
    val connect: (HttpURLConnection) -> BufferedReader,
    val processBufferedReader: (BufferedReader) -> String
) : Observer<HttpURLConnection> {

    override fun onSubscribe(disposable: Disposable) {
    }

    override fun onNext(httpURLConnection: HttpURLConnection) {
        onSuccess(processBufferedReader(connect(httpURLConnection)))
        httpURLConnection.disconnect()
    }

    override fun onError(e: Throwable) {
        Log.e(TAG, "Http client request error: " + e.message)
        onClientError("onError: " + e.message)
    }

    override fun onComplete() {
        Log.d(TAG, "Http client request complete")
    }

    companion object {
        const val TAG = "VeryfiClient"
    }
}
