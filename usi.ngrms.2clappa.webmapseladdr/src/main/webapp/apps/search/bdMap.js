/**
 * Created by limaple on 15/8/19.
 *
 */

var gloabAddrId = '';
var gloabAddrName = '';
var gloabSiteId = '';
var gloabSiteCode = '';
var gloabSiteName = '';
var gloabOldSubAreaId = '';
var gloabZySubAreaName = '';

var gridName = '';
var grid68Name = '';
var gridType = '';
var userAddrList = '';
var currentCity = '';
var mapAutoComplete;
var lastInfoWin = [];

// 获取资源能力接口
var resAbilityUrl =
    //"http://134.64.110.182:9999/service/mboss/route?wsdl";      // -- 测试
    "http://134.64.116.90:9102/service/mboss/route?wsdl";     // -- 生产

//初始化地图
function initialize() {
    createMap(117.292939, 31.875945, 9);
    map.centerAndZoom("合肥", 9);
    changeMapByArea(document.getElementById("sel_city").selectedIndex);
    setMapEvent();
    addMapControl();
}

//移动到兴趣点
function moveToPoi(lng, lat) {
    var point = new BMap.Point(lng, lat);
    map.centerAndZoom(point, 17);
}

//创建地图函数：
function createMap(x, y, level) {
    var map = new BMap.Map("sel_map", {enableMapClick: false});//在百度地图容器中创建一个地图
    var point = new BMap.Point(x, y);//定义一个中心点坐标
    map.centerAndZoom(point, level);//设定地图的中心点和坐标并将地图显示在地图容器中
    window.map = map;//将map变量存储在全局
    var ac = new BMap.Autocomplete(    //建立一个自动完成的对象
        {
            "input": "sel_input",
            "location": "安徽省"
        });
    mapAutoComplete = ac;

    ac.addEventListener("onhighlight", function (e) {  //鼠标放在下拉列表上的事件
        var str = "";
        var _value = e.fromitem.value;
        var value = "";
        if (e.fromitem.index > -1) {
            value = _value.province + _value.city + _value.district + _value.street + _value.business;
        }
        str = "FromItem<br />index = " + e.fromitem.index + "<br />value = " + value;

        value = "";
        if (e.toitem.index > -1) {
            _value = e.toitem.value;
            value = _value.province + _value.city + _value.district + _value.street + _value.business;
        }
        str += "<br />ToItem<br />index = " + e.toitem.index + "<br />value = " + value;
        G("searchResultPanel").innerHTML = str;
    });

    var myValue;
    ac.addEventListener("onconfirm", function (e) {    //鼠标点击下拉列表后的事件
        var _value = e.item.value;
        myValue = _value.province + _value.city + _value.district + _value.street + _value.business;
        G("searchResultPanel").innerHTML = "onconfirm<br />index = " + e.item.index + "<br />myValue = " + myValue;

        setPlace();
    });

    function G(id) {
        return document.getElementById(id);
    }

    var local = new BMap.LocalSearch(map, {
        renderOptions: {
            map: map,
            autoViewport: false,
            selectFirstResult: false
        },
        pageCapacity: 9,
        onSearchComplete: function (results) {
            if (local.getStatus() == BMAP_STATUS_SUCCESS) {
                var pp = local.getResults().getPoi(0).point;    //获取第一个智能搜索的结果
                map.centerAndZoom(pp, 17);
            }
            // 判断状态是否正确
            var marker = ['A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I'];
            if (local.getStatus() == BMAP_STATUS_SUCCESS) {
                var s = new StringBuffer();
                s.append("<ul id='pointList'>");
                for (var i = 0; i < results.getCurrentNumPois(); i++) {
                    s.append("<li onclick='moveToPoi(\"" + results.getPoi(i).point.lng + "\", \"" + results.getPoi(i).point.lat + "\");'><span class='poiName'>" + marker[i] + ": " + results.getPoi(i).title + "</span><span class='poiAdd'>地址: " + results.getPoi(i).address + "</span></li>");
                }
                s.append("</ol>");
                document.getElementById("result_panel").innerHTML = s.toString();
            }
            else {
                document.getElementById("result_panel").innerHTML = "<span id='mapNotFoundWarn'> &nbsp; &nbsp; &nbsp; &nbsp;未找到相关地址，请检查输入是否正确或者输入其它关键词。</span>";
            }

        }
    });

    window.local = local;

    function setPlace() {
        map.clearOverlays();    //清除地图上所有覆盖物
        local.search(myValue);
    }

    map.addEventListener("click", function (e) {
        lastInfoWin.length = 0;
        removePolygon();
        if (map.getZoom() >= 12) {
            window.clickPoint = e.point;

            var lng = clickPoint.lng; // 用户点击获取坐标经度
            var lat = clickPoint.lat; // 用户点击获取坐标纬度
            var areaCode = document.getElementById("sel_city").options[document.getElementById("sel_city").selectedIndex].value;
            window.areaCode = areaCode;
            if ((lng == null) || (lng == "")) return;
            if ((lat == null) || (lat == "")) return;
            if (areaCode == 0) {
                alert("请先选择所在区域");
                return;
            }

            var opts = {
                width: 100,
                height: 30,
                title: ""
            };
            var infoWindow = new BMap.InfoWindow("请稍等...", opts);  // 创建信息窗口对象
            map.openInfoWindow(infoWindow, clickPoint);      // 打开信息窗口

            $.ajax({
                type: "post",
                url: "getMapAddrInfo",
                dataType: "json",
                beforeSend: function () {
                },
                data: {
                    lng: lng,
                    lat: lat
                },
                success: function (_data) {
                    if (_data == null) {
                        noDataWarn();
                        return;
                    }
                    var data;
                    var gridPolygon;
                    $.each(_data, function (key, value) {
                        gridType = key.split(":")[0];
                        gridName = key.split(":")[1];
                        grid68Name = key.split(":")[2];
                        gridPolygon = key.split(":")[3];
                        window.gridId = key.split(":")[4];

                        data = value;
                    });
                    window.data = data;
                    var opts = {
                        width: 370,     // 信息窗口宽度
                        height: 150,     // 信息窗口高度
                        title: gridName  // 信息窗口标题
                    };

                    if (data == null) {
                        noDataWarn();
                        return;
                    }

                    var cont = new StringBuffer();
                    if (GetQueryString("fromsystem") == 'icss') {
                        cont += "<ol id='xq_list'>";
                    } else {
                        cont += "<ol id='xq_list' class='mapHandIcon'>";
                    }
                    var emptyFlag = 1;
                    for (var i = 0; i < data.length; ++i) {
                        var address = data[i].nameBak;
                        var re = new RegExp("<em>", "g");
                        var re2 = new RegExp("</em>", "g");
                        address = address.replace(re, "").replace(re2, '');
                        var singleAddr = address.replace(/ /g, '');
                        if (singleAddr == grid68Name || (gridType == '农村' && singleAddr.indexOf(grid68Name) >= 0)) {
                            var addressId = address + 0;
                            if (gridType == "城市" || areaCode == "555") {
                                emptyFlag = 0;
                                cont += "<li style='padding:13px 0;' onclick='get9ofCity(" + i + ");' addId='" + addressId + "' title='" + address + "'>地址: " + address + "</li>";
                            } else if (gridType == "农村") {
                                emptyFlag = 0;
                                cont += "<li style='padding:10px;' onclick='get9ofCountry(\"" + address.split(" ")[0] + "\");' addId='" + addressId + "' title='" + address + "'>地址: " + address.split(" ")[0] + "</li>";
                                break;
                            } else {
                            }
                        }
                    }
                    cont += "</ol>";

                    if (GetQueryString("fromsystem") != 'icss' || !GetQueryString("fromsystem")) {
                        var serviceTel = "暂无";
                        $.ajax({
                            async: false,
                            type: "post",
                            url: "getServiceTel",
                            dataType: "json",
                            beforeSend: function () {
                            },
                            data: {
                                gridId: gridId
                            },
                            success: function (data) {
                                if (data !== '' && data != null) {
                                    serviceTel = data;
                                }
                            },
                            error: function (data) {
                            }
                        });
                        cont += ("<div style='position:absolute; bottom: 20px;'><span id='mapTelPrompt'>装维咨询: " + serviceTel + "</span></div>");

                        cont += "<div style='position:absolute; bottom: 0;'><span class='mapAbiPrompt'>温馨提示: 点击地址选择详细住址信息</span></div>";
                    }

                    if (emptyFlag == 1) {
                        cont += "<p style='margin-top:20px;font-size:15px;color:#333;'>&nbsp;&nbsp;&nbsp;&nbsp;所选地点未查询到相关地址信息，请重新选点尝试或使用其他选址方式</p>";
                    }

                    var winInfo = cont.toString();

                    var infoWindow = new BMap.InfoWindow(winInfo, opts);  // 创建信息窗口对象
                    map.openInfoWindow(infoWindow, clickPoint);      // 打开信息窗口
                    lastInfoWin.push(infoWindow);


                    /*EXAMPLE: POLYGON  (( 116.61979332 31.92874318, 116.61963095 31.90976766, 116.62668389 31.90915851,
                     116.62701226 31.91207226, 116.64695820 31.91507437, 116.64451914 31.92880132, 116.63482310 31.92809492,
                     116.61979332 31.92874318))
                     MULTIPOLYGON  ((( 118.51191231 31.67685247, 118.51182903 31.67577338, 118.51233228 31.67575197,
                     118.51240636 31.67561743, 118.51241336 31.67530943, 118.51301236 31.67530243, 118.51351936 31.67529643,
                     118.51352036 31.67553543, 118.51352836 31.67566343, 118.51351636 31.67642743, 118.51350298 31.67692742,
                     118.51268326 31.67695954, 118.51191231 31.67685247)),(( 118.51190398 31.67682391, 118.51185758 31.67662999,
                     118.51186829 31.67644677, 118.51188138 31.67662999, 118.51190398 31.67682391)))*/
                    var polygons;
                    if (gridPolygon.indexOf("GON") >= 0) {
                        polygons = gridPolygon.split("GON")[1].split(")),((");
                    } else {
                        polygons = gridPolygon.split(")),((");
                    }
                    for (var iter = 0; iter < polygons.length; ++iter) {
                        var polygon = polygons[iter];
                        var polygon_ins = polygon.split("),(");
                        for (var iter1 = 0; iter1 < polygon_ins.length; ++iter1) {
                            var polygon_in = polygon_ins[iter1];
                            var points = [];
                            var pointStrs = polygon_in.replace(/\(/g, ' ').replace(/\)/g, ' ').trim().split(", ");
                            var cycle = 0;
                            for (var iter2 = 0; iter2 < pointStrs.length; ++iter2) {
                                var pointStr = pointStrs[iter2];
                                points[cycle++] = new BMap.Point(pointStr.split(" ")[0], pointStr.split(" ")[1]);
                                //points.add(new BMap.Point(pointStr.split(" ")[0], pointStr.split(" ")[1]));
                            }
                            var convertor = new BMap.Convertor();
                            var subArray = [];
                            for (var iter3 = 0; iter3 < points.length; iter3 += 9) {
                                if (iter3 + 10 < points.length) {
                                    subArray = points.slice(iter3, iter3 + 10);
                                    convertor.translate(subArray, 0, 5, translateCallBack)
                                } else {
                                    subArray = points.slice(iter3);
                                    convertor.translate(subArray, 0, 5, translateCallBack)
                                }
                            }
                        }
                    }

                },
                error: function (data) {
                }
            });
        }
    });
}

