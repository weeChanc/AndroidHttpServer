package com.example.androidservice.handler

import com.example.androidservice.ui.MFile
import com.example.androidservice.ui.MFileResponse
import com.google.gson.Gson
import com.weechan.httpserver.httpserver.HttpRequest
import com.weechan.httpserver.httpserver.HttpResponse
import com.weechan.httpserver.httpserver.`interface`.HttpHandler
import com.weechan.httpserver.httpserver.annotaion.Http
import java.io.File
import java.io.OutputStream

/**
 * Created by 铖哥 on 2018/3/20.
 */

@Http(route = "/find")
class MyHttpHandler : HttpHandler {

    override fun doPost(request: HttpRequest, response: HttpResponse) {

    }

    override fun doGet(request: HttpRequest, response: HttpResponse) {
        val file = File(request.getRequestArgument("path"))
        if (file.isDirectory) {
            response.write {
                writeFileMessage(file, this)
            }
        }
        response.addHeaders {
            "Access-Control-Allow-Origin"-"*"
            "Access-Control-Allow-Methods"-"POST,GET"
        }
    }

    private fun writeFileMessage(file: File, output: OutputStream) {
        val response: MFileResponse
        if (!file.exists()) {
            response = MFileResponse(-1, null)
        } else {
            val mFiles = mutableListOf<MFile>()
            file.listFiles().forEach {
                mFiles.add(MFile(it.isFile, it.length(), it.path))
            }
            response = MFileResponse(1, mFiles)
        }
        val resp = Gson().toJson(response)
        output.write(resp.toByteArray())
    }

}