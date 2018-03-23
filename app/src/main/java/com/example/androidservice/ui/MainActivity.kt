package com.example.androidservice.ui

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.example.androidservice.R

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



    fun getHostIp(): String? {
        val regexCIp = "^192\\.168\\.(\\d{1}|[1-9]\\d|1\\d{2}|2[0-4]\\d|25\\d)\\.(\\d{1}|[1-9]\\d|1\\d{2}|2[0-4]\\d|25\\d)$"
        //匹配A类地址
        val regexAIp = "^10\\.(\\d{1}|[1-9]\\d|1\\d{2}|2[0-4]\\d|25\\d)\\.(\\d{1}|[1-9]\\d|1\\d{2}|2[0-4]\\d|25\\d)\\.(\\d{1}|[1-9]\\d|1\\d{2}|2[0-4]\\d|25\\d)$"
        //匹配B类地址
        val regexBIp = "^172\\.(1[6-9]|2\\d|3[0-1])\\.(\\d{1}|[1-9]\\d|1\\d{2}|2[0-4]\\d|25\\d)\\.(\\d{1}|[1-9]\\d|1\\d{2}|2[0-4]\\d|25\\d)$"

        val hostIp: String
        val ip = Pattern.compile("($regexAIp)|($regexBIp)|($regexCIp)")
        var networkInterfaces: Enumeration<NetworkInterface>? = null
        try {
            networkInterfaces = NetworkInterface.getNetworkInterfaces()
        } catch (e: SocketException) {
            e.printStackTrace()
        }

        var address: InetAddress
        while (networkInterfaces!!.hasMoreElements()) {
            val networkInterface = networkInterfaces!!.nextElement()
            val inetAddresses = networkInterface.getInetAddresses()
            while (inetAddresses.hasMoreElements()) {
                address = inetAddresses.nextElement()
                val hostAddress = address.getHostAddress()
                val matcher = ip.matcher(hostAddress)
                if (matcher.matches()) {
                    hostIp = hostAddress
                    return hostIp
                }

            }
        }
        return null
    }
}
