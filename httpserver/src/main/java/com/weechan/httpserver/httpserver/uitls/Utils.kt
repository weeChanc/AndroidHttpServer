package com.weechan.httpserver.httpserver.uitls

import android.text.TextUtils
import android.webkit.MimeTypeMap
import java.io.File
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.net.*
import java.util.*
import java.util.regex.Pattern

/**
 * Created by 铖哥 on 2018/3/18.
 */

fun InputStream.writeTo(outputStream: OutputStream, autoClose: Boolean = false, bufferSize: Int = 1024*2) {
    val buffer = ByteArray(bufferSize)
    val br = this.buffered()
    val bw = outputStream.buffered()
    var length = 0

    while ({ length = br.read(buffer); length != -1 }.invoke()) {
        bw.write(buffer, 0, length)
    }

    bw.flush()

    if (autoClose) {
        close()
    }
}

fun InputStream.string(): String {
    val buffer = ByteArray(1024*2)
    val br = this.buffered()
    var length = 0
    val sb = StringBuffer()

    while ({ length = br.read(buffer); length != -1 }.invoke()) {
        sb.append(String(buffer,0,length))
    }

    return sb.toString()
}



fun getHostIp(): String? {
    val regexCIp = "^192\\.168\\.(\\d{1}|[1-9]\\d|1\\d{2}|2[0-4]\\d|25\\d)\\.(\\d{1}|[1-9]\\d|1\\d{2}|2[0-4]\\d|25\\d)$"
    //匹配A类地址
    val regexAIp = "^10\\.(\\d{1}|[1-9]\\d|1\\d{2}|2[0-4]\\d|25\\d)\\.(\\d{1}|[1-9]\\d|1\\d{2}|2[0-4]\\d|25\\d)\\.(\\d{1}|[1-9]\\d|1\\d{2}|2[0-4]\\d|25\\d)$"
    //匹配B类地址
    val regexBIp = "^172\\.(1[6-9]|2\\d|3[0-1])\\.(\\d{1}|[1-9]\\d|1\\d{2}|2[0-4]\\d|25\\d)\\.(\\d{1}|[1-9]\\d|1\\d{2}|2[0-4]\\d|25\\d)$"

    val hostIp: String
    val ip = Pattern.compile("($regexAIp)|($regexBIp)|($regexCIp)")
    var networkInterfaces: Enumeration<NetworkInterface>? = null
    try {
        networkInterfaces = NetworkInterface.getNetworkInterfaces()
    } catch (e: SocketException) {
        e.printStackTrace()
    }

    var address: InetAddress
    while (networkInterfaces!!.hasMoreElements()) {
        val networkInterface = networkInterfaces!!.nextElement()
        val inetAddresses = networkInterface.getInetAddresses()
        while (inetAddresses.hasMoreElements()) {
            address = inetAddresses.nextElement()
            val hostAddress = address.getHostAddress()
            val matcher = ip.matcher(hostAddress)
            if (matcher.matches()) {
                hostIp = hostAddress
                return hostIp
            }

        }
    }
    return null
}



fun getMimeType(path: String): String {
    var mimeType = "application/octet-stream"
    if (!TextUtils.isEmpty(path)) {
        val extension = MimeTypeMap.getFileExtensionFromUrl(path)
        if (MimeTypeMap.getSingleton().hasExtension(extension))
            mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension)
    }
    return mimeType
}

