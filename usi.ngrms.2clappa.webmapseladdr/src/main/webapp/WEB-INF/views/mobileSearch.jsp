<%--
  Created by IntelliJ IDEA.
  User: limaple
  Date: 11/20/15
  Time: 10:26 AM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
    <meta name="viewport"
          content="width=device-width, initial-scale=1.0, maximum-scale=1.0, minimum-scale=1.0, user-scalable=no"/>
    <meta name="format-detection" content="telephone=no"/>
    <meta name="apple-mobile-web-app-capable" content="yes"/>
    <meta name="apple-mobile-web-app-status-bar-style" content="black"/>

    <meta name="baidu-tc-cerfication" content="ef2982c3430a61a4c8620d5e47f8b968"/>

    <link rel="apple-touch-startup-image"
          href="http://s1.map.bdimg.com/mobile/simple/static/common/images/startup_320_460_96280c8.jpg"/>
    <link rel="apple-touch-icon-precomposed"
          href="http://s1.map.bdimg.com/mobile/simple/static/common/images/logo_0800ec5.png"/>
    <link rel="stylesheet" type="text/css" href="./apps/search/css/mobileCss.css">
    <title>地图选址</title>
</head>
<body>
<div id="container">
    <!--搜索框-->
    <div id="mapSearchDiv">
        <input id="mapSearchInput"/>
        <button id="mapSearchButton" onclick="searchMap();">搜 索</button>
    </div>
    <!--地图-->
    <div id="allmap"></div>
    <!--网格信息-->
    <div id="gridShowDiv">
        <div id="gridInfo"></div>
        <span id="changePoiPrompt">温馨提示：点击地图选择您所在的位置</span>
    </div>
    <!--城市八级/农村九级地址-->
    <div id="subAddr" style="display: none">
        <div id="subAddrHead">
            <div id="back1" onclick="goback1();">&lt;</div>
            <div id="title1"></div>
        </div>
        <div id="subAddrContent"></div>
    </div>
    <!--用户地址-->
    <div id="userAddr" style="display: none">
        <div id="userAddrHead">
            <div id="back2" onclick="goback2();">&lt;</div>
            <div id="title2"></div>
        </div>
        <div id="userAddrContent"></div>
    </div>
    <!--资源能力-->
    <div id="ability" style="display: none">
        <div id="abilityHead">
            <div id="back3" onclick="goback3();">&lt;</div>
            <div id="title3"></div>
        </div>
        <div id="abilityContent"></div>
    </div>
    <!--用户自行填写提交地址-->
    <div id="userCommit" style="display: none">
        <div id="userCommitHead">
            <div id="back4" onclick="goback4();">&lt;</div>
            <div id="title4"></div>
        </div>
        <div id="userCommitContent"></div>
    </div>
    <!--用户自行填写提交地址提示-->
    <div id='userAddPrompt' onclick='userSbmAddr();'>没找到您的地址？点此：您可以手动提交你的详细地址</div>
    <!--查找网格信息时的遮蔽层-->
    <div id="mask" style="display: none">
        <span style="display: block; position: absolute; top: 50px; left: 30px; color: #000;">正在查询..</span>
    </div>
</div>

<!--调整页面大小为具体像素值,防止虚拟键盘弹出时页面变形-->
<script>
    window.screenHeight = document.body.clientHeight;
    document.getElementById("mapSearchDiv").style.height = screenHeight * 0.07 + "px";
    document.getElementById("gridShowDiv").style.height = screenHeight * 0.2 + "px";
    document.getElementById("allmap").style.height = screenHeight * 0.8 + "px";
    document.getElementById("subAddrHead").style.height = screenHeight * 0.07 + "px";
    document.getElementById("userAddrHead").style.height = screenHeight * 0.07 + "px";
    document.getElementById("abilityHead").style.height = screenHeight * 0.07 + "px";
    document.getElementById("userCommitHead").style.height = screenHeight * 0.07 + "px";
    document.getElementById("back1").style.lineHeight = screenHeight * 0.07 + "px";
    document.getElementById("back2").style.lineHeight = screenHeight * 0.07 + "px";
    document.getElementById("back3").style.lineHeight = screenHeight * 0.07 + "px";
    document.getElementById("back4").style.lineHeight = screenHeight * 0.07 + "px";

    document.getElementById("userAddPrompt").style.display = "none";

</script>
<script src="./apps/search/jquery-1.7.2.min.js" type="text/javascript"></script>
<script type="text/javascript" src="http://api.map.baidu.com/api?v=2.0&ak=fo01QPSZFnAGNewh6Lqssuu1"></script>
<script src="./apps/search/mobileSearch.js" type="text/javascript"></script>
</body>
</html>
