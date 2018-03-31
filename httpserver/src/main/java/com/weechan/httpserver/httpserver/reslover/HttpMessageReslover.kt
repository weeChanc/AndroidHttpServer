package com.example.androidservice.httpserver.reslover

import com.weechan.httpserver.httpserver.reslover.reslovebean.RequestBody
import com.example.androidservice.httpserver.reslover.reslovebean.RequestHeaders
import com.example.androidservice.httpserver.reslover.reslovebean.RequestLine
import com.example.androidservice.httpserver.reslover.reslovebean.RequestMessage
import java.io.DataInputStream
import java.net.URLDecoder

/**
 * Created by 铖哥 on 2018/3/20.
 */
class HttpMessageReslover {

    companion object {

        fun reslove(ins: DataInputStream): RequestMessage? {

            val firstLine = ins.readLine() ?: return null

            val headers = StringBuffer()

            var tempStr = ins.readLine()

            while (tempStr != null && !tempStr.isEmpty()) {
                headers.append(tempStr + "\r\n")
                tempStr = ins.readLine()
            }


            val requestLine = RequestLine(URLDecoder.decode(firstLine, "utf-8"))
            val requestHeaders = RequestHeaders(headers.toString())
            val requestBody = RequestBody(ins, requestHeaders.get("content-type"), requestHeaders.get("content-length")?.toLongOrNull(),requestHeaders.get("boundary"))

            return RequestMessage(requestLine, requestHeaders, requestBody)


        }
    }

}