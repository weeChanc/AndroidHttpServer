package com.example.androidservice.ui.handler

import android.os.Environment
import android.util.Log
import com.example.androidservice.myhttp.HttpRequest
import com.example.androidservice.myhttp.HttpResponse
import com.example.androidservice.myhttp.`interface`.HttpHandler
import com.example.androidservice.myhttp.annotaion.Http
import com.example.androidservice.ui.FileInfo
import com.example.androidservice.ui.Media
import com.example.androidservice.uitls.writeTo
import com.google.gson.Gson
import java.io.File
import java.io.InputStream
import java.io.PrintWriter
import kotlin.properties.Delegates

/**
 * Created by weechan on 18-3-24.
 */

@Http("/getMedia")
class MediaHandler : HttpHandler {


    override fun doGet(request: HttpRequest, response: HttpResponse) {
        val type = request.getRequestArgument("type")
        var jsonIn : InputStream? = null

        //诡异 字符串貌似放不下这么多字符??????
        when (type) {
            "music" -> jsonIn = Gson().toJson(Media.getMusic()).byteInputStream()
            "document" -> jsonIn = Gson().toJson(Media.getDocument()) .byteInputStream()
            "video" ->jsonIn = Gson().toJson(Media.getVideo()).byteInputStream()
            "photo" -> jsonIn = Gson().toJson(Media.getPhotos()).byteInputStream()
        }
        File(Environment.getExternalStorageDirectory().path+"/output.txt").createNewFile()


        println("FINISH")

        response.write {
            jsonIn?.writeTo(it)
        }



    }

    data class InfosWrapper(val infos : List<FileInfo>)

    override fun doPost(request: HttpRequest, response: HttpResponse) {

    }

}