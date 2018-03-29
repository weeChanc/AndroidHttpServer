package com.weechan.httpserver.httpserver.reslover.reslovebean

import com.weechan.httpserver.httpserver.reslover.body.FormDataPart
import com.weechan.httpserver.httpserver.reslover.body.ResponseBodyStreams
import java.io.DataInputStream
import java.io.InputStream

/**
 * Created by 铖哥 on 2018/3/22.
 */
data class RequestBody( val ins: DataInputStream, private val contentType: String?, val contentLength: Long?, private val boundary: String?) {


    var responseBodyStreams: ResponseBodyStreams? = null
    val map: HashMap<String, String> = hashMapOf()

    val string: String? by lazy { string() }


    init {
        try{
            when (contentType) {
                "application/x-www-form-urlencoded" -> processEncodedForm(map)
                "multipart/form-data" -> kotlin.run {
                    if (contentLength != null && boundary != null) {
                        responseBodyStreams = ResponseBodyStreams(ins, contentLength, boundary)
                    }
                }
            }
        }catch (e : Exception){
            throw RuntimeException("不能解析该数据,请检查请求的Content-type是否匹配")
        }
    }



    fun getRawData(): String? {
        if (contentType != "application/x-www-form-urlencoded" && contentType != "multipart/form-data")
            return string
        else
            throw Exception("CANNOT GET ROW DATA WHILE USE application/x-www-form-urlencoded AND multipart/form-data")
    }

    fun getPart(key: String): FormDataPart? {
        if (contentType != "multipart/form-data") throw RuntimeException("cannot get part from non form-data")
        return responseBodyStreams?.dataPartMapper?.get(key)
    }

    fun getxwwformValue(key: String): String? {
        return map.get(key)
    }


    private fun processEncodedForm(map: HashMap<String, String>) {
        string!!.split('&').forEach {
            val pair = it.split('=')
            map.put(pair[0], pair[1])
        }
    }

    //
    private fun string(): String? {
        if (contentLength == null || contentLength == 0L) return null
        var length: Long = contentLength
        val bufsize = 1024 * 1024
        val buffered = ByteArray(bufsize)
        val string = StringBuffer()
        while (length > 0) {
            if (length > bufsize) {
                ins.read(buffered, 0, bufsize)
                string.append(String(buffered, 0, bufsize))
                length -= bufsize
            } else {
                ins.read(buffered, 0, length.toInt())
                string.append(String(buffered, 0, length.toInt()))
                length = 0
            }

        }
        return string.toString()
    }
}