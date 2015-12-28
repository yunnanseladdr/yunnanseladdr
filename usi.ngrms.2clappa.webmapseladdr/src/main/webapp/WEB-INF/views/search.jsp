<%--
  Created by IntelliJ IDEA.
  User: Administrator
  Date: 2015/4/1
  Time: 18:45
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <base target="_self">
    <title>预选址集成界面</title>
    <script src="./apps/search/jquery-1.7.2.min.js" type="text/javascript"></script>
    <script src="./apps/search/easyui.js" type="text/javascript"></script>
    <script src="./apps/search/jq.js" type="text/javascript"></script>
    <link rel="icon" href="./apps/search/images/favicon.png" type="image/png">
    <link rel="stylesheet" type="text/css" href="./apps/search/css/css.css">
    <meta name="viewport" content="initial-scale=1.0, user-scalable=no"/>
</head>
<body>
<div id="kdzybody" style="display:block;">
    <!--头部标签-->
    <div id="sel_head">
        <span id="sel_label">宽带选址</span>
    </div>

    <!--tab页-->
    <div id="sel_tabs">
        <ul id="tab">
            <li id="inpTab" class="fli">选址</li>
            <li id="mapTab">&nbsp;&nbsp;地图定位选址</li>
        </ul>
    </div>

    <div id="tab_con">
        <div class="sel_tabs fdiv">
            <div id="searchAround">
                <div id="lj_selCity">
                    <label for="zy_city" class="posChoose"><span style="color:#ff8200;">* </span>区域</label>
                    <select class="w120" style="width: 80px; height: 23px;" id="lj_city"
                            onchange="zy_changerx(this.selectedIndex);">
                        <option value="0"><span style="color:#777;">请选择</span></option>
                        <option class="area" value="551">合肥</option>
                        <option value="553">芜湖</option>
                        <option value="552">蚌埠</option>
                        <option value="554">淮南</option>
                        <option value="555">马鞍山</option>
                        <option value="561">淮北</option>
                        <option value="562">铜陵</option>
                        <option value="556">安庆</option>
                        <option value="559">黄山</option>
                        <option value="550">滁州</option>
                        <option value="558">阜阳</option>
                        <option value="557">宿州</option>
                        <option value="564">六安</option>
                        <option value="567">亳州</option>
                        <option value="566">池州</option>
                        <option value="563">宣城</option>
                    </select>
                </div>
                <div id="lj_input">
                    <input id="searchArInput" type="text" value="临近号码查询"/>
                    <button id="searchArButton">查询</button>
                </div>
                <div id="aroundAddr" style="display: none;"></div>
            </div>
            <div class="search_bbx">
                <!--<span style="color: #f00;font-size: 0.8em;font-family: sans-serif;display: block;text-align: left;margin-bottom: 5px;">*请先选择所在地市</span>-->
                <div class="add">
                    <div id="chooseAddr">
                        <label for="zy_city" class="posChoose"><span style="color:#ff8200;">* </span>区域</label>
                        <select class="w120" id="zy_city" onchange="zy_changerx(this.selectedIndex);">
                            <option value="0"><span style="color:#777;">--请选择--</span></option>
                            <option class="area" value="551">合肥</option>
                            <option value="553">芜湖</option>
                            <option value="552">蚌埠</option>
                            <option value="554">淮南</option>
                            <option value="555">马鞍山</option>
                            <option value="561">淮北</option>
                            <option value="562">铜陵</option>
                            <option value="556">安庆</option>
                            <option value="559">黄山</option>
                            <option value="550">滁州</option>
                            <option value="558">阜阳</option>
                            <option value="557">宿州</option>
                            <option value="564">六安</option>
                            <option value="567">亳州</option>
                            <option value="566">池州</option>
                            <option value="563">宣城</option>
                        </select>
                        <label for="zy_qu" class="posChoose">子区域</label>
                        <select class="w120" id="zy_qu">
                            <option value="0"><span style="color:#777;">--请选择--</span></option>
                        </select>
                        <label for="zy_jd" class="posChoose">乡镇/街道</label>
                        <select class="w120" id="zy_jd">
                            <option value=""><span style="color:#777;">--请选择--</span></option>
                        </select>
                    </div>
                    <!--<div id="fullSearch">
                        <input type=radio name="radio" id="isSearchAddress" onclick="onClickRadio(this);">
                        <label>&nbsp;直接查询地址</label>
                    </div>-->
                </div>
                <div class="search-panel">
                    <div class="search-panel-fields">
                        <div class="search-combobox">
                            <div class="search-combobox-input-wrap">
                                <input type="text" id="zyAddress" class="search-combobox-input"
                                       value="关键字搜索参考：请输入小区，村庄或周边标志性建筑名，多个关键字之间以空格隔开" onclick="cityCheck2();">
                            </div>
                        </div>
                    </div>
                    <a class="clearText" onclick="document.getElementById('zyAddress').value='';">X</a>
                    <button id="searchButton" type="button" value="查询">查 询</button>
                </div>
                <div class="cl"></div>
                <!-- 分词展示框 -->
                <div class="Category_contA" style="display:none;" id="keyDiv">
                    <div class="contLeft fl">
                        <div class="contLeft_layout">
                            <ul class="catList" id="keyList"></ul>
                        </div>
                    </div>
                    <div class="contRight fr" style="display:none;" id="subKeyDiv">
                        <div class="contRight_layout">
                            <ul class="entity-main" id="subKeyList"></ul>
                        </div>
                    </div>
                </div>
                <div class="cl"></div>
                <!-- 地址展示框 -->
                <div class="Category_contB" style="display:none;" id="addressDiv">
                    <ul class="all_add"></ul>
                    <div id="fenyediv" class="fr" style="border-top:1px solid #dbdbdb;width: 100%;">
                        <div id="fenye">
                            <button id="lastPage" type="button">&lt;</button>
                            <span id="total"></span>&nbsp;&nbsp;<span style="color:#777; font-size: small;">第</span>
                            <input id="currentPageNo" type="text" onkeyup="this.value=this.value.replace(/[^\d]/g,'') "
                                   onafterpaste="this.value=this.value.replace(/[^\d]/g,'') " style="width: 20px;"/>
                            <span style="color:#777; font-size: small;">页&nbsp;&nbsp;</span>
                            <!--共计<input id = "totalPage" type="text" style="width: 40px" readonly="readonly" />页-->
                            <button id="goBut" type="button">Go!&nbsp;</button>
                            <button id="nextPage" type="button">&gt;</button>
                        </div>
                    </div>
                </div>
                <div class="cl"></div>
                <div class="zy_tab" style="display:none;margin:0 auto;" id="queryResult">
                    <table id="abilityTable" width="100%" border="0" cellspacing="0" cellpadding="0"></table>
                    <p id="res_prompt">温馨提示：因线路/端口有差异，本查询结果仅供参考，具体以安装人员上门核实结果为准。</p>
                    <button id="submitIcss">确认并受理</button>
                    <button id="projSearch2">工程信息查询</button>
                </div>
                <!-- 无查询结果 -->
                <div class="Category_contA" style="display:none;" id="noResultDiv"></div>
                <div id="loadi" cellpadding="0" cellspacing="0"
                     style="width:670px;height:65px;display:none;border:none;">
                    <div style="text-align: center;padding-top:30px;"><img src="./apps/search/images/loading.gif"/>
                    </div>
                </div>
            </div>
        </div>
        <div class="sel_tabs">
            <div id="sel_search">
                <div id="search_panel">
                    <div id="chooseArea" style="display: block">
                        <!--<span style="color: #f00;font-size: 0.8em;font-family: sans-serif;display: block;text-align: left;margin-left: 7px;margin-top: 10px;">*请先选择所在地市</span>-->
                        <label for="sel_city" id="sel_city_label">区域</label>
                        <select id="sel_city" onchange="changeMapByArea(this.selectedIndex);">
                            <option value="0">--请选择--</option>
                            <option class="area" value="551">合肥</option>
                            <option value="553">芜湖</option>
                            <option value="552">蚌埠</option>
                            <option value="554">淮南</option>
                            <option value="555">马鞍山</option>
                            <option value="561">淮北</option>
                            <option value="562">铜陵</option>
                            <option value="556">安庆</option>
                            <option value="559">黄山</option>
                            <option value="550">滁州</option>
                            <option value="558">阜阳</option>
                            <option value="557">宿州</option>
                            <option value="564">六安</option>
                            <option value="567">亳州</option>
                            <option value="566">池州</option>
                            <option value="563">宣城</option>
                        </select>
                    </div>
                    <input type="text" id="sel_input"
                           value=" 请输入您所在的位置" onclick="cityCheck();" onkeydown="if(event.keyCode==13) {searchMap();}"/>
                    <a class="clearText" onclick="document.getElementById('sel_input').value='';">X</a>
                    <input type="button" id="sel_mapsearchbutton" value="搜索" onclick="searchMap();"/>

                    <div id="searchResultPanel"
                         style="border:1px solid #C0C0C0;width:200px;height:auto; display:none;">
                    </div>
                </div>
            </div>
            <div id="result_panel"></div>
            <div id="sel_map">
                <p>map loading...</p>
            </div>
        </div>
    </div>
    <div id="btmLine"></div>

    <!--文字输入选址方式与比百度地图选址方式tab页切换-->
    <script type="text/javascript">
        //解决ie 6 7 8不支持getElementsByClassName的问题
        function getElementsByClassName(className, root, tagName) {    //root：父节点，tagName：该节点的标签名。 这两个参数均可有可无
            if (root) {
                root = typeof root == "string" ? document.getElementById(root) : root;
            } else {
                root = document.body;
            }
            tagName = tagName || "*";
            if (document.getElementsByClassName) {                    //如果浏览器支持getElementsByClassName，就直接的用
                return root.getElementsByClassName(className);
            } else {
                var tag = root.getElementsByTagName(tagName);    //获取指定元素
                var tagAll = [];                                    //用于存储符合条件的元素
                for (var i = 0; i < tag.length; i++) {                //遍历获得的元素
                    for (var j = 0, n = tag[i].className.split(' '); j < n.length; j++) {    //遍历此元素中所有class的值，如果包含指定的类名，就赋值给tagnameAll
                        if (n[j] == className) {
                            tagAll.push(tag[i]);
                            break;
                        }
                    }
                }
                return tagAll;
            }
        }

        var tabs = document.getElementById("tab").getElementsByTagName("li");
        var divs = getElementsByClassName("sel_tabs");
        for (var i = 0; i < tabs.length; i++) {
            tabs[i].onclick = function () {
                change(this);
            }
        }
        function change(obj) {
            for (var i = 0; i < tabs.length; i++) {
                if (tabs[i] == obj) {
                    tabs[i].className = "fli";
                    divs[i].className = "fdiv sel_tabs";
                }
                else {
                    tabs[i].className = "";
                    divs[i].className = "sel_tabs";
                }
            }
        }
        // 地图选址tab页地市选择提示
        function cityCheck() {
            if (document.getElementById("sel_city").selectedIndex == 0) {
                alert("请先选择所在区域");
            }
        }
        // 选址tab页地市选择提示
        function cityCheck2() {
            if (document.getElementById("zy_city").selectedIndex == 0) {
                alert("请先选择所在区域");
            }
        }
    </script>

    <script type="text/javascript">
        if (!window.$) {
            document.write("<script src='./apps/search/jquery-1.7.2.min.js'><\/script>");
        }
        //记录使用本系统的次数
        $(document).ready(function () {
            $.ajax({
                type: "post",
                url: "setSelaCount",
                dataType: "json",
                beforeSend: function () {
                },
                data: {
                    fromsystem: GetQueryString("fromsystem")
                },
                success: function (data) {

                },
                error: function (data) {

                }
            });
        });
    </script>
    <script type="text/javascript">
        function GetQueryString(name) {
            var reg = new RegExp("(^|&)" + name + "=([^&]*)(&|$)");
            var r = window.location.search.substr(1).match(reg);
            if (r != null)return unescape(r[2]);
            return null;
        }

        //页面初始化设置
        var tempradio;
        var isSearchAddress;
        $('#zy_city').val(0);
        /* $('#isSearchAddress').attr("checked", false);
         $("#zy_qu").prop("disabled", false);
         $("#zy_jd").prop("disabled", false);*/
        $("#zyAddress").val('关键字搜索参考：请输入小区，村庄或周边标志性建筑名，多个关键字之间以空格隔开');

        //根据访问路径的不同显示和隐藏按钮
        if (GetQueryString("fromsystem") != 'icss' || !GetQueryString("fromsystem")) {
            //隐藏确认并受理按钮
            $('#isSearchAddress').attr("checked", false);
            $("#zy_qu").prop("disabled", false);
            $("#zy_jd").prop("disabled", false);
            $("#submitIcss").css('display', 'none');
            $("#projSearch2").css('display', 'none');
            $("#acSubmit").css('display', 'none');
            tempradio = 'checked';
            isSearchAddress = '';
            document.getElementById("searchButton").innerHTML = "提 交";
            $("#searchAround").css('display', 'none');
            $("#sel_head").css('display', 'none');
            $("#kdzybody").css('padding-top', '10px');
            $("#btmLine").css('display', 'none');
        } else {
            $('#isSearchAddress').attr("checked", false);
            $("#zy_qu").prop("disabled", false);
            $("#zy_jd").prop("disabled", false);
            $("#res_prompt").css('display', 'none');
            isSearchAddress = '';
            tempradio = 'checked';
        }
        //当点击全地址查询时
        var onClickRadio = function (checkedRadio) {
            //console.log('单选按钮');
            if (tempradio == 'checked') {
                $('#isSearchAddress').attr("checked", false);
                //console.log($('#isIgnoreOther').attr("checked"));
                isSearchAddress = '1';
                tempradio = 'notchecked';
                $("#zy_qu").prop("disabled", false);
                $("#zy_jd").prop("disabled", false);
            } else {
                tempradio = 'checked';
                $('#isSearchAddress').attr("checked", true);
                $("#zy_qu").val(0);
                $("#zy_jd").val("");
                $("#zy_qu").prop("disabled", true);
                $("#zy_jd").prop("disabled", true);
                isSearchAddress = '';
            }
        }
    </script>

    <script src="./apps/search/jquery.json-2.4.min.js" type="text/javascript"></script>
    <script src="./apps/search/autoComplete.js" type="text/javascript"></script>
    <script src="./apps/search/index.js" type="text/javascript"></script>
    <script src="./apps/search/bdMap.js" type="text/javascript"></script>
    <script src="./apps/search/xqt.js" type="text/javascript"></script>

</div>
<iframe id="myfarme" src="" style="display:none;"></iframe>
</body>
</html>
