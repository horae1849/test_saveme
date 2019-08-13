package com.google.android.flexbox

import android.Manifest
import android.annotation.TargetApi
import android.app.Activity
import android.content.ContentUris
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.view.View
import android.widget.*
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.Response.Listener
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import com.bumptech.glide.Glide

import com.google.android.apps.flexbox.R
import com.google.android.material.snackbar.Snackbar
import org.json.JSONObject
import java.text.SimpleDateFormat
import com.google.android.flexbox.GetImageActivity
import java.io.File
import kotlin.Exception as Exception1


//public class IntroActivity : AppCompatActivity(){
class DetailProfileActivity : Activity() {


    private val FINAL_TAKE_PHOTO = 1
    private val FINAL_CHOOSE_PHOTO = 2
    private var picture: ImageView? = null
    private var imageUri: Uri? = null



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

        picture = findViewById(R.id.profile_image)
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

                                    Glide.with(this).load("http://"+server_address+"/uploads/"+viewIndex+".jpg").into(picture)


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


        val btnGallery:View = findViewById(R.id.btnGallery)
        btnGallery.setOnClickListener{view ->

            val checkSelfPermission = ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
            if (checkSelfPermission != PackageManager.PERMISSION_GRANTED){
                ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE), 1)
            }
            else{
                openAlbum()
            }



        }

        val btnCamera:View = findViewById(R.id.btnCamera)
        btnCamera.setOnClickListener{view ->

            val outputImage = File(externalCacheDir, "output_image.jpg")
            if(outputImage.exists()) {
                outputImage.delete()
            }
            outputImage.createNewFile()
            imageUri = if(Build.VERSION.SDK_INT >= 24){
                FileProvider.getUriForFile(this, "com.google.android.flexbox", outputImage)
            } else {
                Uri.fromFile(outputImage)
            }

            val intent = Intent("android.media.action.IMAGE_CAPTURE")
            intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri)
            startActivityForResult(intent, FINAL_TAKE_PHOTO)

        }

        // 수정 버튼 누를 시 현재 입력된 값을 DB로 저장함
        val profile_modify_icon: View = findViewById(R.id.profile_modify_icon)
        profile_modify_icon.setOnClickListener { view ->


            name = profile_name.getText().toString()
            date = profile_date.getText().toString()
            phone = profile_phone.getText().toString()
            unique = profile_unique.getText().toString()

            //      http://192.168.219.200/saveme/person_info_input.php?regid=1&name=조재빈&date=19920903&phone=010-7245-1118&unique=수면제 약 복용
            val input_url = "http://"+server_address+"/saveme/person_info_input.php?"
            val input_url_sum = input_url + "regid="+viewIndex+"&name=\""+name+"\"&date=\""+date+"\"&phone=\""+phone+"\"&unique=\""+unique+"\""

            val request = StringRequest(Request.Method.POST,input_url_sum,
                    Listener { response ->
                        // Process the json
                        try {
                            context.toast("Response: $response")
                        }catch (e:Exception){
                            context.toast("Exception: $e")
                        }

                    }, Response.ErrorListener{
                // Error in request
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





            Snackbar.make(view, "프로필 정보가 저장되었습니다.", Snackbar.LENGTH_LONG)
                    .setAction("Action", null)
                    .show()



        }


    }

    private fun openAlbum(){
        val intent = Intent("android.intent.action.GET_CONTENT")
        intent.type = "image/*"
        startActivityForResult(intent, FINAL_CHOOSE_PHOTO)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when(requestCode){
            1 ->
                if (grantResults.isNotEmpty() && grantResults.get(0) ==PackageManager.PERMISSION_GRANTED){
                    openAlbum()
                }
                else {
                    Toast.makeText(this, "You deied the permission", Toast.LENGTH_SHORT).show()
                }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when(requestCode){
            FINAL_TAKE_PHOTO ->
                if (resultCode == Activity.RESULT_OK) {
                    val bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(imageUri))
                    picture!!.setImageBitmap(bitmap)
                }
            FINAL_CHOOSE_PHOTO ->
                if (resultCode == Activity.RESULT_OK) {
                    if (Build.VERSION.SDK_INT >= 19) {
//                        4.4以上
                        handleImageOnKitkat(data)
                    }
                    else{
//                        4.4以下
                        handleImageBeforeKitkat(data)
                    }
                }
        }
    }


    @TargetApi(19)
    private fun handleImageOnKitkat(data: Intent?) {
        var imagePath: String? = null
        val uri = data!!.data
        if (DocumentsContract.isDocumentUri(this, uri)){
//            document类型的Uri，用document id处理
            val docId = DocumentsContract.getDocumentId(uri)
            if ("com.android.providers.media.documents" == uri.authority){
                val id = docId.split(":")[1]
                val selsetion = MediaStore.Images.Media._ID + "=" + id
                imagePath = imagePath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, selsetion)
            }
            else if ("com.android.providers.downloads.documents" == uri.authority){
                val contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), java.lang.Long.valueOf(docId))
                imagePath = imagePath(contentUri, null)
            }
        }
        else if ("content".equals(uri.scheme, ignoreCase = true)){
//            content类型Uri 普通方式处理
            imagePath = imagePath(uri, null)
        }
        else if ("file".equals(uri.scheme, ignoreCase = true)){
            imagePath = uri.path
        }
        displayImage(imagePath)
    }

    //    没4.4的设备，略过
    private fun handleImageBeforeKitkat(data: Intent?) {}

    private fun imagePath(uri: Uri?, selection: String?): String {
        var path: String? = null
//        通过Uri和selection获取路径
        val cursor = contentResolver.query(uri, null, selection, null, null )
        if (cursor != null){
            if (cursor.moveToFirst()) {
                path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA))
            }
            cursor.close()
        }
        return path!!
    }

    private fun displayImage(imagePath: String?){
        if (imagePath != null) {
            val bitmap = BitmapFactory.decodeFile(imagePath)
            picture?.setImageBitmap(bitmap)
        }
        else {
            Toast.makeText(this, "Failed to get image", Toast.LENGTH_SHORT).show()
        }
    }



    override fun onPause() {
        super.onPause()
        finish()
    }
}