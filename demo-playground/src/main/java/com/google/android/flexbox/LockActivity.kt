package com.google.android.flexbox

// https://android--code.blogspot.com/2019/02/android-kotlin-volley-post-request-with.html

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import kotlinx.android.synthetic.main.lock_all.*
import org.json.JSONObject

import com.google.android.apps.flexbox.R

class LockActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.lock_part)

/*
        btn.setOnClickListener{
            textView.text = ""
            val queue = Volley.newRequestQueue(this)
            val url = "http://125.132.148.9/saveme/lock_st_input.php?lock_st=1&regid=5"

            // Request a string response from the provided URL.
            val stringRequest = StringRequest(Request.Method.GET, url,
                    Response.Listener<String> { response ->
                        // Display the first 500 characters of the response string.
                        textView.text = "Response is: ${response.substring(0, 500)}"
                    },
                    Response.ErrorListener { textView.text = "That didn't work!" })

            // Add the request to the RequestQueue.
            queue.add(stringRequest)

        }*/

        // Run volley json 응답 처리
        btn.setOnClickListener {
 //           val url = "https://postman-echo.com/post"
     //       val url = "http://125.132.148.9/saveme/lock_st_input.php"
            val url = "http://125.132.148.9/saveme/lock_st_input.php?lock_st=1&regid=5"
            textView.text = ""

            // Post parameters
            // Form fields and values
            val params = HashMap<String,String>()
  //          params["foo1"] = "bar1"
            //        params["foo2"] = "bar2"
            params["lock_st"] = "1"
            params["regid"] = "4"
            val jsonObject = JSONObject(params)

            // Volley post request with parameters

            val request = StringRequest(Request.Method.POST,url,
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

            /*
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

              */


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