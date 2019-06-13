package com.google.android.flexbox

// https://android--code.blogspot.com/2019/02/android-kotlin-volley-post-request-with.html

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.lock_all.*
import org.json.JSONObject

import com.google.android.apps.flexbox.R

class LockActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.lock_all)

        // Run volley
        btn.setOnClickListener {
            val url = "https://www.google.com"
            textView.text = ""

            // Post parameters
            // Form fields and values
            val params = HashMap<String,String>()
            params["foo1"] = "bar1"
            params["foo2"] = "bar2"
            val jsonObject = JSONObject(params)

            // Volley post request with parameters
            val request = JsonObjectRequest(Request.Method.POST,url,jsonObject,
                    Response.Listener { response ->
                        // Process the json
                        try {
                            textView.text = "Response: $response"
                        }catch (e:Exception){
                            textView.text = "Exception: $e"
                        }

                    }, Response.ErrorListener{
                // Error in request
                textView.text = "Volley error: $it"
            })


            // Volley request policy, only one time request to avoid duplicate transaction
            request.retryPolicy = DefaultRetryPolicy(
                    DefaultRetryPolicy.DEFAULT_TIMEOUT_MS,
                    // 0 means no retry
                    0, // DefaultRetryPolicy.DEFAULT_MAX_RETRIES = 2
                    1f // DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
            )

            // Add the volley post request to the request queue
            VolleySingleton.getInstance(this).addToRequestQueue(request)
        }
    }
}