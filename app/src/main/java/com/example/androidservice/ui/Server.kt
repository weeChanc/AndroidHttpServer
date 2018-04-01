//package com.example.androidservice.ui
//
//import android.app.Notification
//import android.app.Service
//import android.content.Intent
//import android.os.Binder
//import android.os.IBinder
//import com.weechan.httpserver.httpserver.HttpServer
//
//import com.weechan.httpserver.httpserver.HttpServerBuilder
//
///**
// * Created by 铖哥 on 2018/3/22.
// */
//class Server : Service() {
//    override fun onBind(intent: Intent?): IBinder? {
//        return MyBinder()
//    }
//
//    lateinit var service : HttpServer
//
//    override fun onCreate() {
//        super.onCreate()
//
//        service = HttpServerBuilder
//                .with(this)
//                .port(8080)
//                .getHttpServer()
//
//        service.start()
//
//        startForeground(1, Notification())
//    }
//
//    inner class MyBinder : Binder() {
//        fun stop(){
//            service.start()
//        }
//
//        fun start(){
//            service.stop()
//        }
//    }
//
//}