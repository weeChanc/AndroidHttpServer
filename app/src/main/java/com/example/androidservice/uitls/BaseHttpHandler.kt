package com.example.androidservice.uitls

import com.example.androidservice.myhttp.`interface`.HttpHandler
import com.example.androidservice.myhttp.HttpRequest
import com.example.androidservice.myhttp.HttpResponse
import java.io.*
import java.net.URLConnection


/**
 * Created by 铖哥 on 2018/3/17.
 */

open class BaseHttpHandler() : HttpHandler {
    override fun doPost(request: HttpRequest, response: HttpResponse) {

    }


    val rootPath: String = "/storage/emulated/0"

    override fun doGet(request: HttpRequest, response: HttpResponse) {

    }



    private fun closeHead(outputStream: OutputStream){
        outputStream.write("\r\n".toByteArray())
    }


    private fun getContentType(filename: String): String? {
        val file =  File(filename);
        val ins =  BufferedInputStream( FileInputStream(file));
        return  URLConnection.guessContentTypeFromStream(ins);
    }



}