//点击搜索按钮搜索
function searchMap() {
    map.clearOverlays();    //清除地图上所有覆盖物
    var target = currentCity + document.getElementById("sel_input").value;
    //local.clearResults();
    local.search(target);
}

//地图事件设置函数：
function setMapEvent() {
    map.enableDragging();//启用地图拖拽事件，默认启用(可不写)
    map.enableScrollWheelZoom();//启用地图滚轮放大缩小
    map.enableDoubleClickZoom();//启用鼠标双击放大，默认启用(可不写)
    map.enableKeyboard();//启用键盘上下左右键移动地图
}

//地图控件添加函数：
function addMapControl() {
    //向地图中添加缩放控件
    var ctrl_nav = new BMap.NavigationControl({
        anchor: BMAP_ANCHOR_TOP_LEFT,
        type: BMAP_NAVIGATION_CONTROL_LARGE
    });
    map.addControl(ctrl_nav);
    //向地图中添加缩略图控件
    var ctrl_ove = new BMap.OverviewMapControl({
        anchor: BMAP_ANCHOR_BOTTOM_RIGHT,
        isOpen: 1
    });
    map.addControl(ctrl_ove);
    //向地图中添加比例尺控件
    var ctrl_sca = new BMap.ScaleControl({
        anchor: BMAP_ANCHOR_BOTTOM_LEFT
    });
    map.addControl(ctrl_sca);
    //向地图添加地图类型控件
    map.addControl(new BMap.MapTypeControl({mapTypes: [BMAP_NORMAL_MAP, BMAP_SATELLITE_MAP, BMAP_HYBRID_MAP]}));

}

