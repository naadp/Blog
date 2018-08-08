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

        function check(content){
            //正则表达式
            var reg = new RegExp("^[a-z0-9]+([._\\-]*[a-z0-9])*@([a-z0-9]+[-a-z0-9]*[a-z0-9]+.){1,63}[a-z0-9]+$");
            if(content === ""){
                //输入不能为空
                return false;
            }else if(!reg.test(content)){
                //正则验证不通过，格式不对
                return false;
            }else{
                //格式正确, 验证通过！
                return true;
            }
        }


        function clearData(){
            $("#email").val("");
            $("#newEmail").val("");
            $("#name").val("");
            $("#identityCheckCode").val("");
            $("#action").val("");
        }

       $(function () {
           // $("#button222").hide(0);
           $("#infoDialog").dialog({
               autoOpen: false,
               show: {
                   effect: "blind",
                   duration: 1000
               },
               hide: {
                   effect: "explode",
                   duration: 1000
               }
           });


           var allFields = $( [] ).add("");
           $( "#dialog-form" ).dialog({
               autoOpen: false,
               height: 300,
               width: 350,
               modal: true,
               buttons: {
                   "OK": function() {
                       var action = $("#action").val();
                       if(action=="订阅"){
                           //这里是订阅
                           var name = $("#name").val().trim();
                           var email = $("#email").val().trim();
                           // alert(name)
                           // alert(name.length)
                           if(name==""||name==null){
                              alert("name不能为空");
                               return ;
                           }
                           if(email==""||email==null){
                               alert("email不能为空");
                               return ;
                           }
                           //邮箱通过验证才可以, 这里在前后台都验证下
                            if(check(email)){
                               //验证通过，不管结果如何，先关闭对话框，并清空数据。先清空的话，可能让用户看到不太好！
                                $("#dialog-form").dialog( "close" );
                                clearData();
                               //这里可以发送请求了. 但是别忘了加个 .do 后缀啊, 因为拦截的是有 .do 和 .html 后缀的.
                                $.post("${pageContext.request.contextPath}/subscriber/addSubscriber.do",
                                        {"name":name,"email":email,"action":action},
                                        function(result){
                                            alert(result.info);

                                },"json");

                               // alert("邮箱验证通过")
                            }else{
                               alert("邮箱格式不正确");
                               return ;
                            }


                       }else if(action=="修改邮箱"){
                           //这里是修改邮箱
                           var email = $("#email").val().trim();
                           var newEmail = $("#newEmail").val().trim();
                           var identityCheckCode = $("#identityCheckCode").val().trim();

                           //记住：当不合法时，要结束方法。禁止它继续向下走。
                           if(email==""||email==null){
                               alert("老邮箱不能为空");
                               return ;
                           }else if(newEmail==""||newEmail==null){
                               alert("新邮箱不能为空");
                               return ;
                           }else if(identityCheckCode==""||identityCheckCode==null){
                               alert("身份验证码不能为空");
                               return ;
                           }

                           if(!check(email)){
                               alert("老邮箱验证不合法，请重新输入！");
                               return ;
                           }

                           if(!check(newEmail)){
                               alert("新邮箱验证不合法，请重新输入！");
                               return ;
                           }

                           if(newEmail===email){
                               alert("新老邮箱不能相同");
                               return ;
                           }

                           //验证通过，不管结果如何，先关闭对话框，并清空数据。先清空的话，可能让用户看到不太好！
                           $("#dialog-form").dialog( "close" );
                           clearData();
                           //这里可以发送请求了. 但是别忘了加个 .do 后缀啊, 因为拦截的是有 .do 和 .html 后缀的.
                           //实体类的属性叫 uuid啊，这点注意，否则后台接收时不会给你注入进去的.
                           $.post("${pageContext.request.contextPath}/subscriber/modifyEmail.do",
                               {"email":email, "uuid":identityCheckCode, "newEmail":newEmail, "name":"123"},
                               function(result){
                                   alert(result.info);

                               },"json");


                       }else if(action=="取消订阅"){
                           //这里是取消订阅
                           var email = $("#email").val().trim();
                           var identityCheckCode = $("#identityCheckCode").val().trim();



                           if(email==""||email==null){
                               alert("邮箱不能为空");
                               return ;
                           }else if(identityCheckCode==""||identityCheckCode==null){
                               alert("身份验证码不能为空");
                               return ;
                           }
                           if(!check(email)){
                               alert("邮箱验证不合法，请重新输入！");
                               return ;
                           }

                           $("#dialog-form").dialog( "close" );
                           clearData();
                           $.post("${pageContext.request.contextPath}/subscriber/notWatch.do",
                               {"email":email, "uuid":identityCheckCode, "name":"123"},
                               function(result){
                                   alert(result.info);

                               },"json");

                       }









                   },
                   Cancel: function() {
                       clearData();
                       $( this ).dialog( "close" );
                   }
               },
               close: function() {
                   clearData();
                   allFields.val( "" ).removeClass( "ui-state-error" );
               }
           });






           /*分隔符*/


           $("#aaa").click(function(){
               $("#button222").click();
               return false;
           });

           //这个事件是当用户点击订阅时触发的事件.需要设置下隐藏框的值
           $("#watch").click(function(){
               //设置标识位
               $("#action").val("订阅");

               //让name和邮箱显示出来：因为其他的方法中可能将它们关闭
               $("#nameLabel").show();
               $("#name").show();

               $("#emailLabel").show();
               $("#email").show();


               $("#newEmailLabel").hide();
               $("#newEmail").hide();
               $("#identityCheckCodeLabel").hide();
               $("#identityCheckCode").hide();

               $( "#dialog-form" ).dialog({title:'订阅本站博客'});
               $( "#dialog-form" ).dialog( "open" );
           });

           //这个事件是当用户修改邮箱时触发的事件. 需要设置下隐藏框的值
           $("#modifyEmail").click(function(){
               //设置标识位
               $("#action").val("修改邮箱");


               //让name隐藏
               $("#nameLabel").hide();
               $("#name").hide();

               //其他三个显示
               $("#emailLabel").show();
               $("#email").show();
               $("#newEmailLabel").show();
               $("#newEmail").show();
               $("#identityCheckCodeLabel").show();
               $("#identityCheckCode").show();

               $( "#dialog-form" ).dialog({title:'修改邮箱'});
               $( "#dialog-form" ).dialog( "open" );

           });


           //这个事件是当用户点击取消订阅时触发的事件.需要设置下隐藏框的值
           $("#notWatch").click(function(){
               //设置标识位
               $("#action").val("取消订阅");


               //让name和新邮箱隐藏
               $("#nameLabel").hide();
               $("#name").hide();
               $("#newEmailLabel").hide();
               $("#newEmail").hide();


               //让邮箱和UUID显示出来：因为其他的方法中可能将它们关闭
               $("#emailLabel").show();
               $("#email").show();


               $("#identityCheckCodeLabel").show();
               $("#identityCheckCode").show();

               $( "#dialog-form" ).dialog({title:'取消本站博客'});
               $( "#dialog-form" ).dialog( "open" );
           });






       });





    </script>
