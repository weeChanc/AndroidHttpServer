package com.weechan.httpserver.httpserver.reslover.body

import android.util.Log
import java.io.RandomAccessFile

/**
 * Created by осёз on 2018/3/27.
 */
class ResponseSink(val path :String, val offset : Long, val length : Int){

    private var readBytes = 0
    private val raf : RandomAccessFile by lazy {
        val r =  RandomAccessFile(path,"r") ;
        r.seek(offset)
        Log.e("ResponseSink", "File size is ${length}")
        r
    }
    fun read(buf:ByteArray): Int {
        if(readBytes >= length) return -1
        val maxSize = Math.min(buf.size,length-readBytes)
        val read =  raf.read(buf,0,maxSize)
        readBytes += read
        return read
    }

    fun close() {
        raf.close()
    }

    fun seek( pos : Long){
        raf.seek(offset+pos)
    }

}