package com.example.androidservice.ui

import android.content.Context
import android.os.Bundle
import android.os.Environment
import androidx.appcompat.app.AppCompatActivity
import com.example.androidservice.databinding.ActivityMainBinding
import com.weechan.httpserver.httpserver.HttpServerBuilder
import com.weechan.httpserver.httpserver.uitls.getHostIp
import java.io.InputStream
import java.security.KeyStore
import javax.net.ssl.KeyManagerFactory
import javax.net.ssl.SSLContext


class MainActivity : AppCompatActivity() {


    private fun getClientSSLContext(context: Context): SSLContext {
        val keyStore = KeyStore.getInstance(KeyStore.getDefaultType())
        val keyStream: InputStream =
            context.applicationContext.assets.open("server.bks") //打开证书文件（.bks格式）
        val keyStorePass = "xxxxxx".toCharArray() //证书密码
        keyStore.load(keyStream, keyStorePass)
        val keyManagerFactory =
            KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm())
        keyManagerFactory.init(keyStore, keyStorePass)
        val clientContext = SSLContext.getInstance("TLS")
        clientContext.init(keyManagerFactory.keyManagers, null, null)
        return clientContext
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val viewBinding: ActivityMainBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)

        MediaRepository.init()
        val service = HttpServerBuilder
            .with(this)
            .port(8080)
            //.socketFactory(getClientSSLContext(this).serverSocketFactory)
            .getHttpServer()


        viewBinding.open.setOnClickListener {
            service.start()
            viewBinding.textView.text =
                getHostIp() + ":${8080}  tempFile${Environment.getExternalStorageDirectory().path}"
        }

        viewBinding.stop.setOnClickListener {
            service.stop()
            viewBinding.textView.text =
                getHostIp() + ":${8080}  tempFile${Environment.getExternalStorageDirectory().path}"
        }


    }


}
