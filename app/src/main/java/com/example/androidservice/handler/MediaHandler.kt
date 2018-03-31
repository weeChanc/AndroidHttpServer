package com.example.androidservice.handler


import com.example.androidservice.ui.MediaRepository
import com.example.androidservice.uitls.writeTo
import com.google.gson.Gson
import com.weechan.httpserver.httpserver.HttpRequest
import com.weechan.httpserver.httpserver.HttpResponse
import com.weechan.httpserver.httpserver.interfaces.HttpHandler
import com.weechan.httpserver.httpserver.annotaions.Http
import java.io.InputStream

/**
 * Created by weechan on 18-3-24.
 */

@Http("/getMedia")
class MediaHandler : HttpHandler {


    override fun doGet(request: HttpRequest, response: HttpResponse) {
        val type = request.getRequestArgument("type")
        var jsonIn : InputStream? = null

        when (type) {
            "music" -> jsonIn = Gson().toJson(MediaRepository.getMusic()).byteInputStream()
            "document" -> jsonIn = Gson().toJson(MediaRepository.getDocument()) .byteInputStream()
            "video" ->jsonIn = Gson().toJson(MediaRepository.getVideo()).byteInputStream()
            "photoDir" -> jsonIn = Gson().toJson(MediaRepository.getPhotosDirectory()).byteInputStream()
            "photo" -> jsonIn = Gson().toJson(MediaRepository.getPhotos(request.getRequestArgument("path"))).byteInputStream()
        }


        response.write {
            jsonIn?.writeTo(this)
        }

        response.addHeaders {
            "Access-Control-Allow-Origin"-"*"
            "Access-Control-Allow-Methods"-"POST,GET"
        }
    }


    override fun doPost(request: HttpRequest, response: HttpResponse) {

    }

}