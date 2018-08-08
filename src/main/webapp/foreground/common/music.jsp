<%--
  Created by IntelliJ IDEA.
  User: 20605
  Date: 2018/5/29
  Time: 4:06
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Title</title>
    <link rel="stylesheet" href="//apps.bdimg.com/libs/jqueryui/1.10.4/css/jquery-ui.min.css">
    <script src="//apps.bdimg.com/libs/jquery/1.10.2/jquery.min.js"></script>
    <script src="//apps.bdimg.com/libs/jqueryui/1.10.4/jquery-ui.min.js"></script>

    <style>
        label, input { display:block; }
        input.text { margin-bottom:12px; width:95%; padding: .4em; }
        fieldset { padding:0; border:0; margin-top:25px; }
        h1 { font-size: 1.2em; margin: .6em 0; }
        div#users-contain { width: 350px; margin: 20px 0; }
        div#users-contain table { margin: 1em 0; border-collapse: collapse; width: 100%; }
        div#users-contain table td, div#users-contain table th { border: 1px solid #eee; padding: .6em 10px; text-align: left; }
        .ui-dialog .ui-state-error { padding: .3em; }
        .validateTips { border: 1px solid transparent; padding: 0.3em; }
    </style>
    <script type="text/javascript">

        $(function () {
            var audio = document.querySelector("audio");
            audio.play();
            var play = true;
            $("#pause").click(function(){
                if(play){
                    //修改内容
                    $("#pauseContent").text("播放");
                    //此时正在播放，应该暂停
                    audio.pause();
                    //修改标识位
                    play = false;
                }else{
                    //修改内容
                    $("#pauseContent").text("暂停");
                    //此时暂停了，应该播放
                    audio.play();
                    //修改标识位
                    play = true;
                }
            });

        });


    </script>
</head>
<body>
<%--<audio  src="/Blog/1.mp3" loop="loop"  ></audio>--%>
<a id="pause" href="#"><font color="black"><strong id="pauseContent" >暂停</strong></font></a>

</body>
</html>