// 当变换所选区域时，改变地图焦点位置
function changeMapByArea(index) {
    var obj = document.getElementById("sel_city");
    var city = obj.options[index].text;
    currentCity = city;
    if (index != 0) {
        map.centerAndZoom(city, 12);
        mapAutoComplete.setLocation(city + "市");
    }
}

// 获取九级地址列表
function get9ofCity(index) {
    var opts = {
        width: 100,
        height: 30,
        title: ""
    };
    var infoWindow = new BMap.InfoWindow("请稍等...", opts);  // 创建信息窗口对象
    map.openInfoWindow(infoWindow, clickPoint);      // 打开信息窗口

    var buildings = data[index].subNames;
    var xqName = data[index].nameBak;
    var re = new RegExp("<em>", "g");
    var re2 = new RegExp("</em>", "g");
    xqName = xqName.replace(re, "").replace(re2, '');
    //若九级地址列表为空，直接搜索八级地址下的标准地址
    if (!buildings || buildings.split(" ").length == 0) {
        getAddressBy9(xqName, "");
        return;
    }
    window.buildingArray = buildings.split(" ");

    var cont = new StringBuffer();
     cont.append("<div class='goBack' onclick='backTo();'>&nbsp; &nbsp; 返回</div>");
    cont.append("<div style='height: 200px;width: 400px; overflow: auto;'>");
    cont.append("<div id = 'citySearchPanel'><input id='city9LSearch' value='搜索: \"" + gridName + "\"下的地址信息' onclick='cleanContent(this)'  oninput='city9LSearch(\"" + xqName + "\", " + "this.value);' onpropertychange='city9LSearch(\"" + xqName + "\", " + "this.value);'/><button id='city9LSearchButton'>搜索</button></div>");
    cont.append("<div id='city9List'><ol id='xq_list'>");
    for (var i = 0; i < buildingArray.length; i++) {
        cont.append("<li onclick='getAddressBy9(\"" + xqName + "\", \"" + buildingArray[i] + "\")' style='width:77px; display: inline-block;overflow:hidden;white-space: nowrap;text-overflow:ellipsis;' title='" + buildingArray[i] + "'>" + buildingArray[i] + "</li>");
    }
    cont.append("</ol></div></div>");
    if (GetQueryString("fromsystem") != 'icss' || !GetQueryString("fromsystem")) {
        cont.append("<div style='position:absolute; bottom: 0;'><span id='userAddPrompt' onclick='userSbmAddr();'>没找到您的地址？点此：您可以手动提交你的详细地址</span></div>");
    }
    var winInfo = cont.toString();

    opts = {
        width: 400,
        height: 250,
        title: gridName
    };

    infoWindow = new BMap.InfoWindow(winInfo, opts);  // 创建信息窗口对象
    map.openInfoWindow(infoWindow, clickPoint);      // 打开信息窗口
    lastInfoWin.push(infoWindow);
}

// 由农村网格名称（六级地址）获取八级地址
function get9ofCountry(gridName) {
    var opts = {
        width: 100,
        height: 30,
        title: ""
    };
    var infoWindow = new BMap.InfoWindow("请稍等...", opts);  // 创建信息窗口对象
    map.openInfoWindow(infoWindow, clickPoint);      // 打开信息窗口

    $.ajax({
        url: "getSuggAddrWithInput",
        dataType: "json",
        type: "post",
        data: {
            areaCode: areaCode,
            subAreaName: "",
            streetName: "",
            input: gridName,
            pageNo: 1,
            pageSize: 20
        },
        success: function (data) {
            if (data == null || data.length == 0) {
                noDataWarn();
                return;
            }
            opts = {
                width: 400,
                height: 320,
                title: gridName
            };
            var cont = new StringBuffer();
            cont += "<div class='goBack' onclick='backTo();'>&nbsp; &nbsp; 返回</div>";
            cont += "<div style='width: 400px; height: 270px; overflow: auto;'>";
            cont += "<div id = 'countrySearchPanel'><input id='country9LSearch' value='搜索: \"" + gridName + "\"下的地址信息' onclick='cleanContent(this)'  oninput='country9LSearch(\"" + gridName + "\", " + "this.value);' onpropertychange='country9LSearch(\"" + gridName + "\", " + "this.value);'/><button id='country9LSearchButton'>搜索</button></div>";
            cont += "<div id = 'country9List'><ol id='xq_list'>";
            for (var i = 0; i < data.length; i++) {
                var address = data[i].nameBak;
                var re = new RegExp("<em>", "g");
                var re2 = new RegExp("</em>", "g");
                address = address.replace(re, "").replace(re2, '');
                if (address.replace(' ', '').indexOf(grid68Name) < 0) {
                    continue;
                }
                var addressId = address + i;
                cont += "<li onclick='getAddressBy9(\"" + address + "\", \"" + address.split(" ")[1] + "\")'; title=\"" + address.split(" ")[1] + "\">" + data[i].nameBak + "</li>";
            }
            cont += "</ol></div></div>";
            if (GetQueryString("fromsystem") != 'icss' || !GetQueryString("fromsystem")) {
                cont += ("<div style='position:absolute; bottom: 0;'><span id='userAddPrompt' onclick='userSbmAddr();'>没找到您的地址？点此：您可以手动提交你的详细地址</span></div>");

            }
            var winInfo = cont.toString();

            var infoWindow = new BMap.InfoWindow(winInfo, opts);  // 创建信息窗口对象
            map.openInfoWindow(infoWindow, clickPoint);      // 打开信息窗口
            lastInfoWin.push(infoWindow);
        },
        error: function () {
        }
    });
}

