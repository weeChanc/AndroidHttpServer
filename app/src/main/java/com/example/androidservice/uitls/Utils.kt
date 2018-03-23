package com.example.androidservice.uitls

import android.text.TextUtils
import android.webkit.MimeTypeMap
import java.io.File
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.net.JarURLConnection
import java.net.URLDecoder
import java.util.HashSet

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


fun getMimeType(path: String): String {
    var mimeType = "application/octet-stream"
    if (!TextUtils.isEmpty(path)) {
        val extension = MimeTypeMap.getFileExtensionFromUrl(path)
        if (MimeTypeMap.getSingleton().hasExtension(extension))
            mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension)
    }
    return mimeType
}
object ClassUtil {


    /**
     * 获取类加载器
     * @Title: getClassLoader
     * @Description: TODO(这里用一句话描述这个方法的作用)
     * @param @return    设定文件
     * @return ClassLoader    返回类型
     * @throws
     */
    val classLoader: ClassLoader
        get() = Thread.currentThread().contextClassLoader

    /**
     * 加载类
     * 需要提供类名与是否初始化的标志，
     * 初始化是指是否执行静态代码块
     * @Title: loadClass
     * @Description: TODO(这里用一句话描述这个方法的作用)
     * @param @param className
     * @param @param isInitialized  为提高性能设置为false
     * @param @return    设定文件
     * @return Class    返回类型
     * @throws
     */
    fun loadClass(className: String, isInitialized: Boolean): Class<*> {

        val cls: Class<*>
        try {
            cls = Class.forName(className, isInitialized, classLoader)
            //Thread.currentThread().getContextClassLoader().loadClass(className);
        } catch (e: ClassNotFoundException) {
            e.printStackTrace()

            throw RuntimeException(e)
        }

        return cls
    }

    /**
     * 加载指定包下的所有类
     * @Title: getClassSet
     * @Description: TODO(这里用一句话描述这个方法的作用)
     * @param @param packageName
     * @param @return    设定文件
     * @return Set<Class></Class>>    返回类型
     * @throws
     */
    fun getClassSet(packageName: String): Set<Class<*>> {
        val classSet = HashSet<Class<*>>()

        try {
            val urls = classLoader.getResources(packageName.replace(".", "/"))

            while (urls.hasMoreElements()) {

                val url = urls.nextElement()

                if (url != null) {

                    val protocol = url.protocol

                    if (protocol == "file") {
                        // 转码
                        val packagePath = URLDecoder.decode(url.file, "UTF-8")
                        // String packagePath =url.getPath().replaceAll("%20",
                        // "");
                        // 添加
                        addClass(classSet, packagePath, packageName)

                    } else if (protocol == "jar") {

                        val jarURLConnection = url.openConnection() as JarURLConnection

                        if (jarURLConnection != null) {
                            val jarFile = jarURLConnection!!.getJarFile()

                            if (jarFile != null) {

                                val jarEntries = jarFile!!.entries()

                                while (jarEntries.hasMoreElements()) {

                                    val jarEntry = jarEntries.nextElement()

                                    val jarEntryName = jarEntry.getName()

                                    if (jarEntryName.endsWith(".dex")) {

                                        val className = jarEntryName.substring(0, jarEntryName.lastIndexOf("."))
                                                .replace("/".toRegex(), ".")
                                        doAddClass(classSet, className)

                                    }
                                }

                            }
                        }
                    }

                }

            }

        } catch (e: IOException) {
            e.printStackTrace()
        }

        return classSet
    }

    /**
     * 添加文件到SET集合
     * @Title: addClass
     * @Description: TODO(这里用一句话描述这个方法的作用)
     * @param @param classSet
     * @param @param packagePath
     * @param @param packageName    设定文件
     * @return void    返回类型
     * @throws
     */
    private fun addClass(classSet: MutableSet<Class<*>>, packagePath: String, packageName: String) {

        val files = File(packagePath).listFiles { file -> file.isFile && file.name.endsWith(".dex") || file.isDirectory }

        for (file in files) {

            val fileName = file.getName()

            if (file.isFile()) {
                var className = fileName.substring(0, fileName.lastIndexOf("."))

                if (packageName.isNotEmpty()) {

                    className = "$packageName.$className"
                }
                // 添加
                doAddClass(classSet, className)
            } else {
                // 子目录
                var subPackagePath = fileName
                if (packagePath.isNotEmpty()) {
                    subPackagePath = "$packagePath/$subPackagePath"
                }

                var subPackageName = fileName
                if (packageName.isNotEmpty()) {
                    subPackageName = "$packageName.$subPackageName"
                }

                addClass(classSet, subPackagePath, subPackageName)
            }
        }

    }

    private fun doAddClass(classSet: MutableSet<Class<*>>, className: String) {

        val cls = loadClass(className, false)
        classSet.add(cls)
    }

}