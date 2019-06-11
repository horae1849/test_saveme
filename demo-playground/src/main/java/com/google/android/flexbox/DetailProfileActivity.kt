package com.google.android.flexbox

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.widget.Button

import com.google.android.apps.flexbox.R
import com.google.android.material.snackbar.Snackbar


//public class IntroActivity : AppCompatActivity(){
class DetailProfileActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.detail_profile) //xml , java 소스 연결


        val back_icon2: View = findViewById(R.id.back_icon2)
        back_icon2.setOnClickListener { view ->
            Snackbar.make(view, "Here's a Snackbar menu", Snackbar.LENGTH_LONG)
                    .setAction("Action", null)
                    .show()

            finish()

        }


        val profile_modify_icon: View = findViewById(R.id.profile_modify_icon)
        profile_modify_icon.setOnClickListener { view ->
            Snackbar.make(view, "Here's a Snackbar menu", Snackbar.LENGTH_LONG)
                    .setAction("Action", null)
                    .show()

        }


    }


    override fun onPause() {
        super.onPause()
        finish()
    }
}