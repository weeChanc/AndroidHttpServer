package com.example.androidservice.myhttp.`interface`

import com.example.androidservice.myhttp.HttpRequest
import com.example.androidservice.myhttp.HttpResponse

/**
 * Created by 铖哥 on 2018/3/17.
 */
interface HttpHandler{
    fun doGet(request : HttpRequest, response: HttpResponse)
    fun doPost(request : HttpRequest, response: HttpResponse)
}