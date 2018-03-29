package com.example.androidservice.ui

/**
 * Created by 铖哥 on 2018/3/17.
 */

data class MFileResponse(val code : Int, val mFile: List<MFile>?)

data class MFile(val isFile: Boolean,
                 val size: Long,
                 val name: String)