// 由九级地址获取标准地址列表
function getAddressBy9(xqName, ldName) {
    var opts = {
        width: 100,
        height: 30,
        title: ""
    };
    var infoWindow = new BMap.InfoWindow("请稍等...", opts);  // 创建信息窗口对象
    map.openInfoWindow(infoWindow, clickPoint);      // 打开信息窗口

    $.ajax({
        url: "getUserAddrByInput",
        dataType: "json",
        type: "post",
        data: {
            areaCode: areaCode,
            input: xqName + ldName,
            pageNo: 1,
            pageSize: 50
        },
        success: function (data) {
            if (data == null || data.length == 0) {
                noDataWarn();
                return;
            }
            opts = {
                width: 500,
                height: 300,
                title: gridName
            };
            var cont = new StringBuffer();
            var adList = new StringBuffer();
            var temp = xqName.split(" ");
            var promptName = temp[temp.length - 1];
            var count = 0;
            cont += "<div class='goBack' onclick='backTo();'>&nbsp; &nbsp; 返回</div>";
            cont += "<div style='width: 500px; height: 250px; overflow: auto;'>";
            cont += "<div id = 'userAddSearchPanel'>" +
                "<input id='userAddrSearch' value='搜索: \"" + promptName + ldName + "\"下的地址信息' onclick='cleanContent(this)' oninput='userAddSearch(\"" + xqName + "\", \"" + ldName + "\", " + "this.value);' onpropertychange='userAddSearch(\"" + xqName + "\", \"" + ldName + "\", " + "this.value);'/><button id='userAddrSearchButton'>搜索</button></div>";
            cont += "<div id = 'userAdList'><ol id='xq_list'>";
            for (var i = 0; i < data.length; i++) {
                var addName = data[i].highlightText;
                var re = new RegExp("<em>", "g");
                var re2 = new RegExp("</em>", "g");
                var re3 = new RegExp(" ", "g");
                addName = addName.replace(re, "").replace(re2, "").replace(re3, "");
                if (addName.indexOf(gridName) < 0) {
                    continue;
                }
                if (ldName.length > 1 && addName.indexOf(ldName) < 0) {
                    continue;
                }

                if (data[i].sites) {
                    if (data[i].sites.length > 0) {
                        var allSiteNames = '';
                        var allSiteIds = '';
                        var allSiteCodes = '';
                        for (var ii = 0; ii < data[i].sites.length; ii++) {
                            if (ii == data[i].sites.length - 1) {
                                allSiteNames += data[i].sites[ii].siteName;
                                allSiteIds += data[i].sites[ii].siteId;
                                allSiteCodes += data[i].sites[ii].siteCode;
                            } else {
                                allSiteNames += data[i].sites[ii].siteName + ',';
                                allSiteIds += data[i].sites[ii].siteId + ',';
                                allSiteCodes += data[i].sites[ii].siteCode + ',';
                            }
                        }
                        if (data[i].subAreaName) {
                            cont += "<li onclick='getResAbility($(\"#sel_city\").val(),$(this).find(\"a\").attr(\"oldSubAreaId\"),$(this).find(\"a\").attr(\"zySubAreaName\"),$(this).find(\"a\").attr(\"addrId\"),$(this).find(\"a\").attr(\"siteId\"),$(this).find(\"a\").attr(\"siteName\"),$(this).find(\"a\").attr(\"siteCode\"),\"" + resAbilityUrl + "\", $(this).find(\"a\").text())'><a oldSubAreaId='" + data[i].oldSubAreaId + "' addrId='" + data[i].id + "' siteId='" + allSiteIds + "' siteName='" + allSiteNames + "' siteCode='" + allSiteCodes + "' zySubAreaName='" + data[i].subAreaName + "' >" + data[i].highlightText + "</a></li>";
                        } else {
                            cont += "<li onclick='getResAbility($(\"#sel_city\").val(),$(this).find(\"a\").attr(\"oldSubAreaId\"),$(this).find(\"a\").attr(\"zySubAreaName\"),$(this).find(\"a\").attr(\"addrId\"),$(this).find(\"a\").attr(\"siteId\"),$(this).find(\"a\").attr(\"siteName\"),$(this).find(\"a\").attr(\"siteCode\"),\"" + resAbilityUrl + "\", $(this).find(\"a\").text())'><a oldSubAreaId='" + data[i].oldSubAreaId + "' addrId='" + data[i].id + "' siteId='" + allSiteIds + "' siteName='" + allSiteNames + "' siteCode='" + allSiteCodes + "' zySubAreaName=''>" + data[i].highlightText + "</a></li>";
                        }

                    }
                } else {
                    cont += "<li onclick='getResAbility($(\"#sel_city\").val(),$(this).find(\"a\").attr(\"oldSubAreaId\"),$(this).find(\"a\").attr(\"zySubAreaName\"),$(this).find(\"a\").attr(\"addrId\"),$(this).find(\"a\").attr(\"siteId\"),$(this).find(\"a\").attr(\"siteName\"),$(this).find(\"a\").attr(\"siteCode\"),\"" + resAbilityUrl + "\", $(this).find(\"a\").text())'><a addrId='" + data[i].id + "' siteId='' siteCode='' siteName=''" + " oldSubAreaId='' zySubAreaName=''>" + data[i].highlightText + "</a></li>";
                }
                ++count;
            }
            userAddrList = adList.toString();
            cont += userAddrList;
            cont += "</ol></div></div>";
            if (GetQueryString("fromsystem") != 'icss' || !GetQueryString("fromsystem")) {
                cont += "<div style='position:absolute; bottom: 0;'><span id='userAddPrompt' onclick='userSbmAddr();'>没找到您的地址？点此：您可以手动提交你的详细地址</span></div>";
            }
            if (count == 0) {
                var cont1 = new StringBuffer();
                cont1.append("<div class='goBack' onclick='backTo();'>&nbsp; &nbsp; 返回</div>");
                cont1.append("<div style='width: 550px; height:260px; overflow: auto'>");
                cont1.append("<input id='addrResAbil' />");
                cont1.append("<button id='sbmitAd' onclick='sbmitAd();'>提交</button>");
                if (GetQueryString("fromsystem") == 'icss') {
                    cont1.append("<table id='resAbility' style='margin-top: 20px;'>" +
                        "<tr class=\"bg\">" +
                        "<td>产品名称</td><td>接入方式</td><td>资源能力</td><td>理论下行速率</td><td>G/EPON属性</td><td>局向</td><td>支持e8-c</td>" +
                        "</tr><tr>" +
                        "<td></td><td></td><td>不具备</td><td></td><td></td><td></td><td></td>" +
                        "</tr></table>");
                    cont1.append("<button id='acSubmit' onclick='sbmitAdIcss();'>确认并受理</button>");
                } else {
                    cont1.append("<table id='resAbility' style='margin-top: 20px;'>" +
                        "<tr class=\"bg\">" +
                        "<td>接入方式</td><td>资源能力</td><td>理论下行速率</td><td>适用终端类型</td>" +
                        "</tr><tr>" +
                        "<td></td><td>不具备</td><td></td><td></td>" +
                        "</tr></table>");
                }
                if (GetQueryString("fromsystem") != 'icss' || !GetQueryString("fromsystem")) {
                    cont1.append("<p class='mapAbiPrompt'>温馨提示：因线路/端口有差异，本查询结果仅供参考，具体以安装人员上门核实结果为准。</p>");
                }
                cont1.append("</div>");
                if (GetQueryString("fromsystem") != 'icss' || !GetQueryString("fromsystem")) {
                    cont1.append("<div style='position:absolute; bottom: 0;'><span id='userAddPrompt' onclick='userSbmAddr();'>没找到您的地址？点此：您可以手动提交你的详细地址</span></div>");

                }
                var winInfo1 = cont1.toString();
                opts = {
                    width: 550,
                    height: 260,
                    title: "查询结果"
                };
                var infoWindow1 = new BMap.InfoWindow(winInfo1, opts);  // 创建信息窗口对象
                map.openInfoWindow(infoWindow1, clickPoint);      // 打开信息窗口
                lastInfoWin.push(infoWindow1);
                return;
            }

            var winInfo = cont.toString();
            var infoWindow = new BMap.InfoWindow(winInfo, opts);  // 创建信息窗口对象
            map.openInfoWindow(infoWindow, clickPoint);      // 打开信息窗口
            lastInfoWin.push(infoWindow);
        },
        error: function () {
        }
    });
}

