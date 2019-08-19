package com.google.android.flexbox

import android.app.Activity
import android.app.AlertDialog
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.net.NetworkInfo
import android.net.wifi.WifiConfiguration
import android.net.wifi.WifiInfo
import android.net.wifi.WifiManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

import com.google.android.apps.flexbox.R
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.webview.*





//public class IntroActivity : AppCompatActivity(){
class WebViewActivity : AppCompatActivity() {
    val TAG:String="WebViewActivity";
    private val url = "https://www.google.com"
    object Config{
        const val SSID="\"goodluck\""
        const val PASS="\"goodluck\""
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.webview)

        // Get the web view settings instance
        val settings = web_view.settings

        // Enable java script in web view
        settings.javaScriptEnabled = true

        // Enable and setup web view cache
        settings.setAppCacheEnabled(true)
        settings.cacheMode = WebSettings.LOAD_DEFAULT
        settings.setAppCachePath(cacheDir.path)


        // Enable zooming in web view
        settings.setSupportZoom(true)
        settings.builtInZoomControls = true
        settings.displayZoomControls = true


        // Zoom web view text
        settings.textZoom = 125


        // Enable disable images in web view
        settings.blockNetworkImage = false
        // Whether the WebView should load image resources
        settings.loadsImagesAutomatically = true


        // More web view settings
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            settings.safeBrowsingEnabled = true  // api 26
        }
        //settings.pluginState = WebSettings.PluginState.ON
        settings.useWideViewPort = true
        settings.loadWithOverviewMode = true
        settings.javaScriptCanOpenWindowsAutomatically = true
        settings.mediaPlaybackRequiresUserGesture = false


        // More optional settings, you can enable it by yourself
        settings.domStorageEnabled = true
        settings.setSupportMultipleWindows(true)
        settings.loadWithOverviewMode = true
        settings.allowContentAccess = true
        settings.setGeolocationEnabled(true)
        settings.allowUniversalAccessFromFileURLs = true
        settings.allowFileAccess = true

        // WebView settings
        web_view.fitsSystemWindows = true


        /*
            if SDK version is greater of 19 then activate hardware acceleration
            otherwise activate software acceleration
        */
        web_view.setLayerType(View.LAYER_TYPE_HARDWARE, null)


        // Set web view client
        web_view.webViewClient = object: WebViewClient(){
            override fun onPageStarted(view: WebView, url: String, favicon: Bitmap?) {
                // Page loading started
                // Do something
                toast("Page loading.")

                // Enable disable back forward button
                button_back.isEnabled = web_view.canGoBack()
                button_forward.isEnabled = web_view.canGoForward()
            }

            override fun onPageFinished(view: WebView, url: String) {
                // Page loading finished
                // Display the loaded page title in a toast message
                toast("Page loaded: ${view.title}")

                // Enable disable back forward button
                button_back.isEnabled = web_view.canGoBack()
                button_forward.isEnabled = web_view.canGoForward()
            }
        }


        // Set web view chrome client
        web_view.webChromeClient = object: WebChromeClient(){
            override fun onProgressChanged(view: WebView, newProgress: Int) {
                progress_bar.progress = newProgress
            }
        }


        // Load button click listener
        button_load.setOnClickListener{
            // Load url in a web view
            web_view.loadUrl(url)
        }


        // Back button click listener
        button_back.setOnClickListener{
            if(web_view.canGoBack()){
                // Go to back history
                web_view.goBack()
            }
        }


        // Forward button click listener
        button_forward.setOnClickListener{
            if(web_view.canGoForward()){
                // Go to forward history
                web_view.goForward()
            }
        }

        Wifi_Connect.setOnClickListener{
            toast("Changing wifi to "+Config.SSID)
            connectToWPAWiFi(Config.SSID,Config.PASS)
        }
    }



    //connects to the given ssid
    fun connectToWPAWiFi(ssid:String,pass:String){
        if(isConnectedTo(ssid)){ //see if we are already connected to the given ssid
            toast("Connected to"+ssid)
            return
        }
        val wm: WifiManager = applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
        var wifiConfig=getWiFiConfig(ssid)
        if(wifiConfig==null){//if the given ssid is not present in the WiFiConfig, create a config for it
            createWPAProfile(ssid,pass)
            wifiConfig=getWiFiConfig(ssid)
        }
        wm.disconnect()
        wm.enableNetwork(wifiConfig!!.networkId,true)
        wm.reconnect()
        Log.d(TAG,"intiated connection to SSID"+ssid);
    }
    fun isConnectedTo(ssid: String):Boolean{
        val wm:WifiManager= applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
        if(wm.connectionInfo.ssid == ssid){
            return true
        }
        return false
    }
    fun getWiFiConfig(ssid: String): WifiConfiguration? {
        val wm:WifiManager= applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
        val wifiList=wm.configuredNetworks
        for (item in wifiList){
            if(item.SSID != null && item.SSID.equals(ssid)){
                return item
            }
        }
        return null
    }
    fun createWPAProfile(ssid: String,pass: String){
        Log.d(TAG,"Saving SSID :"+ssid)
        val conf = WifiConfiguration()
        conf.SSID = ssid
        conf.preSharedKey = pass
        val wm:WifiManager= applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
        wm.addNetwork(conf)
        Log.d(TAG,"saved SSID to WiFiManger")
    }

    class WiFiChngBrdRcr : BroadcastReceiver(){ // shows a toast message to the user when device is connected to a AP
        private val TAG = "WiFiChngBrdRcr"
        override fun onReceive(context: Context, intent: Intent) {
            val networkInfo=intent.getParcelableExtra<NetworkInfo>(WifiManager.EXTRA_NETWORK_INFO)
            if(networkInfo.state == NetworkInfo.State.CONNECTED){
                val bssid=intent.getStringExtra(WifiManager.EXTRA_BSSID)
                Log.d(TAG, "Connected to BSSID:"+bssid)
                val ssid=intent.getParcelableExtra<WifiInfo>(WifiManager.EXTRA_WIFI_INFO).ssid
                val log="Connected to SSID:"+ssid
                Log.d(TAG,"Connected to SSID:"+ssid)
                Toast.makeText(context, log, Toast.LENGTH_SHORT).show()
            }
        }
    }
    //  https://icircuit.net/android-connecting-wifi-programmatically/1814



    // Method to show app exit dialog
    private fun showAppExitDialog() {
        val builder = AlertDialog.Builder(this)

        builder.setTitle("Please confirm")
        builder.setMessage("No back history found, want to exit the app?")
        builder.setCancelable(true)

        builder.setPositiveButton("Yes", { _, _ ->
            // Do something when user want to exit the app
            // Let allow the system to handle the event, such as exit the app
        //    super@MainActivity.onBackPressed()
            finish()
        })

        builder.setNegativeButton("No", { _, _ ->
            // Do something when want to stay in the app
            toast("thank you.")
        })

        // Create the alert dialog using alert dialog builder
        val dialog = builder.create()

        // Finally, display the dialog when user press back button
        dialog.show()
    }



    // Handle back button press in web view
    override fun onBackPressed() {
        if (web_view.canGoBack()) {
            // If web view have back history, then go to the web view back history
            web_view.goBack()
            toast("Going to back history")
        } else {
            // Ask the user to exit the app or stay in here
            showAppExitDialog()
        }
    }
}


// Extension function to show toast message
fun Context.toast(message: String) {
    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
}