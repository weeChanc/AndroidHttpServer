package com.weechan.httpserver.httpserver.reslover.body

import android.os.Environment
import java.io.DataInputStream
import java.io.File
import java.io.RandomAccessFile
import java.nio.ByteBuffer
import java.nio.channels.FileChannel
import java.nio.charset.Charset
import java.util.regex.Pattern

/**
 * Created by ??? on 2018/3/27.
 */



class ResponseBodyStreams(val ins : DataInputStream, val formLength : Long, val boundary  : String) {

    private val boundaryLength = boundary.toByteArray().size
    var path : String = Environment.getExternalStorageDirectory().path+"/AndroidServiceTempFile/"

    private val raf : RandomAccessFile
    //所有边界的坐标
    private var bpos : IntArray
    //分割线的长度
    val dataPartMapper = hashMapOf<String, FormDataPart>()

    private lateinit var boundaries : Pair<Int,Int>
    private var totoalLength : Int = 0
    private var contentLength : Int = 0


    init {

        if(!File(path).exists()) File(path).mkdir()
        path += "${System.currentTimeMillis()}"
        raf = RandomAccessFile(path,"rw")
        saveInTempFile()

        val channel = raf.channel
        var buf = channel.map(FileChannel.MapMode.READ_ONLY, 0, raf.length())
        bpos = getBoundaryPositions(buf,boundary.toByteArray())

        //读取所有表单项,并存起来
        for(i in 0..bpos.size-2){
            //设置本次读取的界限
            setBoundary(bpos[i],bpos[i+1])

            //读取表单数据的描述
            val sb = StringBuilder()
            var line = raf.readLine()
            while(line.isNotEmpty()){
                line = String(line.toByteArray(Charset.forName("ISO-8859-1")), Charset.forName("utf-8"))
                sb.append(line+"\r\n")
                line = raf.readLine()
            }

            //表单数据的描述长度
            val descriptorLength = sb.toString().toByteArray().size

            //正文内容偏移量
            val offset = boundaries.first+boundaryLength+descriptorLength+4
            //正文内容大小
            contentLength = (totoalLength-descriptorLength-3)


            val part = resloveDescription(sb.toString())

            part.inputSink = ResponseSink(path, offset.toLong(),contentLength)

            dataPartMapper.put(part.key!!,part)
        }
        channel.close()
        buf = null
        raf.close()
        System.gc()
    }

    private fun saveInTempFile(){

        val requestDataOutpuStream = getTempFile()
        val buf = ByteArray(1024)
        var totalLength = formLength

        var length =  0;
        while(totalLength > 0){
            length = ins.read(buf)
            if(length > 0){
                requestDataOutpuStream.write(buf,0,length)
                totalLength -= length;
            }
        }

        requestDataOutpuStream.close()

    }

    private fun getTempFile(): RandomAccessFile {
        File(path)
        return RandomAccessFile(path,"rw")
    }

    private fun init(){
        totoalLength = boundaries.second-boundaries.first-boundaryLength-5
        raf.seek((boundaries.first+boundaryLength+2).toLong())
    }

    private fun setBoundary(from:Int , to :Int){
        boundaries = Pair(from,to)
        init()
    }

    private fun getBoundaryPositions(b: ByteBuffer, boundary: ByteArray): IntArray {
        var res = IntArray(0)
        if (b.remaining() < boundary.size) {
            return res
        }

        var search_window_pos = 0
        val search_window = ByteArray(4 * 1024 + boundary.size)

        val first_fill = if (b.remaining() < search_window.size) b.remaining() else search_window.size
        b.get(search_window, 0, first_fill)
        var new_bytes = first_fill - boundary.size

        do {
            // Search the search_window
            for (j in 0 until new_bytes) {
                for (i in boundary.indices) {
                    if (search_window[j + i] != boundary[i])
                        break
                    if (i == boundary.size - 1) {
                        // Match found, add it to results
                        val new_res = IntArray(res.size + 1)
                        System.arraycopy(res, 0, new_res, 0, res.size)
                        new_res[res.size] = search_window_pos + j
                        res = new_res
                    }
                }
            }
            search_window_pos += new_bytes

            // Copy the end of the buffer to the start
            System.arraycopy(search_window, search_window.size - boundary.size, search_window, 0, boundary.size)

            // Refill search_window
            new_bytes = search_window.size - boundary.size
            new_bytes = if (b.remaining() < new_bytes) b.remaining() else new_bytes
            b.get(search_window, boundary.size, new_bytes)
        } while (new_bytes > 0)


        return res
    }

    private fun resloveDescription(description : String): FormDataPart {

        val NAME_MATCHER = """\bname\b="(.*?)""""
        val FILENAME_MATCHER = """\bfilename\b="(.*?)""""
        val CONTENTTYPE_MATCHER = """\bContent-Type\b: (.*)"""


        val name : String?
        val fileName : String?
        val contentType: String?

        name = Pattern.compile(NAME_MATCHER).matcher(description).let {
            if(it.find()) it.group(1)
            else null
        }

        fileName = Pattern.compile(FILENAME_MATCHER).matcher(description).let {
            if(it.find()) it.group(1)
            else null
        }

        contentType = Pattern.compile(CONTENTTYPE_MATCHER).matcher(description).let {
            if(it.find()) it.group(1)
            else null
        }

        return FormDataPart(name,fileName,contentType)

    }
}