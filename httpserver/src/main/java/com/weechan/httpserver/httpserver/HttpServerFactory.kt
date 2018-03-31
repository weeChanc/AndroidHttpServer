package com.weechan.httpserver.httpserver

import android.content.Context
import android.util.Log
import com.weechan.httpserver.httpserver.`interface`.HttpHandler
import com.weechan.httpserver.httpserver.uitls.getClassesInPackage

/**
 * Created by 铖哥 on 2018/3/19.
 */
class HttpServerFactory {

    companion object {

        private val serverMapper: LinkedHashMap<Int, HttpServer> by lazy { LinkedHashMap<Int, HttpServer>() }
        private var handlerPackage = "handler"
        private lateinit var handlerClassList: List<Class<*>>
        private fun tryAs(clazz: Class<*>):Class<HttpHandler>?{
            var var0:Class<HttpHandler>? = null
            try {
                var0 = clazz as Class<HttpHandler>
            } catch (e:Exception){

            }
            return var0
        }
        fun with(context: Context): Companion {
            if (handlerPackage == "") throw RuntimeException("need to invoke setup handlerPackage")
            handlerClassList = getClassesInPackage(handlerPackage, context)
            return this
        }

        fun handlerPackage(var0: String): Companion {
            handlerPackage = var0
            return this
        }

        fun getHttpServer(port: Int): HttpServer {
            var server = serverMapper[port]
            if (server == null) {
                server = HttpServer(port)
                serverMapper[port] = server
            }
            for (clazz in handlerClassList) {
                    server.addHandler(tryAs(clazz))
            }
            return server
        }
    }


}