// 从用户地址列表返回上一级地址列表
function backTo() {
    map.openInfoWindow(lastInfoWin[lastInfoWin.length - 2], clickPoint);
    lastInfoWin.pop();
}

// 用户地址本地搜索
function userAddSearch(xqName, ldName, input) {
    $.ajax({
        url: "getUserAddrByInput",
        dataType: "json",
        type: "post",
        data: {
            areaCode: areaCode,
            input: xqName + " " + ldName + " " + input,
            pageNo: 1,
            pageSize: 15
        },
        success: function (data) {
            if (data == null || data.length == 0) {
                document.getElementById("userAdList").innerHTML = "<ol id='xq_list><li>未找到相关地址</li><ol>";
                return;
            }
            var cont = new StringBuffer();
            cont += "<ol id='xq_list'>";
            for (var i = 0; i < data.length; i++) {
                if (data[i].sites) {
                    if (data[i].sites.length > 0) {
                        var allSiteNames = '';
                        var allSiteIds = '';
                        var allSiteCodes = '';
                        for (var ii = 0; ii < data[i].sites.length; ii++) {
                            if (ii == data[i].sites.length - 1) {
                                allSiteNames += data[i].sites[ii].siteName;
                                allSiteIds += data[i].sites[ii].siteId;
                                allSiteCodes += data[i].sites[ii].siteCode;
                            } else {
                                allSiteNames += data[i].sites[ii].siteName + ',';
                                allSiteIds += data[i].sites[ii].siteId + ',';
                                allSiteCodes += data[i].sites[ii].siteCode + ',';
                            }
                        }
                        if (data[i].subAreaName) {
                            cont += "<li onclick='getResAbility($(\"#sel_city\").val(),$(this).find(\"a\").attr(\"oldSubAreaId\"),$(this).find(\"a\").attr(\"zySubAreaName\"),$(this).find(\"a\").attr(\"addrId\"),$(this).find(\"a\").attr(\"siteId\"),$(this).find(\"a\").attr(\"siteName\"),$(this).find(\"a\").attr(\"siteCode\"),\"" + resAbilityUrl + "\", $(this).find(\"a\").text())'><a oldSubAreaId='" + data[i].oldSubAreaId + "' addrId='" + data[i].id + "' siteId='" + allSiteIds + "' siteName='" + allSiteNames + "' siteCode='" + allSiteCodes + "' zySubAreaName='" + data[i].subAreaName + "' >" + data[i].highlightText + "</a></li>";
                        } else {
                            cont += "<li onclick='getResAbility($(\"#sel_city\").val(),$(this).find(\"a\").attr(\"oldSubAreaId\"),$(this).find(\"a\").attr(\"zySubAreaName\"),$(this).find(\"a\").attr(\"addrId\"),$(this).find(\"a\").attr(\"siteId\"),$(this).find(\"a\").attr(\"siteName\"),$(this).find(\"a\").attr(\"siteCode\"),\"" + resAbilityUrl + "\", $(this).find(\"a\").text())'><a oldSubAreaId='" + data[i].oldSubAreaId + "' addrId='" + data[i].id + "' siteId='" + allSiteIds + "' siteName='" + allSiteNames + "' siteCode='" + allSiteCodes + "' zySubAreaName=''>" + data[i].highlightText + "</a></li>";
                        }

                    }
                } else {
                    cont += "<li onclick='getResAbility($(\"#sel_city\").val(),$(this).find(\"a\").attr(\"oldSubAreaId\"),$(this).find(\"a\").attr(\"zySubAreaName\"),$(this).find(\"a\").attr(\"addrId\"),$(this).find(\"a\").attr(\"siteId\"),$(this).find(\"a\").attr(\"siteName\"),$(this).find(\"a\").attr(\"siteCode\"),\"" + resAbilityUrl + "\", $(this).find(\"a\").text())'><a addrId='" + data[i].id + "' siteId='' siteCode='' siteName=''" + " oldSubAreaId='' zySubAreaName=''>" + data[i].highlightText + "</a></li>";
                }
            }
            cont += "</ol>";

            document.getElementById("userAdList").innerHTML = cont.toString();
        },
        error: function () {
        }
    });
}

