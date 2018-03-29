package com.example.androidservice.httpserver.reslover

/**
 * Created by 铖哥 on 2018/3/17.
 */
class URLResolver {
    companion object {
        fun getRequestRouter(path: String): String {
            val index = path.indexOf('?')
            if (index != -1) {
                return path.substring(0, index)
            }

            return path
        }

        /**
         * 获取请求URL中的参数名以及参数值
         */
        fun getRequestArgument(realPath: String): HashMap<String, String> {

            val argumentMap = hashMapOf<String,String>()

//            Log.e("REALPATH", realPath)
            val router = getRequestRouter(realPath)
//            Log.e("ROUTER", router)

            if(realPath == router) return argumentMap

            val arguments = realPath.substring(router.length+1, realPath.length)
            val args = arguments.split("&")

            args.forEach {
                val pair = it.split('=')
                argumentMap.put(pair[0],pair[1])
            }

            return argumentMap
        }
    }

}