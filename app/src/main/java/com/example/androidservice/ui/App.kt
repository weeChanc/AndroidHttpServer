package com.example.androidservice.ui

import android.app.Application
import kotlin.properties.Delegates

/**
 * Created by weechan on 18-3-24.
 */
class App : Application() {

    companion object {
         var ctx : Application by Delegates.notNull()
    }


    override fun onCreate() {
        super.onCreate()
        ctx = this
    }
}