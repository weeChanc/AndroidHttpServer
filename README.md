# EasyHttpServer
simple service + simple demo

[![image](https://jitpack.io/v/weeChanc/AndroidService.svg)](https://jitpack.io/#weeChanc/AndroidService)

一个利用Kotlin语言编写的简易的Android嵌入式Http服务器,可以处理GET,POST请求,支持form-data,x-www-urlencoded表单解析,
支持直接访问Android手机所有文件并且支持断点续传

加入依赖
Step 1. Add the JitPack repository to your build file
allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}

Step 2. Add the dependency
dependencies {
	        compile 'com.github.weeChanc:AndroidService:0.1'
	}

使用方法
1.在应用程序根包名下创建一个包叫做handler
2.在该包下创建Handler
```
@Http("/") //指定请求访问的路径,"/"为本IP默认访问的Handler
class MainHandler() : HttpHandler {

    override fun doGet(request: HttpRequest, response: HttpResponse) {
       
        //设置返回头
        response.addHeaders {
            "Access-Control-Allow-Origin" - "*"
            "Access-Control-Allow-Methods" - "POST,GET"
        }
        
        //设置返回体
        response.write{
            "HELLO WORLD".byteInputStream().writeTo(this)
        }

    }

    override fun doPost(request: HttpRequest, response: HttpResponse) {
    }
}
```
3.在合适位置创建服务器
```
        val service = HttpServerFactory
                .with(this)
                .getHttpServer(8080)
        service.start()
```
4.打开浏览器访问手机IP(保证在同一局域网内)

其他例子可查看代码
