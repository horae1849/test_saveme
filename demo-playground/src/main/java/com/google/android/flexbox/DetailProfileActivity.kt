package com.google.android.flexbox

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.Response.Listener
import com.android.volley.toolbox.JsonObjectRequest

import com.google.android.apps.flexbox.R
import com.google.android.material.snackbar.Snackbar
import org.json.JSONObject
import kotlin.Exception as Exception1


//public class IntroActivity : AppCompatActivity(){
class DetailProfileActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.detail_profile) //xml , java 소스 연결


        //인텐트 전달 값으로 ID 확인
        val intent = getIntent()
        val viewIndex = intent.getIntExtra("viewindex",0)

        val context: Context = this.applicationContext


        //전역 변수 선언 부분
        var mApp = GlobalVar()
        var server_address = mApp.server_address

        var profile_name: EditText = findViewById(R.id.profile_name)
        var profile_date: EditText = findViewById(R.id.profile_date)
        var profile_phone: EditText = findViewById(R.id.profile_phone)
        var profile_unique: EditText = findViewById(R.id.profile_unique2)


        var name = " "
        var date = " "
        var phone = " "
        var unique =" "

//        context.toast("view index 값 : "+viewIndex)

        //DB에서 프로필 정보 가져오기
        val url = "http://"+server_address+"/saveme/user_st_json.php"
        val params = HashMap<String,String>()
        params["REG_ID"] = viewIndex.toString()
        val jsonObject = JSONObject(params)


        val request = JsonObjectRequest(Request.Method.GET,url,jsonObject,
                Listener { response ->
                    // Process the json
                    try {
                        val jsonarray=response.getJSONArray("USER_ST")

                        for (i in 0..(jsonarray.length() - 1)) {
                            val item = jsonarray.getJSONObject(i)

                            val regid = item.getString("REG_ID")
                            if(regid.toInt() == (viewIndex)){ // REG_ID 와 viewIdex를 비교하여 똑같은 값이면 TEL값 저장
                                name = item.getString("USER_NM")
                                date = item.getString("USER_BIRTH_DT")
                                phone = item.getString("USER_TEL_NO")
                                unique = item.getString("USER_ETC_NM")

                                if(name=="null"){
                                    context.toast("프로필 등록이 필요합니다.")

                                }else {

                                    profile_name.setText(name)
                                    profile_date.setText(date)
                                    profile_phone.setText(phone)
                                    profile_unique.setText(unique)


                                }
                            }
                        }

                    }catch (e:Exception){
                        // textView.text = "Exception: $e"
                        context.toast("Exception: $e")
                    }

                }, Response.ErrorListener{
            // Error in request
            //textView.text = "Volley error: $it"
            context.toast("Volley error: $it")
        })




        // Volley request policy, only one time request to avoid duplicate transaction
        request.retryPolicy = DefaultRetryPolicy(
                DefaultRetryPolicy.DEFAULT_TIMEOUT_MS,
                // 0 means no retry
                0, // DefaultRetryPolicy.DEFAULT_MAX_RETRIES = 2
                1f // DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        )

        // Add the volley post request to the request queue

        VolleySingleton.getInstance(context).addToRequestQueue(request)







        //뒤로가기 버튼 누를 시 종료
        val back_icon2: View = findViewById(R.id.back_icon2)
        back_icon2.setOnClickListener { view ->
            Snackbar.make(view, "Here's a Snackbar menu", Snackbar.LENGTH_LONG)
                    .setAction("Action", null)
                    .show()

            finish()

        }

        // 수정 버튼 누를 시 현재 입력된 값을 DB로 저장함
        val profile_modify_icon: View = findViewById(R.id.profile_modify_icon)
        profile_modify_icon.setOnClickListener { view ->

            name = profile_name.getText().toString()
            date = profile_date.getText().toString()
            phone = profile_phone.getText().toString()
            unique = profile_unique.getText().toString()




            Snackbar.make(view, "프로필 정보가 저장되었습니다.", Snackbar.LENGTH_LONG)
                    .setAction("Action", null)
                    .show()



        }


    }


    override fun onPause() {
        super.onPause()
        finish()
    }
}