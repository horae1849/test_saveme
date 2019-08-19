package com.google.android.flexbox

import android.app.Application

class GlobalVar : Application() {
//    var server_address = "125.132.148.9"
    var server_address = "192.168.0.200"

    fun getGlobalString(): String {
        return server_address
    }

    fun setGlobalString(globalString: String) {
        this.server_address = globalString
    }
}