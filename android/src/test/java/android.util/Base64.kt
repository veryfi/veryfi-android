package android.util

import java.util.Base64


object Base64 {

    @JvmStatic
    fun encodeToString(input: ByteArray?, flags: Int): String {
        return Base64.getEncoder().encodeToString(input)
    }

}
