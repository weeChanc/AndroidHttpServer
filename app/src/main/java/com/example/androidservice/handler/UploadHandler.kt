package com.example.androidservice.handler

import android.os.Environment
import com.weechan.httpserver.httpserver.HttpRequest
import com.weechan.httpserver.httpserver.HttpResponse
import com.weechan.httpserver.httpserver.interfaces.HttpHandler
import com.weechan.httpserver.httpserver.annotaions.Http
import java.io.File

/**
 * Created by 铖哥 on 2018/3/25.
 */
@Http("/uplaod")
class  UploadHandler : HttpHandler{
    override fun doGet(request: HttpRequest, response: HttpResponse) {

    }

    override fun doPost(request: HttpRequest, response: HttpResponse) {

        val buf = ByteArray(1024)
        //根据key获取表单中key对应的部分
        val part = request.getRequestBody().getPart("photo")
        val input = part?.inputSink
        if (input != null){
            var length = input.read(buf)
            val out = File(Environment.getExternalStorageDirectory().path+"/${part.fileName}").outputStream().buffered()
            while(length != -1){
                out.write(buf,0,length)
                length = input.read(buf)
            }
            out.flush()
            input.close()
        }
        response.write { write("上传成功 ${Environment.getExternalStorageDirectory().path+"/${part?.fileName}"} ".toByteArray()) }
    }

}