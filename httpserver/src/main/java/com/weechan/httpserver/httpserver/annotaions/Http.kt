package com.weechan.httpserver.httpserver.annotaions

/**
 * Created by 铖哥 on 2018/3/17.
 */

@Target( AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class Http(val route:String)