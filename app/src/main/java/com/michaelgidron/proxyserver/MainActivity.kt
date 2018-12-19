package com.michaelgidron.proxyserver


import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.webkit.WebView
import android.util.Log
import android.webkit.WebChromeClient
import android.webkit.WebViewClient
import com.good.gd.GDAndroid
import com.good.gd.GDStateListener
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread


class MainActivity : AppCompatActivity(), GDStateListener {

    private var webView: WebView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        GDAndroid.getInstance().activityInit(this);
        webView = findViewById(R.id.webView_MainActivity)

    }

    fun setupWebView() {
        webView?.settings?.javaScriptEnabled = true
        webView?.settings?.setAppCacheEnabled(true)
        webView?.settings?.databaseEnabled = true
        webView?.settings?.domStorageEnabled = true
        webView?.settings?.javaScriptCanOpenWindowsAutomatically = true
        webView?.webChromeClient = WebChromeClient()
    }

    fun onLoadClick(view: View) {
        //webView?.loadUrl("http://127.0.0.1:8081/url/login.salesforce.com")
        webView?.loadUrl("http://127.0.0.1:8081/url/https://login.salesforce.com")
    }

    fun onStartServer(view: View) {

        doAsync {
            ProxyServer.runServer()

            uiThread {
               Log.i(LOG_KEY, "MY TEST")
            }
        }
    }

    override fun onLocked() {

    }

    override fun onWiped() {

    }

    override fun onUpdateConfig(map: MutableMap<String, Any>?) {

    }

    override fun onUpdateServices() {

    }

    override fun onAuthorized() {
        webView?.loadUrl("https://www.google.com")
    }

    override fun onUpdateEntitlements() {

    }

    override fun onUpdatePolicy(map: MutableMap<String, Any>?) {

    }

}
