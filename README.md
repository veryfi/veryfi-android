<img src="https://user-images.githubusercontent.com/30125790/212157461-58bdc714-2f89-44c2-8e4d-d42bee74854e.png#gh-dark-mode-only" width="200">
<img src="https://user-images.githubusercontent.com/30125790/212157486-bfd08c5d-9337-4b78-be6f-230dc63838ba.png#gh-light-mode-only" width="200">

[![License: MIT](https://img.shields.io/badge/License-MIT-green.svg)](https://opensource.org/licenses/MIT)
[![code coverage](./metrics/code_coverage.svg)](./metrics/code_coverage.svg)

**veryfi** is a Android module for communicating with the [Veryfi OCR API](https://veryfi.com/api/)

## Installation

Install from [Maven](https://mvnrepository.com/), a
package manager for Java.


Add in your project build.gradle file the veryfi android SDK dependency:
```ruby
dependencies {
    implementation 'com.veryfi:veryfi-android:1.0.8'
}
```

## Getting Started

### Obtaining Client ID and user keys
If you don't have an account with Veryfi, please go ahead and register here: [https://hub.veryfi.com/signup/api/](https://hub.veryfi.com/signup/api/)

### Android API Client Library
The **veryfi** library can be used to communicate with Veryfi API. All available functionality is described here https://veryfi.github.io/veryfi-android/android/com.veryfi.android/-client/index.html

Below is the sample kotlin script using **veryfi** to OCR and extract data from a document:

Create a basic layout with a TextView to set the response
```xml
<?xml version="1.0" encoding="utf-8"?>
<androidx.constraintlayout.widget.ConstraintLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    xmlns:tools="http://schemas.android.com/tools"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    tools:context=".MainActivity">

    <TextView
        android:id="@+id/response"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:textColor="#535353"
        app:layout_constraintBottom_toBottomOf="parent"
        app:layout_constraintLeft_toLeftOf="parent"
        app:layout_constraintRight_toRightOf="parent"
        app:layout_constraintTop_toTopOf="parent" />

</androidx.constraintlayout.widget.ConstraintLayout>
```

Process a document
```java
class MainActivity : AppCompatActivity() {

    var clientId = "your_client_id"
    var clientSecret = "your_client_secret"
    var username = "your_username"
    var apiKey = "your_password"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val client = VeryfiClientFactory.createClient(clientId, clientSecret, username, apiKey)
        val categories = listOf("Advertising & Marketing", "Automotive")
        val fileName = "example1.jpg"
        client.processDocument(assets.open(fileName), fileName, categories, false, null, { jsonString ->
            //Update UI with jsonString response
            findViewById<TextView>(R.id.response).text = jsonString
        }, { errorMessage ->
            //handle errorMessage
        })
    }
}
```

Update a document
```java
class MainActivity : AppCompatActivity() {

    var clientId = "your_client_id"
    var clientSecret = "your_client_secret"
    var username = "your_username"
    var apiKey = "your_password"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setContentView(R.layout.activity_main)
        val documentId = "your_document_id"
        val parameters = JSONObject()
        parameters.put("category", "Meals & Entertainment")
        parameters.put("total", 11.23)
        client.updateDocument(documentId, parameters, { jsonString ->
            //Update UI with jsonString response
            findViewById<TextView>(R.id.response).text = jsonString
        }, { errorMessage ->
            //handle errorMessage
        })
    }
}
```

## Need help?
If you run into any issue or need help installing or using the library, please contact support@veryfi.com.

If you found a bug in this library or would like new features added, then open an issue or pull requests against this repo!

To learn more about Veryfi visit https://www.veryfi.com/

## Tutorial
Below is an introduction to the Android SDK.

[Link to blog post →](https://www.veryfi.com/android/)
