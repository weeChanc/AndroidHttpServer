package com.example.androidservice.ui

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.util.Log
import com.example.androidservice.R
import com.example.androidservice.uitls.getHostIp
import com.weechan.httpserver.httpserver.HttpServerBuilder
import kotlinx.android.synthetic.main.activity_main.*



class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)



        textView.text = getHostIp()+":8080  tempFile${Environment.getExternalStorageDirectory().path}"
        MediaRepository.init()
        val service = HttpServerBuilder
                .with(this)
                .port(8080)
                .getHttpServer()

        service.start()

        open.setOnClickListener { Log.e("MainActivity", service.start().toString()) }
        stop.setOnClickListener{ Log.e("MainActivity", service.stop().toString())}

    }




}
