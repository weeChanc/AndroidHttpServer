package com.weechan.httpserver.httpserver

import android.util.Log
import com.example.androidservice.httpserver.reslover.reslovebean.RequestMessage
import com.weechan.httpserver.httpserver.uitls.writeTo
import java.io.File
import java.io.OutputStream
import java.io.RandomAccessFile
import kotlin.properties.Delegates

/**
 * Created by weechan on 18-3-24.
 */

class ResponseBuilder() {

    private var range: Pair<Long, Long>? = null
    private var fileSize: Long? = null
    private var file : File by Delegates.notNull()
    private val response: HttpResponse = HttpResponse()

    var handlerExist  : Boolean = false
    var mRequestMessage: RequestMessage by Delegates.notNull()

    companion object {

        fun config( config : ResponseBuilder.()->Unit) : ResponseBuilder {
            val r = ResponseBuilder()
            r.config()
            r.setUp()
            r.resloveResponse()
            return r
        }

    }

    fun build(): HttpResponse = response

    private fun setUp(){
        file = File(mRequestMessage.requestPath)
        if (file.exists() && file.isFile) fileSize = file.length()
        range = resloveRange(mRequestMessage.getHead("range"), fileSize)
    }

    private fun resloveResponse() {
        setHttpState()
        setResponseHeaders()
        setBody()
    }

    private fun setHttpState() {

        if ((!file.exists() || !file.isFile) && !handlerExist) {
            response.httpState = HttpState.Not_Found_404
            return
        }

        if (range != null) {
            response.httpState = HttpState.RangeOK_206
            return
        }

        response.httpState = HttpState.OK_200
    }

    private fun setResponseHeaders() {
        if (range != null) {
            response.addHeaders {
                "Content-Length" - " $fileSize\r\n"
                "Content-Range" - " bytes ${range?.first}-${range?.second}/${fileSize}\r\n"
                "Accept-Ranges" - " bytes\r\n"
            }
        }
    }

    private fun setBody() {

        if (fileSize != null) {

            if (range != null) {
                response.write {
                    readAndSendPartFile(this)

                }
            } else {
                response.write {
                    file.inputStream().writeTo(this, true)
                }
            }
        }
    }

    private fun readAndSendPartFile(output: OutputStream) {
        val (from, to) = range!!
        var length = to - from + 1
        val raf = RandomAccessFile(mRequestMessage.requestPath, "r")
        raf.seek(from)

        val buf = ByteArray(1024 * 1024 * 2)

        while (length > 0) {
            var readBytes : Int
            if (length < buf.size ) {
                readBytes = raf.read(buf, 0, length.toInt())
            } else {
                readBytes = raf.read(buf)
            }

            output.write(buf, 0, readBytes)
            length -= readBytes
            Log.e("ResponseBuilder",length.toString())
        }


    }

    private fun resloveRange(range: String?, fileSize: Long?): Pair<Long, Long>? {
        if (range == null || fileSize == null) return null
        var (from, to) = range.split("=")[1].split("-").map { if (it.isEmpty()) -1 else it.toLong() }
        if (from == -1L) from = 0
        if (to == -1L) to = fileSize-1
        return Pair(from, to)
    }

}