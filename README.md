https://veryfi.github.io/veryfi-android/

![Veryfi Logo](https://cdn.veryfi.com/logos/veryfi-logo-wide-github.png)

[![Android CI with Maven](https://github.com/veryfi/veryfi-android/actions/workflows/workflow.yml/badge.svg)](https://github.com/veryfi/veryfi-android/actions/workflows/workflow.yml)

**veryfi** is a Android module for communicating with the [Veryfi OCR API](https://veryfi.com/api/)

## Installation

Install from [Maven](https://mvnrepository.com/), a
package manager for Java.


Add in your project build.gradle file the veryfi android SDK dependency:
```ruby
dependencies {
    implementation 'com.veryfi:android:1.0.0'
}
```

## Getting Started

### Obtaining Client ID and user keys
If you don't have an account with Veryfi, please go ahead and register here: [https://hub.veryfi.com/signup/api/](https://hub.veryfi.com/signup/api/)

### Android API Client Library
The **veryfi** library can be used to communicate with Veryfi API. All available functionality is described here https://veryfi.github.io/veryfi-android/android/com.veryfi.android/-client/index.html

Http requests on Android must be performed on a thread different from the main UI thread to avoid android.os.NetworkOnMainThreadException, you can use your favorite way, for this documentation we're going to use a basic Android AsynkTask

Below is the sample kotlin script using **veryfi** to OCR and extract data from a document:

Create a basic Activity
```java
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class MainActivity : AppCompatActivity() {
    
    var clientId = "your_client_id"
    var clientSecret = "your_client_secret"
    var username = "your_username"
    var apiKey = "your_password"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }
}
```

Create a basic layout TextView to set the response
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
        app:layout_constraintBottom_toBottomOf="parent"
        app:layout_constraintLeft_toLeftOf="parent"
        app:layout_constraintRight_toRightOf="parent"
        app:layout_constraintTop_toTopOf="parent" />

</androidx.constraintlayout.widget.ConstraintLayout>
```

Update a document
```java
import veryfi.*;
import org.json.JSONObject;

public class Main {
    public static void main(String[] args) {
        String clientId = "your_client_id";
        String clientSecret = "your_client_secret";
        String username = "your_username";
        String apiKey = "your_password";
        Client client = VeryfiClientFactory.createClient(clientId, clientSecret, username, apiKey);
        String documentId = "your_document_id";
        JSONObject parameters = new JSONObject();
        parameters.put("category", "Meals & Entertainment");
        parameters.put("total", 11.23);
        String jsonResponse = client.updateDocument(documentId, parameters);
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
