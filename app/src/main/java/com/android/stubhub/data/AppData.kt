package com.android.stubhub.data

import android.content.Context
import com.google.gson.Gson
import java.io.BufferedReader
import java.io.InputStreamReader

object AppData {

    inline fun <reified T> create(context: Context, name: String): T {
        val assetManager = context.assets
        val inputStream = assetManager.open(name)
        val reader = BufferedReader(InputStreamReader(inputStream))
        return Gson().fromJson(reader, T::class.java)

    }

}