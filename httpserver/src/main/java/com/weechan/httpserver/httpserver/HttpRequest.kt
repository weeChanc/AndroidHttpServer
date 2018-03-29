package com.weechan.httpserver.httpserver

import com.example.androidservice.httpserver.reslover.reslovebean.RequestMessage
import com.weechan.httpserver.httpserver.reslover.body.FormDataPart
import com.weechan.httpserver.httpserver.reslover.reslovebean.RequestBody
import java.io.InputStream

/**
 * Created by 铖哥 on 2018/3/18.
 */
open class HttpRequest(private val m: RequestMessage) {


    fun getRequestHead(key: String) = m.getHead(key)

    fun getRequestArgument(key: String) = m.getArgument(key)

    fun getRequestBody() = RequestBodyWrapper(m.getBody())

    class RequestBodyWrapper(private val requestBody : RequestBody) {

        val contentLength = requestBody.contentLength

        fun getPart(key : String): FormDataPart? = requestBody.getPart(key)

        fun string() = requestBody.getRawData()

        fun getxwwformValue(key : String) = requestBody.getxwwformValue(key)

        fun getRawInputStream() : InputStream = requestBody.ins


    }
}