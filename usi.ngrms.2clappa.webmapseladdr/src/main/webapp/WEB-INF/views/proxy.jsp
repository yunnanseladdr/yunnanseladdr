<%--
  Created by IntelliJ IDEA.
  User: Administrator
  Date: 2015/4/10
  Time: 16:04
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<HTML>
<HEAD>
  <META NAME="GENERATOR" Content="Microsoft Visual Studio 6.0">
  <TITLE>111111111</TITLE>
</HEAD>

<BODY>
  <form id="page_interface_form" name="page_interface_form" method="post">
    </form>
  <iframe name="page_interface_frame" width="100%" height="100%" ></iframe>
</BODY>
</HTML>
<script type="text/javascript">
  //嵌入集成页面
  call_page_interface();
  //在iframe中调用页面集成接口
  function call_page_interface(){
    document.getElementById("page_interface_form").action = "http://134.64.22.251:8088/search/addrsearch?fromsystem=icss";
    document.getElementById("page_interface_form").target = 'page_interface_frame';
    document.getElementById("page_interface_form").submit();
  }
</script>