//城市九级地址搜索
function city9LSearch(xqName, input) {
    var cont = new StringBuffer();
    cont.append("<ol id='xq_list'>");
    for (var i = 0; i < buildingArray.length; i++) {
        if (input == '' || buildingArray[i].indexOf(input) >= 0) {
            cont.append("<li onclick='getAddressBy9(\"" + xqName + "\", \"" + buildingArray[i] + "\")' style='width:77px; display: inline-block;overflow:hidden;white-space: nowrap;text-overflow:ellipsis;' title='" + buildingArray[i] + "'>" + buildingArray[i] + "</li>");
        }
    }
    cont.append("</ol>");
    document.getElementById("city9List").innerHTML = cont.toString();
}

// 农村八级地址搜索
function country9LSearch(gridName, input) {
    $.ajax({
        url: "getSuggAddrWithInput",
        dataType: "json",
        type: "post",
        data: {
            areaCode: areaCode,
            subAreaName: "",
            streetName: "",
            input: gridName + " " + input,
            pageNo: 1,
            pageSize: 15
        },
        success: function (data) {
            if (data == null || data.length == 0) {
                document.getElementById("country9List").innerHTML = "<ol id='xq_list><li>未找到相关地址</li><ol>";
                return;
            }
            else {
                var cont = new StringBuffer();
                cont += "<ol id='xq_list'>";
                for (var i = 0; i < data.length; i++) {
                    var address = data[i].nameBak;
                    var re = new RegExp("<em>", "g");
                    var re2 = new RegExp("</em>", "g");
                    address = address.replace(re, "").replace(re2, '');
                    var addressId = address + i;
                    cont += "<li onclick='getAddressBy9(\"" + address + "\", " + "\" \"" + ")'; title=\"" + address.split(" ")[1] + "\">" + data[i].nameBak + "</li>";
                }
                cont += "</ol>";
                document.getElementById("country9List").innerHTML = cont.toString();
            }
        },
        error: function () {
        }
    });
}

// 查找资源能力
function getResAbility(areaCode, oldSubAreaId, zySubAreaName, addrId, siteIds, siteNames, siteCodes, oipUrl, addrName) {
    var opts = {
        width: 100,
        height: 30,
        title: ""
    };
    var infoWindow = new BMap.InfoWindow("正在查询，请稍等...", opts);  // 创建信息窗口对象
    map.openInfoWindow(infoWindow, clickPoint);      // 打开信息窗口

    gloabAddrId = addrId;
    gloabAddrName = addrName.replace(/<em>/g, '').replace(/<\/em>/g, '');
    gloabSiteId = siteIds;
    gloabSiteCode = siteCodes;
    gloabSiteName = siteNames;
    gloabOldSubAreaId = oldSubAreaId;
    gloabZySubAreaName = zySubAreaName;

    $.ajax({
        type: "post",
        url: "getResAbility",
        dataType: "json",

        data: {
            'areaCode': areaCode,
            'addrId': addrId,
            'siteIds': siteIds,
            'siteNames': siteNames,
            'oipUrl': oipUrl
        },
        success: function (data) {
            var winInfo;
            if (data) {
                var tableCont = new StringBuffer();
                tableCont.append("<div class='goBack' onclick='backTo();'>&nbsp; &nbsp; 返回</div>");
                tableCont.append("<div style='width: 550px; height:260px; overflow: auto'>");
                if (!GetQueryString("fromsystem") || GetQueryString("fromsystem") != 'icss') {
                    tableCont.append("<input id='addrResAbil' value='" + gloabAddrName + "' />");
                    tableCont.append("<button id='sbmitAd' onclick='sbmitAd();'>提交</button>");
                } else {
                    tableCont.append("<input id='addrResAbil' style='width:520px;' value='" + gloabAddrName + "' />");
                }
                tableCont.append("<table id='resAbility'>");
                tableCont.append("<tr class=\"bg\">");
                if (GetQueryString("fromsystem") == 'icss') {
                    tableCont.append("<td>产品名称</td>");
                }
                tableCont.append("<td>接入方式</td>");
                tableCont.append("<td>资源能力</td>");
                tableCont.append("<td>理论带宽</td>");
                if ((GetQueryString("fromsystem") != 'icss' || !GetQueryString("fromsystem"))) {
                    tableCont.append("<td>适用终端类型</td>");
                }
                if (GetQueryString("fromsystem") == 'icss') {
                    tableCont.append("<td>G/EPON属性</td>");
                    tableCont.append("<td>局向</td>");
                    tableCont.append("<td>支持e8-c</td>");
                }
                tableCont.append("</tr>");
                if (data.length > 0) {
                    for (var i = 0; i < data.length; i++) {
                        var row = data[i];
                        if (row.productName.indexOf('宽带') < 0 && (GetQueryString("fromsystem") != 'icss' || !GetQueryString("fromsystem"))) {
                            continue;
                        }
                        tableCont.append("<tr>");
                        if (GetQueryString("fromsystem") == 'icss') {
                            tableCont.append("<td>" + row.productName + "</td>");
                        }
                        if (row.terminalPortType == '1') {
                            tableCont.append("<td>ADSL</td>");
                        } else if (row.terminalPortType == '2') {
                            tableCont.append("<td>LAN</td>")
                        } else if (row.terminalPortType == '3') {
                            tableCont.append("<td>FTTH</td>");
                        } else if (row.terminalPortType == '4') {
                            tableCont.append("<td>铜缆</td>");
                        } else {
                            tableCont.append("<td>&nbsp;</td>");
                        }
                        //资源能力
                        if (row.ability == '0') {
                            tableCont.append("<td>具备</td>");
                        } else if (row.ability == '1') {
                            tableCont.append("<td>不具备</td>");
                        } else {
                            tableCont.append("<td>&nbsp;</td>");
                        }
                        //理论下行速率
                        if (row.logicRate) {
                            tableCont.append("<td>" + row.logicRate + "</td>");
                        } else {
                            tableCont.append("<td>&nbsp;</td>");
                        }
                        //G/EPON属性
                        if (row.ponType == '1') {
                            tableCont.append("<td>EPON</td>");
                        } else if (row.ponType == 2) {
                            tableCont.append("<td>GPON</td>");
                        } else {
                            tableCont.append("<td>&nbsp;</td>");
                        }
                        if (GetQueryString("fromsystem") == 'icss') {
                            //局向
                            if (row.postDirection) {
                                tableCont.append("<td>" + row.postDirection + "</td>")
                            } else {
                                tableCont.append("<td>&nbsp;</td>")
                            }
                            //支持e8-c
                            if (row.isE8C == '0') {
                                tableCont.append("<td>否</td>");
                            } else if (row.isE8C == '1') {
                                tableCont.append("<td>是</td>");
                            } else {
                                tableCont.append("<td>&nbsp;</td>");
                            }
                        }
                        tableCont.append("</tr>");
                    }
                    tableCont.append("</table>");
                    if (GetQueryString("fromsystem") == 'icss') {
                        tableCont.append("<button id='acSubmit' onclick='sbmitAdIcss();'>确认并受理</button>");
                        tableCont.append("<button id='projSearch' onclick='projSearch();'>工程信息查询</button><div>");
                    }
                    if (GetQueryString("fromsystem") != 'icss' || !GetQueryString("fromsystem")) {
                        tableCont.append("<p class='mapAbiPrompt'>温馨提示：因线路/端口有差异，本查询结果仅供参考，具体以安装人员上门核实结果为准。</p>");
                        tableCont.append("<div style='position:absolute; bottom: 0;'><span id='userAddPrompt' onclick='userSbmAddr();'>没找到您的地址？点此：您可以手动提交你的详细地址</span></div>");
                    }
                }
                winInfo = tableCont.toString();
            } else {
                var noResCont = new StringBuffer();
                noResCont += "<div class='goBack' onclick='backTo();'>&nbsp; &nbsp; 返回</div>";
                noResCont += "<p style='margin-top:20px;font-size:15px;color:#333;'>未查到地址对应宽带办理能力信息!</p>";
                if (GetQueryString("fromsystem") != 'icss' || !GetQueryString("fromsystem")) {
                    noResCont += ("<div style='position:absolute; bottom: 0;'><span id='userAddPrompt' onclick='userSbmAddr();'>没找到您的地址？点此：您可以手动提交你的详细地址</span></div>");
                }
                winInfo = noResCont.toString();
            }
            var opts = {
                width: 550,
                height: 280,
                title: "查询结果"
            };
            var infoWindow = new BMap.InfoWindow(winInfo, opts);  // 创建信息窗口对象
            map.openInfoWindow(infoWindow, clickPoint);      // 打开信息窗口
            lastInfoWin.push(infoWindow);
        },
        error: function (data) {
            var opts = {
                width: 300,
                height: 170,
                title: "查询结果"
            };
            var infoWindow = new BMap.InfoWindow("数据查询失败...", opts);  // 创建信息窗口对象
            map.openInfoWindow(infoWindow, clickPoint);      // 打开信息窗口
        }
    });
}

