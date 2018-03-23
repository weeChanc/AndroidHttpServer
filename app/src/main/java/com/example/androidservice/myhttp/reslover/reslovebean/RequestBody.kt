package com.example.androidservice.myhttp.reslover.reslovebean

import java.io.DataInputStream

/**
 * Created by 铖哥 on 2018/3/22.
 */
data class RequestBody(private val ins: DataInputStream, private val contentType: String?, private val contentLength: Long?)  {

    val string : String? by lazy { string() }

    fun bodyArguments(): HashMap<String, String>? {
        if (contentLength == null || contentLength == 0L || contentType == null) return null
        val map : HashMap<String,String> = hashMapOf()

        when(contentType){
            "application/x-www-form-urlencoded"-> processEncodedForm(string,map)
        }

        return  map

    }

    private fun processEncodedForm(data : String? , map : HashMap<String,String>){
        if(string == null) return
        data!!.split('&').forEach {
            val pair = it.split('=')
            map.put(pair[0],pair[1])
        }
    }



    private fun string(): String? {
        if (contentLength == null || contentLength == 0L) return null
//            var clock = System.currentTimeMillis()
//            val string = StringBuffer()
//            val blockingThread: Thread
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


//            blockingThread = thread {
//                try {
//                    while ({ val l = ins.readLine(); string.append(l+"\r\n"); clock = System.currentTimeMillis();true }());
//                } catch (e: Exception) {
//                    e.printStackTrace()
//                }
//            }
//
//            while (System.currentTimeMillis() - clock < 200) {
//                Thread.sleep(100)
//            }
//            blockingThread.interrupt()
////
////            Log.e("ReqeustBody", string.toString())
//            return string.toString()
    }
//
//        fun bytes():ByteArray{
//            val blockingThread: Thread
//            val buf = ByteArray(1024*1024)
//            var clock = System.currentTimeMillis()
//
//            blockingThread = thread {
//                try {
//                    while ({ val l = ins.read(buf); ; clock = System.currentTimeMillis();true }());
//                } catch (e: Exception) {
//                    e.printStackTrace()
//                }
//            }
//
//            while (System.currentTimeMillis() - clock < 200) {
//                Thread.sleep(100)
//            }
//            blockingThread.interrupt()
//
//            return string.toString()
//        }
}