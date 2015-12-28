<%--
  Created by IntelliJ IDEA.
  User: Administrator
  Date: 2015/4/13
  Time: 14:11
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
  <title></title>
</head>
<body>
      <input type="button" value="test" onclick="test();"/>
      <input id="returninfo" type="text" />
</body>
</html>
<script type="text/javascript">
  function test() {
    var paramObj = new Object();
    //弹出模式窗口,集成页面
    var ret_Value = window.showModalDialog("http://134.64.22.251:8088/search",paramObj,"dialogWidth:800px;dialogHeight:600px;help:no;status:no;dialogTitle:'宽带资源查询'");
    alert("返回值---" + ret_Value.addrId+";"+ret_Value.addrName+";"+ret_Value.subAreaId+";" + ret_Value.subAreaName+";"+ret_Value.isOpen);
    document.getElementById('returninfo').value = "返回值---" + ret_Value.addrId+";"+ret_Value.addrName+";"+ret_Value.subAreaId+";" + ret_Value.subAreaName+";"+ret_Value.isOpen;
  }
</script>