package com.example.androidservice.handler



import com.weechan.httpserver.httpserver.HttpRequest
import com.weechan.httpserver.httpserver.HttpResponse
import com.weechan.httpserver.httpserver.annotaions.Http
import com.weechan.httpserver.httpserver.interfaces.HttpHandler
import com.weechan.httpserver.httpserver.uitls.getHostIp
import com.weechan.httpserver.httpserver.uitls.writeTo

/**
 * Created by 铖哥 on 2018/3/23.
 */
@Http("/")
class MainHandler() : HttpHandler {

    val ip : String = getHostIp()+":8080"

    val html = """
        <!DOCTYPE html>
<html lang="en">
<head>
       <meta charset="UTF-8">
    <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap.min.css">
    <script src="https://ajax.googleapis.com/ajax/libs/jquery/3.3.1/jquery.min.js"></script>
    <script src="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/js/bootstrap.min.js"></script>

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
            position: fixed;
        }

        main {
            margin-top: 98px;
            display: flex;
            width: 100%;
            height: 100%;
            flex-direction: row;
            /*justify-content: space-around;*/
        }

        .leftNav {
            display: flex;
            flex-direction: column;
            position: fixed;
            width: 100px;
        }

        .navItem {
            display: flex;
            padding: 8px;
            justify-content: center;
            align-items: center;

        }

        .navItem:hover {
            background-color: #e8e8e8;
        }

        .listView {
            margin-right: 32px;
            flex-grow: 1;
            margin-top: 8px;

            /*justify-content: center;*/
            flex-direction: column;
            margin-left: 100px;
        }

        .listItem {
            display: flex;
            margin-left: 8px;
            padding-left: 20px;
            justify-content: left;
            align-items: center;
        }

        .listItem:hover {
            background-color: #e8e8e8;
        }

        .icon {
            width: 32px;
            height: 32px;
        }

        .image{
            width: 120px;
            height: 120px;
            margin: 2px;
            object-fit: cover;
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

        <p class="navItem" onclick="init()">
            <image class="icon" src="http://p02rncoab.bkt.clouddn.com/folder.png" ></image>
            <span style="margin-left: 4px"> 根目录</span>
        </p>


        <p class="navItem"  onclick="getMedias('document')">
            <image class="icon" src="http://p02rncoab.bkt.clouddn.com/folder.png"></image>
            <span style="margin-left: 4px"> 文件</span>
        </p>

        <p class="navItem"  onclick="getMedias('photoDir')">
            <image class="icon" src="http://p02rncoab.bkt.clouddn.com/image.png"></image>
            <span style="margin-left: 4px"> 图片</span>
        </p>

        <p class="navItem"  onclick="getMedias('video')">
            <image class="icon" src="http://p02rncoab.bkt.clouddn.com/video.png"></image>
            <span style="margin-left: 4px"> 视频</span>
        </p>

        <p class="navItem"  onclick="getMedias('music')">
            <image class="icon"  src="http://p02rncoab.bkt.clouddn.com/music.png"></image>
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

    let ip = "192.168.156.2:8080";

    init();

    function clearListView(){
        document.getElementById("list_view").innerHTML = "";
    }

    function init() {
        let xhr = new XMLHttpRequest();
        xhr.onreadystatechange = function (ev) {

            if (xhr.readyState === 4) {
                if ((xhr.status >= 200 && xhr.status < 300) || xhr.status === 304) {
                    clearListView();
                    let data = JSON.parse(xhr.responseText);
                    for (let i = 0; i < data.mFile.length; i++) {

                        let child = document.createElement("div");
                        let text = document.createElement("span");
                        let link = document.createElement("a");
                        link.appendChild(text);
                        let image = document.createElement("img");

                        child.className = "card listItem";
                        let path = data.mFile[i].name;
                        text.innerText = `  文件路径:${"$"+"{path}"}`;
                        text.marginLeft = "4px";

                        image.setAttribute("width", "32px");
                        image.setAttribute("height", "32px");
                        if (!data.mFile[i].isFile) {
                            image.setAttribute("src", "http://p02rncoab.bkt.clouddn.com/document.png");
                            child.onclick = function (ev2) {
                                console.log(path);
                                alert(path);
                                xhr.open("get", `http://${ip}/find?path=` + path, true);
                                xhr.send()
                            };
                        } else {
                            image.setAttribute("src", "http://p02rncoab.bkt.clouddn.com/folder.png");
                            link.href = `http://${ip}/` + path;
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
        xhr.open("get", `http://${ip}/find?path=/storage/emulated/0/`, true);
        xhr.send()
    }

    function getMedias(type) {
        console.log("getMedias");
        let xhr = new XMLHttpRequest();
        xhr.onreadystatechange = function (ev) {
            if(xhr.readyState ===4){
                if ((xhr.status >= 200 && xhr.status < 300) || xhr.status === 304) {
                    clearListView();
                    let data = JSON.parse(xhr.responseText);
                    let listView = document.getElementById("list_view");
                    for( let i = 0 ; i < data.length ; i++){
                        let div = document.createElement("div");
                        div.className = "card listItem";
                        let text = document.createElement("span");
                        let link = document.createElement("a");
                        text.innerText = data[i].name;
                        link.href=`http://${ip}${ "$"+"{data[i].path}" }`;
                        link.target ="_blank";
                        // console.log(`${ip}${"$"+"{data[i].path}"}`);
                        link.appendChild(text);
                        div.appendChild(link);
                        listView.appendChild(div);

                        if(type === "photoDir"){
                            div.onclick = function (ev) {
                                getPhotos(data[i].path)
                            }
                        }
                    }
                }
            }
        };
        xhr.open("get", `http://${ip}/getMedia?type=${"$"+"{type}"}`, true);
        xhr.send()
    }

    function getPhotos(path){
        let listView = document.getElementById("list_view");
        let xhr = new XMLHttpRequest();
        xhr.onreadystatechange = function (ev) {
            if (xhr.readyState === 4) {
                if ((xhr.status >= 200 && xhr.status < 300) || xhr.status === 304) {
                    clearListView();

                    let data = JSON.parse(xhr.responseText);
                    console.log(data);
                    for( let i = 0 ; i < data.length ; i++){
                        let image= new Image();
                        image.src = `http://${ip}${"$"+"{data[i].path}"}`;
                        image.className = "image img-thumbnail";
                        listView.appendChild(image)
                    }
                }
            }
        };

        xhr.open("get", `http://${ip}/getMedia?type=photo&path=${"$"+"{path}"}`, true);
        xhr.send()
    }


</script>


</html>

    """.trimIndent()

    override fun doGet(request: HttpRequest, response: HttpResponse) {
        response.addHeaders {
            "Access-Control-Allow-Origin"-"*"
            "Access-Control-Allow-Methods"-"POST,GET"
        }
        response.write{
            html.byteInputStream().writeTo(this)
        }


    }

    override fun doPost(request: HttpRequest, response: HttpResponse) {
//        Log.e("MainHandler", request.getRequestBody().string)
    }



}