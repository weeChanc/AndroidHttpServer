package com.weechan.httpserver.httpserver.`interface`

import com.weechan.httpserver.httpserver.HttpRequest
import com.weechan.httpserver.httpserver.HttpResponse

/**
 * Created by 铖哥 on 2018/3/17.
 */
interface HttpHandler{
    fun doGet(request : HttpRequest, response: HttpResponse)
    fun doPost(request : HttpRequest, response: HttpResponse)
}