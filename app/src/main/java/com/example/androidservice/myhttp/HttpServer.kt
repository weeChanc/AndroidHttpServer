package com.example.androidservice.myhttp

import android.app.Application
import android.util.Log
import com.example.androidservice.R
import com.example.androidservice.myhttp.annotaion.Http
import com.example.androidservice.myhttp.`interface`.HttpHandler
import com.example.androidservice.myhttp.reslover.HttpMessageReslover
import com.example.androidservice.uitls.ClassUtil
import com.example.androidservice.uitls.writeTo
import java.io.*
import java.net.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import kotlin.concurrent.thread
import java.nio.ByteBuffer
import java.nio.channels.*


/**
 * Created by 铖哥 on 2018/3/17.
 */
class HttpServer constructor(val port: Int) {

    private val pool: ExecutorService by lazy { Executors.newFixedThreadPool(5) }
    private val routerMapper = mutableMapOf<String, HttpHandler>()
    private val buf = ByteBuffer.allocate(1024 * 1024)

//    fun scanHandler(vararg packageName: String = arrayOf("HttpHandler")) {
//
//        val clazz = packageName.map { ClassUtil.getClassSet("${app.packageName}.$it") }
//        println(app.packageName + clazz[0].size)
//        clazz.forEach {
//            it.forEach {
//                val an = it.getAnnotation(Http::class.java)
//                if (an != null) {
//                    val hanlder = it.constructors[0].newInstance() as HttpHandler
//                    routerMapper.put(an.route, hanlder)
//                }
//                Log.e("HttpServer", it.simpleName)
//            }
//        }
//
//    }

    init {
//        scanHandler()
    }

    fun addHandler(handler: HttpHandler) {
        val route = handler.javaClass.getAnnotation(Http::class.java).route
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
                val handler = SocketHandler(socket)
                pool.execute(handler)
                Log.e("HttpServer", "ACCEPT")
            }
        }

        return true
    }

    inner class SocketHandler(val socket: Socket) : Runnable {

        lateinit var inputReader: DataInputStream
        lateinit var outputWriter: OutputStream

        override fun run() {
            inputReader = DataInputStream(socket.getInputStream())
            outputWriter = socket.getOutputStream()

            try {

                val requestMessage = HttpMessageReslover.reslove(inputReader)


//                Log.e("requestMessage", requestMessage)


                val request = HttpRequest(requestMessage)

                val method = requestMessage.requestMethod

                if (!isMethodValid(method)) {
                    outputWriter.write(HttpState.Method_Not_Allowed.toByteArray())
                    return
                }

                //静态资源不经过get set 直接处理返回
                if (isStaticResource(requestMessage.requestPath)) return

                val handler = routerMapper.get(requestMessage.requestPath)

                //找不到对应的处理器,fourzerofour
                if (handler == null) {
                    outputWriter.write(HttpState.Not_Found_404.toByteArray())
                    val ins = HttpState.FOUR_O_FOUR_HTML.byteInputStream()
                    closeHead(outputWriter)
                    ins.writeTo(outputWriter)
                    outputWriter.close()
                    return
                }

                val response = HttpResponse()

                when(method){
                    "POST" -> handler.doPost(request, response)
                    "GET"-> handler.doGet(request, response)
                }

                outputWriter.write(HttpState.OK_200.toByteArray())
                for ((k, v) in response.headers) {
                    outputWriter.write("$k:$v \r\n".toByteArray())
                }
                closeHead(outputWriter)
                response.write?.invoke(outputWriter)

            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                inputReader.close()
                outputWriter.close()
            }

        }


        private fun closeHead(output: OutputStream) {
            output.write("\r\n".toByteArray())
        }

        private fun isStaticResource(filePath: String): Boolean {
            val file = File(filePath)
            if (file.exists() && file.isFile) {
                outputWriter.write(HttpState.OK_200.toByteArray())

                closeHead(outputWriter)
                file.inputStream().writeTo(outputWriter, true)
                outputWriter.close()
                return true
            }
            return false
        }

        private fun isMethodValid(method: String) = !(method != "POST" && method != "GET")


    }

    inner class NIOHandler(val port: Int) {
        val selector by lazy { Selector.open() }
        var handler: NioActionHandler? = null
        val ssc by lazy {
            ServerSocketChannel.open().apply {
                socket().bind(InetSocketAddress(port))
                configureBlocking(false)
                register(selector, SelectionKey.OP_ACCEPT, ByteBuffer.allocate(1024))
            }
        }

        fun start() {
            ssc
            pool.execute {
                while (true) {
                    if (selector.select(1000) == 0) {
                        println("==")
                        continue
                    }
                    val iter = selector.selectedKeys().iterator()
                    while (iter.hasNext()) {
                        val key = iter.next()
                        if (key.isAcceptable) {
                            handler?.onAccepted(key)
                        }
                        if (key.isReadable) {
                            handler?.onReadable(key)
                        }
                        if (key.isWritable && key.isValid) {
                            handler?.onWritable(key)
                        }
                        if (key.isConnectable) {
                            println("isConnectable = true")
                        }
                        iter.remove()
                    }
                }
            }
        }

    }

    interface NioActionHandler {
        fun onReadable(key: SelectionKey)
        fun onWritable(key: SelectionKey)
        fun onAccepted(key: SelectionKey)
        fun onConnected(key: SelectionKey)
    }

    class NormalHandler : NioActionHandler {
        override fun onReadable(key: SelectionKey) {
            val sc = key.channel() as SocketChannel
            val buf = key.attachment() as ByteBuffer
            var bytesRead = sc.read(buf)
            var size = 0;
            var buffered = ByteArray(1024)

            while (bytesRead > 0) {
                buf.flip()
                while (buf.hasRemaining()) {
                    buffered[size++] = buf.get()
                }
                println(size.toString() + String(buffered, 0, size))
                buf.clear()
                bytesRead = sc.read(buf)
            }
            if (bytesRead == -1) {
                sc.close()
            }
        }

        override fun onWritable(key: SelectionKey) {
            print("write")
        }

        override fun onAccepted(key: SelectionKey) {
            val ssChannel = key.channel() as ServerSocketChannel
            val sc = ssChannel.accept()
            sc.configureBlocking(false)
            sc.register(key.selector(), SelectionKey.OP_READ, ByteBuffer.allocateDirect(1024))
        }

        override fun onConnected(key: SelectionKey) {
            print("con")
        }

    }

}