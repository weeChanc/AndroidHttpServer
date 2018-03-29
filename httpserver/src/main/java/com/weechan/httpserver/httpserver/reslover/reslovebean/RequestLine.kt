package com.example.androidservice.httpserver.reslover.reslovebean

import com.example.androidservice.httpserver.reslover.URLResolver

/**
 * Created by 铖哥 on 2018/3/22.
 */
data class RequestLine(val requestLine: String){

    val method: String
    val path: String
    val protocol: String
    val arguments: HashMap<String, String>

    init {

        val firstIndex = requestLine.indexOf(" ")
        val lastIndex = requestLine.lastIndexOf(" ")
        val requestUrl = requestLine.substring(firstIndex + 1, lastIndex)

        arguments = URLResolver.getRequestArgument(requestUrl)
        path = URLResolver.getRequestRouter(requestUrl)
        method = requestLine.substring(0, firstIndex)
        protocol = requestLine.substring(lastIndex + 1, requestLine.length)

    }
}