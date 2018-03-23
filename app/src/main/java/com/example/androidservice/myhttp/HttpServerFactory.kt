package com.example.androidservice.myhttp

import android.app.Application

/**
 * Created by 铖哥 on 2018/3/19.
 */
class HttpServerFactory {

    companion object {

        private val serverMapper: LinkedHashMap<Int, HttpServer> by lazy { LinkedHashMap<Int, HttpServer>() }

        fun getHttpServer(app: Application, port: Int): HttpServer {
            var server = serverMapper[port]
            if (server == null) {
                server = HttpServer(port)
                serverMapper[port] = server
            }
            return server
        }
    }


}