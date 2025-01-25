package com.my.darkmatter

import android.os.Bundle
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val webView: WebView = findViewById(R.id.webview)
        webView.webViewClient = WebViewClient()

        // Enable JavaScript
        val webSettings = webView.settings
        webSettings.javaScriptEnabled = true

        // Disable scrolling by setting an empty touch listener
        webView.setOnTouchListener { _, _ -> true }

        // Load the webpage
        webView.loadUrl("https://gen-image-three.vercel.app/")
    }

    // Handle back button to go back in WebView history
    override fun onBackPressed() {
        val webView: WebView = findViewById(R.id.webview)
        if (webView.canGoBack()) {
            webView.goBack()
        } else {
            super.onBackPressed()
        }
    }
}
