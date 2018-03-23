package com.example.androidservice.myhttp

import com.example.androidservice.myhttp.reslover.reslovebean.RequestMessage

/**
 * Created by 铖哥 on 2018/3/18.
 */
open class HttpRequest(private val m: RequestMessage) {

    fun getRequestHead(key: String) = m.getHead(key)

    fun getRequestArgument(key: String) = m.getArgument(key)

    fun getRequestBody() = m.getBody()
}