package com.example.androidservice.httpserver.reslover.reslovebean

/**
 * Created by 铖哥 on 2018/3/22.
 */

data class RequestHeaders(val headers: String) {

    private val headersMapper = hashMapOf<String, String>()

    init {

        val lines = headers.split("\r\n")
        lines.forEach {
            val divideIndex = it.indexOf(':')
            if (divideIndex == -1) return@forEach
            val key = it.substring(0, divideIndex).toLowerCase()
            var value = it.substring(divideIndex + 2, it.length)

            if(key == "content-type"){
                val middleIndex = it.indexOf(';')
                if(middleIndex != -1){
                    value = it.substring(divideIndex + 2,middleIndex)
                    val boundaryValue = it.substring(middleIndex+11,it.length)
                    headersMapper.put("boundary",boundaryValue)
                }
            }

            headersMapper.put(key, value)

        }

    }

    fun get(key: String): String? {
        return headersMapper.get(key)
    }
}