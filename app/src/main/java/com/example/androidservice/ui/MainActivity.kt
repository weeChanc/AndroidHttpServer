package com.example.androidservice.ui

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.example.androidservice.R
import com.example.androidservice.uitls.getHostIp

import kotlinx.android.synthetic.main.activity_main.*
import java.net.InetAddress
import java.net.NetworkInterface
import java.net.SocketException
import java.util.*
import java.util.regex.Pattern


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        startService(Intent(this,Server::class.java))
        textView.text = getHostIp()+":8080"


    }


}
