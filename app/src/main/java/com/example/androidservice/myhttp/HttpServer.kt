package com.example.androidservice.myhttp

import android.util.Log
import com.example.androidservice.myhttp.annotaion.Http
import com.example.androidservice.myhttp.`interface`.HttpHandler
import com.example.androidservice.myhttp.reslover.HttpMessageReslover
import java.io.*
import java.net.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import kotlin.concurrent.thread


/**
 * Created by 铖哥 on 2018/3/17.
 */
class HttpServer constructor(val port: Int) {

    private val pool: ExecutorService by lazy { Executors.newFixedThreadPool(5) }
    private val routerMapper = mutableMapOf<String, Class<*>>()

    fun addHandler(handler: Class<*>) {
        val route = handler.getAnnotation(Http::class.java).route
        Log.e("HttpServer",route)
        routerMapper.put(route, handler)
    }

    fun start(): Boolean {

        val serverSocket: ServerSocket

        try {
            serverSocket = ServerSocket(port)
        } catch (e: Exception) {
            e.printStackTrace()
            return false
        }

        thread {
            while (true) {
                val socket = serverSocket.accept()
                val handler = MessageDispatcher(socket)
                pool.execute(handler)
                Log.e("HttpServer", "ACCEPT")
            }
        }

        return true
    }

    inner class MessageDispatcher(val socket: Socket) : Runnable {

        lateinit var input: DataInputStream
        lateinit var out: OutputStream

        override fun run() {
            input = DataInputStream(socket.getInputStream())
            out = socket.getOutputStream()

            try {

                val requestMessage = HttpMessageReslover.reslove(input)
                val method = requestMessage.requestMethod
                val h = routerMapper.get(requestMessage.requestPath)?.getConstructor()?.newInstance()
                val handler: HttpHandler? = if(h == null) null else h as HttpHandler
                val request = HttpRequest(requestMessage)
                val response = ResponseBuilder.config {
                    handlerExist = handler != null
                    this.requestMessage = requestMessage
                }.build()

                when (method) {
                    "POST" -> handler?.doPost(request, response)
                    "GET" -> handler?.doGet(request, response)
                    else -> run { out.write(HttpState.Method_Not_Allowed.toByteArray()) }
                }

                process(response)

            } catch (e: Exception) {

                e.printStackTrace()
            } finally {
                input.close()
                out.close()
                socket.close()
            }
        }
        private fun process(response: HttpResponse) {
            out.write(response.httpState.toByteArray())
            for ((k, v) in response.headers) {
                out.write("$k:$v \r\n".toByteArray())
            }
            closeHead()
            response.write?.invoke(out)
        }
        /**
         * 处理静态资源获取,处理成功返回true 否则返回false
         */
//        private fun handleStaticResource(filePath: String, range: String?): Boolean {
//            val file = File(filePath)
//
//            if (range == null && file.exists() && file.isFile) {
//                out.write(HttpState.OK_200.toByteArray())
//                out.write("Content-Length:${file.length()}\r\n".toByteArray())
//                closeHead()
//                file.inputStream().writeTo(out, true)
//                out.close()
//                return true
//            } else if (range != null) {
//                val (from, to) = range.split("=")[1].split("-").map { if (it.isEmpty()) file.length() - 1 else it.toLong() }
//
//                Log.e("Range", "$from-$to")
//                out.write(HttpState.RangeOK_206.toByteArray())
//                out.write("Content-Length: ${file.length()}\r\n".toByteArray())
//                out.write("Content-Range: bytes $from-$to/${file.length()}\r\n".toByteArray())
//                out.write("Accept-Ranges: bytes\r\n".toByteArray())
//                closeHead()
//
//                val raf = RandomAccessFile(filePath, "r")
//
//                raf.seek(from)
//                var length = to - from + 1
//                val buf = ByteArray(1024 * 1024 * 2)
//
//                while (length > 0) {
//                    if (length < buf.size) {
//                        val readBytes = raf.read(buf, 0, length.toInt())
//                        out.write(buf, 0, readBytes)
//                        length -= readBytes;
//                    } else {
//                        val readBytes = raf.read(buf)
//                        out.write(buf, 0, readBytes)
//                        length -= readBytes
//                    }
//                }
//
//                return true
//            }
//
//
//
//            return false
//        }
//
//        /**
//         * 处理空的handler 处理成功返回true
//         * 否则返回false
//         */
//        private fun handleHandler(handler: HttpHandler?): Boolean {
//            if (handler == null) {
//                out.write(HttpState.Not_Found_404.toByteArray())
//                val ins = HttpState.FOUR_O_FOUR_HTML.byteInputStream()
//                closeHead()
//                ins.writeTo(out)
//                out.close()
//                return true
//            }
//
//            return false
//        }
        private fun closeHead() {
            out.write("\r\n".toByteArray())
        }

    }

}


