package com.example.androidservice.httpserver.reslover.reslovebean

import com.weechan.httpserver.httpserver.reslover.reslovebean.RequestBody

/**
 * Created by 铖哥 on 2018/3/22.
 */
data class RequestMessage(private val requestLine: RequestLine,
                          private val requestHeaders: RequestHeaders,
                          private val requestBody: RequestBody) {

    lateinit var requestMethod: String
    lateinit var requestPath: String
    lateinit var requestProtocol: String
    lateinit var requestArguments: HashMap<String, String>

    init {
        with(requestLine) {
            requestMethod = method
            requestPath = path
            requestProtocol = protocol
            requestArguments = arguments
        }
    }

    fun getHead(key: String) = requestHeaders.get(key)

    fun getArgument(key: String) = requestArguments.get(key)

    fun getBody() = requestBody

}