</head>
<body>
<a id="aaa" href="#"><font color="black"><strong>订阅本站博客</strong></font></a>

    <li hidden="hidden">
        <div class="btn-group">
            <button hidden="hidden" id="button222" type="button" class="btn btn-default dropdown-toggle btn-sm"
                    data-toggle="dropdown">

            </button>
            <script type="text/javascript">
                $("#button222").hide(0);
            </script>
            <br>
            <ul hidden="hidden" class="dropdown-menu" role="menu">
                <li><a id="watch" href="#">订阅</a></li>
                <li><a id="modifyEmail" href="#">修改通知邮箱</a></li>
                <li class="divider"></li>
                <li><a id="notWatch" href="#">取消订阅</a></li>
            </ul>
        </div>
    </li>






<div hidden="hidden" id="dialog-form" title="" style="font-size: 62.5%;" >
    <form>
        <fieldset>
            <%--
                该隐藏域是一个标识位,
                通过它来设置哪个输入框该隐藏
                它仅能有三个值：
                1. 订阅
                2. 修改邮箱
                3. 取消订阅
            --%>
            <input id="action" type="hidden" value="" />
            <label id="nameLabel" for="name">名字</label>
            <input type="text" name="name" id="name" class="text ui-widget-content ui-corner-all">
            <label id="emailLabel" for="email">邮箱</label>
            <input type="text" name="email" id="email" value="" class="text ui-widget-content ui-corner-all">
            <label id="newEmailLabel" for="newEmail">新邮箱</label>
            <input type="text" name="newEmail" id="newEmail" value="" class="text ui-widget-content ui-corner-all">
            <label id="identityCheckCodeLabel" for="identityCheckCode">身份验证码</label>
            <input type="text" name="identityCheckCode" id="identityCheckCode" class="text ui-widget-content ui-corner-all">
        </fieldset>
    </form>
</div>

</body>
</html>