// 工程信息查询
function projSearch() {
    var opts = {
        width: 100,
        height: 30,
        title: ""
    };
    var infoWindow = new BMap.InfoWindow("正在查询，请稍等...", opts);  // 创建信息窗口对象
    map.openInfoWindow(infoWindow, clickPoint);      // 打开信息窗口

    $.ajax({
        type: "post",
        url: "getProjectInfo",
        dataType: "json",

        data: {
            'areaCode': areaCode,
            'gridId': gridId
        },
        success: function (data) {
            var winInfo;
            if (data) {
                var tableCont = new StringBuffer();
                tableCont.append("<div class='goBack' onclick='backTo();'>&nbsp; &nbsp; 返回</div>");
                tableCont.append("<div style='width: 550px; height:200px; margin-top:10px; font-size:0.9em; overflow: auto'>");
                tableCont.append("<table id='projInfo'>");
                tableCont.append("<tr class='bg'>");
                tableCont.append("<td>工程名称</td>");
                tableCont.append("<td>工程覆盖范围</td>");
                tableCont.append("<td>预计完工时间</td>");
                tableCont.append("</tr>");
                if (data.length > 0) {
                    for (var i = 0; i < data.length; i++) {
                        var row = data[i];
                        tableCont.append("<tr>");
                        tableCont.append("<td>" + row.split('||')[0] + "</td>");
                        tableCont.append("<td>" + row.split('||')[1] + "</td>");
                        tableCont.append("<td>" + row.split('||')[2] + "</td>");
                        tableCont.append("</tr>");
                    }
                } else {
                    tableCont.append("<tr>");
                    tableCont.append("<td></td>");
                    tableCont.append("<td></td>");
                    tableCont.append("<td></td>");
                    tableCont.append("</tr>");
                }
                winInfo = tableCont.toString();
            } else {
                var noResCont = new StringBuffer();
                noResCont += "<div class='goBack' onclick='backTo();'>&nbsp; &nbsp; 返回</div>";
                noResCont += "<p style='margin-top:20px;font-size:15px;color:#333;'>未查到该网格下的工程信息!</p>";
                winInfo = noResCont.toString();
            }
            var opts = {
                width: 550,
                height: 220,
                title: "工程信息"
            };
            var infoWindow = new BMap.InfoWindow(winInfo, opts);  // 创建信息窗口对象
            map.openInfoWindow(infoWindow, clickPoint);      // 打开信息窗口
            lastInfoWin.push(infoWindow);
        },
        error: function (data) {
            var opts = {
                width: 300,
                height: 170,
                title: "查询结果"
            };
            var infoWindow = new BMap.InfoWindow("数据查询失败...", opts);  // 创建信息窗口对象
            map.openInfoWindow(infoWindow, clickPoint);      // 打开信息窗口
        }
    });
}


