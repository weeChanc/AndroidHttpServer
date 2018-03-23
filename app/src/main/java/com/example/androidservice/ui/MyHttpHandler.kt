package com.example.androidservice.ui

import com.example.androidservice.myhttp.annotaion.Http
import com.example.androidservice.myhttp.`interface`.HttpHandler
import com.example.androidservice.myhttp.HttpRequest
import com.example.androidservice.myhttp.HttpResponse
import com.example.androidservice.uitls.MFile
import com.example.androidservice.uitls.MFileResponse
import com.google.gson.Gson
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
                writeFileMessage(file, it)
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