package com.example.androidservice.myhttp.NIO

import java.net.InetSocketAddress
import java.nio.ByteBuffer
import java.nio.channels.SelectionKey
import java.nio.channels.Selector
import java.nio.channels.ServerSocketChannel
import java.nio.channels.SocketChannel
import kotlin.concurrent.thread

/**
 * Created by 铖哥 on 2018/3/23.
 */
 class NIOHandler(val port: Int) {
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
        thread{
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