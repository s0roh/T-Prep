package com.example.tprep.app.utils

import android.content.Context
import org.json.JSONObject

fun getApiBaseUrl(context: Context): String {
    val json = context.assets.open("config.json").bufferedReader().use { it.readText() }
    val jsonObject = JSONObject(json)
    return jsonObject.getString("api_base_url")
}
