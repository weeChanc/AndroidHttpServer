package com.weechan.httpserver.httpserver.annotaion

/**
 * Created by 铖哥 on 2018/3/17.
 */

@Target( AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class Http(val route:String)