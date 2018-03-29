package com.weechan.httpserver.httpserver

/**
 * Created by 铖哥 on 2018/3/18.
 */
class HttpState() {
    companion object {
        val Method_Not_Allowed = "HTTP/1.1 405 Method Not Allowed\r\n"
        val Not_Found_404 = "HTTP/1.1 404 Not Found\r\n"
        val OK_200 = "HTTP/1.1 200 OK\r\n"
        val Internal_Server_Error_500 = "HTTP/1.1 500 Internal Server Error\r\n"
        val RangeOK_206 = "HTTP/1.1 206 OK\r\n"
        val FOUR_O_FOUR_HTML by lazy {
            """<!DOCTYPE html>
<html>
<head>
	<title>FILE BROWSER FROM INTERNET</title>
</head>

	<style>
		html,body{
			height: 90%;
			width: 99%;
		}

		.four-oh-four{

			flex: 1;
			display: flex;
			justify-content: center;
			align-items: center;
			flex-direction: column;
			height: 95%;
			width: 99%;
		}

		img{
			flex: 1
			display: block;
		}

		.text{

			display: flex;
			justify-content: center;
			align-items: center;
			flex-direction: column;
		}

		a{
			text-decoration: none;
		}



	</style>



<body>

   <div class="four-oh-four">
      <img src="https://ionicframework.com/img/framework-four-oh-four.png">
      <div class="text">
        <h2 style="display: inline; margin-bottom: 4px ; color: #124214">Ooops. The page you're looking for got lost in space.</h2>
      	<p style="color: #a9a4b2">The address might be mistyped or the page may have moved.</p>
      	<a onmouseover="over(this)" onmouseout="out(this)" href="/" style="color: #4e8ffd" >Take me back home</a>
      </div>

   </div>
</body>

	<script type="text/javascript">

		function over(obj){
			obj.style.color="#95bbfb"
		}

		function out(obj){
			obj.style.color="#4e8ffd"
		}

	</script>
</html>""".trim()
        }
    }
}

