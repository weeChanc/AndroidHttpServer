package com.weechan.httpserver.httpserver.uitls

import android.content.Context
import android.util.Log
import dalvik.system.DexFile

/**
 * Created by jimiji on 2018/3/24.
 * to use this method,u need to disable instant run
 * Settings->Build->Instant Run
 * Instant Run will make the apk different from the release one
 */
private fun getClassesNameListInPackage(packageName: String, context: Context): List<String> {
    val realPackageName = "${context.packageName}.$packageName"
    val df = DexFile(context.packageCodePath)
    val enumration = df.entries()
    val list = mutableListOf<String>()
    while (enumration.hasMoreElements()) {
        val className = enumration.nextElement()
        if (className.contains(realPackageName) && !className.contains("$")) {
            Log.e("AndroidC",className )
            list.add(className)
        }
    }
    return list
}

fun getClassesInPackage(packageName: String, context: Context)
        = getClassesNameListInPackage(packageName, context).mapNotNull { Class.forName(it) }

