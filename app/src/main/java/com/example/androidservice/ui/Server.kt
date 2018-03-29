package com.example.androidservice.ui

import android.app.Notification
import android.app.Service
import android.content.Intent
import android.os.IBinder

import com.weechan.httpserver.httpserver.HttpServerFactory
import java.nio.file.Files

/**
 * Created by 铖哥 on 2018/3/22.
 */
class Server : Service() {
    override fun onBind(intent: Intent?): IBinder? {
        return null
    }


    override fun onCreate() {
        super.onCreate()

        val service = HttpServerFactory
                .handlerPackage("handler")
                .with(this)
                .getHttpServer(8080)

        service.start()

        startForeground(1, Notification())
    }

}