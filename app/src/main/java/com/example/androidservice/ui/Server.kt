package com.example.androidservice.ui

import android.app.IntentService
import android.content.Intent
import com.example.androidservice.myhttp.HttpServerFactory
import java.net.InetAddress
import java.net.NetworkInterface
import java.net.SocketException
import java.util.*
import java.util.regex.Pattern

/**
 * Created by 铖哥 on 2018/3/22.
 */
class Server : IntentService("httpServer") {

    override fun onHandleIntent(intent: Intent?) {

    }

    override fun onCreate() {
        super.onCreate()

        val service = HttpServerFactory.getHttpServer(8080)
        service.addHandler(MyHttpHandler())
        service.addHandler(MainHandler(getHostIp()+":8080"))
        service.start()
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