// 提交
function sbmitAd() {
    gloabAddrName = document.getElementById("addrResAbil").value;
    var returnData;
    if (GetQueryString("fromsystem") == 'icss') {
        if (!gloabAddrId && !gloabAddrName) {
            $("#sbmitAd").css("background-color", "#bbb");
            $("#acSubmit").css("background-color", "#bbb");
            return false;
        }
        returnData = {
            'addrId': gloabAddrId,
            'addrName': gloabAddrName,
            'subAreaId': gloabOldSubAreaId,
            'subAreaName': gloabZySubAreaName,
            'isOpen': '0'
        };
    } else {
        returnData = {
            'addrId': gloabAddrId,
            'addrName': gloabAddrName,
            'subAreaId': gloabOldSubAreaId,
            'siteId': gloabSiteId.split(',')[0],
            'siteCode': gloabSiteCode.split(',')[0],
            'siteName': gloabSiteName.split(',')[0]
        };
        var par = parseParam(returnData);
        document.getElementById('myfarme').src = "http://ah.189.cn/netup/comm/broadresource/c.jsp?" + par;
    }

    window.returnValue = returnData;
    //window.opener.document.getElementById("WH_COMMON_CUSTOM_INFO/STAND_ADDR").value=returnData;
    window.close();
}

// 用于网厅，提交用户自行输入地址信息
function submitAd() {
    gloabAddrName = document.getElementById("userInputAd").value;
    if (gloabAddrName.length == 0) {
        alert("请输入详细地址信息");
        return;
    }
    var returnData = {
        'addrId': '',
        'addrName': gloabAddrName,
        'subAreaId': '',
        'siteId': '',
        'siteCode': '',
        'siteName': ''
    };
    var par = parseParam(returnData);
    document.getElementById('myfarme').src = "http://ah.189.cn/netup/comm/broadresource/c.jsp?" + par;
    window.returnValue = returnData;
    //window.opener.document.getElementById("WH_COMMON_CUSTOM_INFO/STAND_ADDR").value=returnData;
    window.close();
}

// 提交并受理
function sbmitAdIcss() {
    if (!gloabAddrId && !gloabAddrName) {
        $("#sbmitAd").css("background-color", "#aaa");
        $("#acSubmit").css("background-color", "#aaa");
        return false;
    }
    gloabAddrName = document.getElementById("addrResAbil").value;
    window.returnValue = {
        'addrId': gloabAddrId,
        'addrName': gloabAddrName,
        'subAreaId': gloabOldSubAreaId,
        'subAreaName': gloabZySubAreaName,
        'isOpen': '1'
    };
    //window.opener.document.getElementById("WH_COMMON_CUSTOM_INFO/STAND_ADDR").value=returnData;
    window.close();
}

// 清除搜索框内容
function cleanContent(obj) {
    if (obj.value.indexOf("搜索") >= 0) {
        obj.value = "";
    }
}

function userSbmAddr() {
    var opts = {
        width: 400,
        height: 130,
        title: ""
    };
    var msg = new StringBuffer();
    msg.append("<div class='goBack' onclick='backTo();'>&nbsp; &nbsp; 返回</div>");
    msg.append("<p style='margin:20px 0 5px 0;font-size: 0.9em;color: #222;'>请输入详细地址:</p>");
    msg.append("<input id='userInputAd' type='text' style='width: 300px; height: 30px;border:1px solid #aaa;border-right: 0;font-size:14px;color:#777;line-height:30px;vertical-align: middle;display:inline-block;float:left;' />");
    msg.append("<button onclick='submitAd();' style='display:inline-block;float:left;width:50px;height:32px;font-size:13px;background-color:#FF8200;color:#fff;'>提交</button>")
    msg.append("<p style='display: block;position: absolute;left: 0;bottom: 0;font-size: 12px;color: #444;'>* 请确认已输入完整准确的地址信息后提交</p>");
    var infoWindow = new BMap.InfoWindow(msg.toString(), opts);  // 创建信息窗口对象
    map.openInfoWindow(infoWindow, clickPoint);      // 打开信息窗口
    lastInfoWin.push(infoWindow);
}

function noDataWarn() {
    var opts = {
        width: 400,
        height: 100,
        title: ""
    };
    var msg = new StringBuffer();
    msg.append("<div class='goBack' onclick='backTo();'>&nbsp; &nbsp; 返回</div>");
    msg.append("<p style='margin-top:20px;font-size:15px;color:#333;'>&nbsp;&nbsp;&nbsp;&nbsp;所选地点未查询到相关地址信息，请重新选点尝试或使用其他选址方式</p>");
    if (GetQueryString("fromsystem") != 'icss' || !GetQueryString("fromsystem")) {
        msg.append("<div style='position:absolute; bottom: 0;'><span id='userAddPrompt' onclick='userSbmAddr();'>没找到您的地址？点此：您可以手动提交你的详细地址</span></div>");

    }
    var infoWindow = new BMap.InfoWindow(msg.toString(), opts);  // 创建信息窗口对象
    map.openInfoWindow(infoWindow, clickPoint);      // 打开信息窗口
    lastInfoWin.push(infoWindow);
}


String.prototype.trim = function () {
    return this.replace(/^\s\s*/, '').replace(/\s\s*$/, '');
};

window.polylines = [];
window.polyLoop = 0;
translateCallBack = function (data) {
    if (data.status === 0) {
        polylines[polyLoop++] = new BMap.Polyline(data.points, {
            strokeColor: "#FF8200",
            strokeWeight: 2,
            strokeOpacity: 1
        });
        map.addOverlay(polylines[polyLoop - 1]);
    }
};
function removePolygon() {
    for (var i = 0; i < polylines.length; ++i) {
        map.removeOverlay(polylines[i]);
    }
}

function parseParam(param, key) {
    var paramStr = "";
    if (param instanceof String || param instanceof Number || param instanceof Boolean) {
        paramStr += "&" + key + "=" + encodeURIComponent(param);
    } else {
        $.each(param, function (i) {
            var k = key == null ? i : key + (param instanceof Array ? "[" + i + "]" : "." + i);
            paramStr += '&' + parseParam(this, k);
        });
    }
    return paramStr.substr(1);
}


// 加载百度地图api
function loadScript() {
    var script = document.createElement("script");
    script.src = "http://api.map.baidu.com/api?v=2.0&ak=fo01QPSZFnAGNewh6Lqssuu1&callback=initialize";
    document.body.appendChild(script);
}
window.onload = loadScript;