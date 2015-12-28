<%--
  Created by IntelliJ IDEA.
  User: Administrator
  Date: 2015/4/1
  Time: 18:45
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <base target="_self">
    <title>jsonp</title>
    <script src="./apps/search/jquery-1.7.2.min.js" type="text/javascript"></script>
    <script type="text/javascript">
        $(function(){
            /*
             //简写形式，效果相同
             $.getJSON("http://app.example.com/base/json.do?sid=1494&busiId=101&jsonpCallback=?",
             function(data){
             $("#showcontent").text("Result:"+data.result)
             });
             */
            $.ajax({
                type : "get",
                async:false,
                url : "http://192.168.210.200:8088/search/base/json.do?sid=1494&busiId=101",
                dataType : "jsonp",//数据类型为jsonp
                jsonp: "jsonpCallback",//服务端用于接收callback调用的function名的参数
                success : function(data){
                    $("#showcontent").text("Result:"+data.addrId)
                },
                error:function(){
                    alert('fail');
                }
            });
        });
    </script>
</head>
<body>
    <div id="showcontent">Result:</div>
</body>
</html>
