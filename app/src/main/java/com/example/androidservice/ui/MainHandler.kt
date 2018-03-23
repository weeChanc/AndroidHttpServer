package com.example.androidservice.ui

import android.content.Context
import com.example.androidservice.R
import com.example.androidservice.myhttp.HttpRequest
import com.example.androidservice.myhttp.HttpResponse
import com.example.androidservice.myhttp.`interface`.HttpHandler
import com.example.androidservice.myhttp.annotaion.Http
import com.example.androidservice.uitls.writeTo

/**
 * Created by 铖哥 on 2018/3/23.
 */
@Http("/")
class MainHandler(val ip : String) : HttpHandler{

    val html = """
        <!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <style type="text/css">

        * {
            margin: 0;
            color: #414141;
            font-size: 16px;
        }

        body {
            display: flex;
            height: 100%;
            width: 100%;
            background-color: #f1f1f1;
            flex-direction: column;
        }

        html {
            height: 100%;
            width: 100%;
        }

        .card {

            height: 60px;
            /*border-color: #dbdbdb;*/
            /*border-width: 0px;*/
            /*border-style: solid;*/
            box-shadow: 0 1px 1px 1px #cccccc;
            border-radius: 2px;
            margin-bottom: 8px;
            background-color: white;
        }

        .head {
            display: flex;
            width: 100%;
            height: 98px;
            margin-bottom: 8px;
            border-radius: 0;
            box-shadow: 0 2px 1px 1px #b7b7b7;
            font-size: 22px;
            text-align: center;

            justify-content: center;
            align-items: center;
        }

        main {
            display: flex;
            width: 100%;
            height: 100%;
            flex-direction: row;
            /*justify-content: space-around;*/
        }

        .leftNav {
            display: flex;
            flex-direction: column;
        }

        .navItem{
            display: flex;
            padding: 8px;
            justify-content: center;
            align-items: center;

        }

        .navItem:hover{
            background-color:#e8e8e8 ;
        }

        .listView {
            margin-right: 32px;
            flex-grow: 1;
            margin-top: 8px;

            /*justify-content: center;*/
            flex-direction: column;
        }

        .listItem{
            display: flex;
            margin-left: 8px;
            padding-left: 20px;
            justify-content: left;
            align-items: center;
        }

        .listItem:hover{
            background-color:#e8e8e8 ;
        }


        .icon {
            width: 32px;
            height: 32px;
        }

    </style>
    <title>文件浏览器</title>
</head>
<body>

<header class="card head">

    <span>文档头部</span>

</header>

<main>

    <div class="leftNav">

        <p class="navItem">
            <image class="icon" src="http://p02rncoab.bkt.clouddn.com/folder.png"></image>
            <span style="margin-left: 4px"> 文件</span>
        </p>

        <p class="navItem">
            <image class="icon" src="http://p02rncoab.bkt.clouddn.com/image.png"></image>
            <span style="margin-left: 4px"> 图片</span>
        </p>

        <p class="navItem">
            <image class="icon" src="http://p02rncoab.bkt.clouddn.com/video.png"></image>
            <span style="margin-left: 4px"> 视频</span>
        </p>

        <p class="navItem">
            <image class="icon" src="http://p02rncoab.bkt.clouddn.com/music.png"></image>
            <span style="margin-left: 4px"> 音乐</span>
        </p>
    </div>

    <div class="listView" id="list_view">

        <div class="card listItem">
            <image width="32px" height="32px" src="http://p02rncoab.bkt.clouddn.com/folder.png"></image>
            <span>storage/emulated/0/Pictures/friday</span>
        </div>

    </div>

</main>

</body>


<script type="text/javascript">


    let xhr = new XMLHttpRequest();
    xhr.onreadystatechange = function (ev) {

        document.getElementById("list_view").innerHTML = "";

        if (xhr.readyState === 4) {
            if ((xhr.status >= 200 && xhr.status < 300) || xhr.status === 304) {

                let data = JSON.parse(xhr.responseText);
                for (let i = 0; i < data.mFile.length; i++) {



                    let child = document.createElement("div");
                    let text = document.createElement("span");
                    let link = document.createElement("a");
                    link.appendChild(text);
                    let image = document.createElement("image");

                    child.className = "card listItem";
                    let path = data.mFile[i].name;
                    text.innerText = path;

                    image.setAttribute("width","32px");
                    image.setAttribute("height","32px");
                    if (!data.mFile[i].isFile) {
                        image.setAttribute("src","folder.png");
                        child.onclick = function (ev2) {
                            console.log(path);
                            xhr.open("get", "http://$ip/find?path=" + path, true);
                            xhr.send()
                        };
                    } else {
                        image.setAttribute("src","document.png");
                        link.href = "http://$ip/" + path;
                        link.target = "_blank"
                    }
                    child.appendChild(image);
                    child.appendChild(link);

                    document.getElementById("list_view").appendChild(child)
                }

            } else {
                alert("Request was unsuccessful: " + xhr.status);
            }
        }
    };

    xhr.open("get", "http://$ip/find?path=storage/emulated/0/", true);
    xhr.send()

</script>


</html>

    """.trimIndent()

    override fun doGet(request: HttpRequest, response: HttpResponse) {
        response.write{
            html.byteInputStream().writeTo(it)
        }
    }

    override fun doPost(request: HttpRequest, response: HttpResponse) {
    }



}