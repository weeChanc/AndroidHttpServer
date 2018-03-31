package com.weechan.httpserver.httpserver

import android.content.Context
import com.weechan.httpserver.httpserver.interfaces.HttpHandler
import com.weechan.httpserver.httpserver.uitls.getClassesInPackage
import java.nio.charset.Charset

/**
 * Created by 铖哥 on 2018/3/19.
 */
class HttpServerBuilder {

    companion object {

        private var handlerPackage = "handler"
        private lateinit var handlerClassList: List<Class<*>>
        private var port = 8080
        private var encodeCharset: String = "UTF-8"
        private var decodeCharset: String = "UTF-8"


        fun with(context: Context): Companion {
            if (handlerPackage == "") throw RuntimeException("need to invoke setup handlerPackage")
            handlerClassList = getClassesInPackage(handlerPackage, context)
            return this
        }

        fun handlerPackage(var0: String): Companion {
            handlerPackage = var0
            return this
        }

        fun port(port: Int): Companion {
            this.port = port
            return this
        }

        fun encode(charSet : Charset){
            this.encodeCharset = charSet.name()
        }

        fun decode(charSet : Charset){
            this.decodeCharset = charSet.name()
        }

        fun getHttpServer(): HttpServer {
            val server = HttpServer(port)
            for (clazz in handlerClassList) {
                server.addHandler(tryAs(clazz))
            }
            return server
        }

        private fun tryAs(clazz: Class<*>): Class<HttpHandler>? {
            var var0: Class<HttpHandler>? = null
            try {
                var0 = clazz as Class<HttpHandler>
            } catch (e: Exception) {

            }
            return var0
        }


    }


}