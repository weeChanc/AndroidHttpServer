package com.weechan.httpserver.httpserver

import android.util.Log
import com.weechan.httpserver.httpserver.annotaions.Http
import com.weechan.httpserver.httpserver.interfaces.HttpHandler
import com.example.androidservice.httpserver.reslover.HttpMessageReslover
import com.example.androidservice.httpserver.reslover.reslovebean.RequestMessage
import com.weechan.httpserver.httpserver.uitls.writeTo
import java.io.*
import java.net.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import kotlin.concurrent.thread


/**
 * Created by 铖哥 on 2018/3/17.
 */
class HttpServer constructor(val port: Int) {

    private val pool: ExecutorService by lazy { Executors.newCachedThreadPool() }
    private val routerMapper = mutableMapOf<String, Class<*>>()
    private var serverSocket: ServerSocket? = null


    fun  < T : HttpHandler >  addHandler(handler: Class<T>?) {
        if (handler == null) return
        val route = handler.getAnnotation(Http::class.java).route
        Log.e("HttpServer", route)
        routerMapper.put(route, handler)
    }

    fun stop() {
        serverSocket?.close()
    }

    fun destory(){
        serverSocket?.close()
        pool.shutdown()
        routerMapper.clear()
    }

    fun start(): Boolean {

        try {
            serverSocket = ServerSocket(port)
        } catch (e: Exception) {
            return false
        }
        execute()
        return true
    }

    private fun execute(){
        pool.execute {
            try{
                while (true) {
                    val socket = serverSocket?.accept() ?: continue
                    val handler = MessageDispatcher(socket)
                    pool.execute(handler)
                }
            }catch (e : Exception){
                Log.e("HttpServer", "shutdown")
            }
        }
    }

    fun startAuto(start : Int) : Int{
        var from = start
        while(from <= 65535){
            try {
                serverSocket = ServerSocket(from)
            } catch (e: Exception) {
                from++
                continue
            }
            execute()
            return from
        }

        return -1
    }

    inner class MessageDispatcher(val socket: Socket) : Runnable {

        private val input: DataInputStream =DataInputStream(socket.getInputStream())
        private val out: OutputStream = socket.getOutputStream()
        private var requestMessage : RequestMessage? = null

        override fun run() {

            try {
                requestMessage = HttpMessageReslover.reslove(input)
                requestMessage?:return

                with(requestMessage!!){
                    val h = routerMapper.get(requestPath)?.getConstructor()?.newInstance()
                    val handler: HttpHandler? = if (h == null) null else h as HttpHandler
                    val request = HttpRequest(this)
                    val response = ResponseBuilder.config {
                        handlerExist = handler != null
                        this.mRequestMessage = this@with
                    }.build()

                    when (requestMethod) {
                        "POST" -> handler?.doPost(request, response)
                        "GET" -> handler?.doGet(request, response)
                        else -> run { out.write(HttpState.Method_Not_Allowed.toByteArray()) }
                    }
                    process(response)
                }

            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                input.close()
                out.close()
                socket.close()
                deleteAllTempFile()
            }
        }

        private fun deleteAllTempFile(){

            requestMessage?:return

            val responseBodyStreams = requestMessage!!.getBody().responseBodyStreams
            if(responseBodyStreams != null){
                for( (k,v) in responseBodyStreams.dataPartMapper){
                    v.inputSink.close()
                }

                System.gc()
                val filePath =  requestMessage!!.getBody().responseBodyStreams?.path
                if (filePath != null) {
                    File(filePath).delete()
                }
            }

        }

        private fun process(response: HttpResponse) {
            out.write(response.httpState.toByteArray())
            for ((k, v) in response.headers) {
                out.write("$k:$v \r\n".toByteArray())
            }
            closeHead()
            if(response.httpState == HttpState.Not_Found_404){
                HttpState.FOUR_O_FOUR_HTML.byteInputStream().writeTo(out)
                return
            }
            response.write?.invoke(out)

        }

        private fun closeHead() {
            out.write("\r\n".toByteArray())
        }

    }

}


