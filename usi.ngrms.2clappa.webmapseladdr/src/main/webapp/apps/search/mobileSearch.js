/**
 * Created by limaple on 11/23/15.
 * 手机选址
 */

var currentCity = "";
var areaCode = "0";
// 获取资源能力接口
var resAbilityUrl =
    //"http://134.64.110.182:9999/service/mboss/route?wsdl";      // -- 测试
    "http://134.64.116.90:9102/service/mboss/route?wsdl";     // -- 生产

var gloabAddrId = '';
var gloabAddrName = '';
var gloabSiteId = '';
var gloabSiteCode = '';
var gloabSiteName = '';
var gloabOldSubAreaId = '';

var userAddrPageNum = 1;    // 用户地址页面当前页号

var map = new BMap.Map("allmap", {enableMapClick: false});
map.centerAndZoom("安徽", 7);
document.getElementById("gridInfo").innerHTML = "地图选址";

//对搜索框添加自动完成类
var ac = new BMap.Autocomplete(    //建立一个自动完成的对象
    {
        "input": "mapSearchInput",
        "location": "安徽省"
    });
var mapAutoComplete = ac;

ac.addEventListener("onconfirm", function (e) {    //鼠标点击下拉列表后的事件
    document.getElementById("mapSearchInput").blur();
    var _value = e.item.value;
    var myValue = _value.province + _value.city + _value.district + _value.street + _value.business;
    setPlace(myValue);
});

function setPlace(myValue) {
    map.clearOverlays();    //清除地图上所有覆盖物
    local.search(myValue);
}

//本地搜索
var local = new BMap.LocalSearch(map, {
    renderOptions: {
        map: map,
        autoViewport: false,
        selectFirstResult: false
    },
    pageCapacity: 5,
    onSearchComplete: function (results) {
        if (local.getStatus() == BMAP_STATUS_SUCCESS) {
            var pp = local.getResults().getPoi(0).point;    //获取第一个智能搜索的结果
            map.centerAndZoom(pp, 17);
        }
    }
});

function createMarker(point) {
    var marker = new BMap.Marker(point);
    marker.addEventListener("click", function (e) {
        e.domEvent.stopPropagation();
        queryGrid(point);
        document.getElementById("mapSearchInput").blur();
    });
    return marker;
}

local.setMarkersSetCallback(function (pois) {
    for (var i = pois.length; i--;) {
        var marker = pois[i].marker;
        map.removeOverlay(marker);
        var point = marker.getPosition();
        var relMarker = createMarker(point);
        map.addOverlay(relMarker, {enableClicking: true});
    }
});

//点击搜索按钮搜索
function searchMap() {
    map.clearOverlays();    //清除地图上所有覆盖物
    var target = currentCity + document.getElementById("mapSearchInput").value;
    local.search(target);
}

//添加缩放控件
var bottom_right_navigation = new BMap.NavigationControl({
    anchor: BMAP_ANCHOR_BOTTOM_RIGHT,
    type: BMAP_NAVIGATION_CONTROL_ZOOM
});
map.addControl(bottom_right_navigation);

// 添加定位控件
var geolocationControl = new BMap.GeolocationControl();
geolocationControl.addEventListener("locationSuccess", function (e) {
    map.centerAndZoom(e.point, 16);
});
map.addControl(geolocationControl);

// 地图类型控件
var mapTypeControl = new BMap.MapTypeControl({mapTypes: [BMAP_NORMAL_MAP, BMAP_SATELLITE_MAP], anchor: BMAP_ANCHOR_TOP_LEFT});
mapTypeControl.setOffset(new BMap.Size(10, 60));
map.addControl(mapTypeControl);

//获取并移动到当前城市
function myFun(result) {
    var cityName = result.name;
    if (cityName) {
        currentCity = cityName;
        mapAutoComplete.setLocation(cityName);
        document.getElementById("gridInfo").innerHTML = "当前城市: " + cityName;
    }
    map.centerAndZoom(cityName, 12);
}
var myCity = new BMap.LocalCity();
myCity.get(myFun);

//获取用户位置
function getUserLoc() {
    var geolocation = new BMap.Geolocation();
    geolocation.getCurrentPosition(function (r) {
        if (this.getStatus() == BMAP_STATUS_SUCCESS) {
            if (r.address.district || r.address.street) {
                var mk = new BMap.Marker(r.point);
                mk.addEventListener("click", function (e) {
                    e.domEvent.stopPropagation();
                    queryGrid(r.point);
                });
                map.addOverlay(mk);
                map.centerAndZoom(r.point, 16);
            }
        }
    }, {enableHighAccuracy: true});
}
window.onload = getUserLoc;

//监听用户操作事件
var lastClickPoi;
function clickFunc(e) {
    document.getElementById("mapSearchInput").blur();
    if (lastClickPoi) {
        map.removeOverlay(lastClickPoi);
    }
    var thisClickPoi = new BMap.Marker(e.point);
    map.addOverlay(thisClickPoi);
    lastClickPoi = thisClickPoi;
    queryGrid(e.point);
}
map.addEventListener("click", clickFunc);

map.addEventListener("touchend", function (e) {
    document.getElementById("mapSearchInput").blur();
});

function queryGrid(point) {
    removePolygon();
    document.getElementById("gridInfo").innerHTML = "正在查询, 请稍等 ...";
    document.getElementById("mask").style.display = "block";
    $.ajax({
        type: "post",
        url: "getMapAddrInfo",
        dataType: "json",
        beforeSend: function () {
        },
        data: {
            lng: point.lng,
            lat: point.lat
        },
        success: function (_data) {
            document.getElementById("mask").style.display = "none";
            if (_data == null) {
                document.getElementById("gridInfo").innerHTML = "所选地点未查到相关信息，请重新选点尝试";
                return;
            }
            var gridPolygon, gridType, gridId;
            window.data = [];
            window.gridName = '';
            window.grid68Name = '';
            $.each(_data, function (key, value) {
                var keyArray = key.split(":");
                gridType = keyArray[0];
                gridName = keyArray[1];
                grid68Name = keyArray[2];
                gridPolygon = keyArray[3];
                gridId = keyArray[4];
                areaCode = keyArray[5];
                data = value;
            });
            var cont = new StringBuffer();

            var emptyFlag = 1;
            for (var i = 0; i < data.length; ++i) {
                var address = data[i].nameBak;
                var re = new RegExp("<em>", "g");
                var re2 = new RegExp("</em>", "g");
                address = address.replace(re, "").replace(re2, '');
                var showAddress = address;
                if (address.length > 15 && address.split(" ").length > 1) {
                    showAddress = address.split(" ")[1];
                }
                var singleAddr = address.replace(/ /g, '');
                if (singleAddr == grid68Name || (gridType == '农村' && singleAddr.indexOf(grid68Name) >= 0)) {
                    var addressId = address + 0;
                    if (gridType == "城市" || areaCode == "555") {
                        emptyFlag = 0;
                        cont += "<span id='xqGrid' onclick='get9ofCity(" + i + ");' addId='" + addressId + "' title='" + address + "'>地址: " + showAddress + "<span id='detail'>详情 &gt;</span></span>";
                        break;
                    } else if (gridType == "农村") {
                        emptyFlag = 0;
                        cont += "<span id='xqGrid' onclick='get9ofCountry(\"" + address.split(" ")[0] + "\");' addId='" + addressId + "' title='" + showAddress + "'>地址: " + address.split(" ")[0] + "<span id='detail'>详情 &gt;</span></span>";
                        break;
                    }
                }
            }

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
            if (serviceTel == '暂无') {
                cont += ("<span id='mapTelPrompt'>装维咨询: " + serviceTel + "</span>");
            } else {
                cont += ("<span id='mapTelPrompt'>装维咨询: " + "<a style='color: #6bb1f7' href='tel: " + serviceTel + "'>" + serviceTel + "</a></span>");
            }

            if (emptyFlag == 1) {
                cont = "<p>所选地点未查到相关信息，请重新选点尝试</p>";
            }

            document.getElementById("gridInfo").innerHTML = cont.toString();

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
            document.getElementById("mask").style.display = "none";
            alert("查询失败");
        }
    });
}

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

// 获取九级地址列表
function get9ofCity(index) {
    document.getElementById("subAddr").style.display = "block";
    document.getElementById("userAddPrompt").style.display = "block";
    document.getElementById("subAddrContent").innerHTML = "";
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

    cont.append("<div id = 'citySearchPanel'><input id='city9LSearch' value='搜索: \"" + gridName + "\"下的地址信息' onclick='cleanContent(this)'  oninput='city9LSearch(\"" + xqName + "\", " + "this.value);' onpropertychange='city9LSearch(\"" + xqName + "\", " + "this.value);'/><button id='city9LSearchButton'>搜索</button></div>");
    cont.append("<div id='city9List'><ul id='buildingList'>");
    for (var i = 0; i < buildingArray.length; i++) {
        cont.append("<li onclick='getAddressBy9(\"" + xqName + "\", \"" + buildingArray[i] + "\")' title='" + buildingArray[i] + "'>" + buildingArray[i] + "</li>");
    }
    cont.append("</ul></div>");

    document.getElementById("subAddrContent").innerHTML = cont.toString();
    document.getElementById("citySearchPanel").style.height = screenHeight * 0.06 + "px";
}

//城市九级地址搜索
function city9LSearch(xqName, input) {
    var cont = new StringBuffer();
    cont.append("<ul id='buildingList'>");
    for (var i = 0; i < buildingArray.length; i++) {
        if (input == '' || buildingArray[i].indexOf(input) >= 0) {
            cont.append("<li onclick='getAddressBy9(\"" + xqName + "\", \"" + buildingArray[i] + "\")' title='" + buildingArray[i] + "'>" + buildingArray[i] + "</li>");
        }
    }
    cont.append("</ul>");
    document.getElementById("city9List").innerHTML = cont.toString();
}

// 由农村网格名称（六级地址）获取八级地址
function get9ofCountry(gridName) {
    document.getElementById("subAddr").style.display = "block";
    document.getElementById("userAddPrompt").style.display = "block";
    document.getElementById("subAddrContent").innerHTML = "正在查询,请稍等...";
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
            pageSize: 47
        },
        success: function (data) {
            if (data == null || data.length == 0) {
                document.getElementById("subAddrContent").innerHTML = "所选地点未查询到相关地址信息";
                return;
            }
            var cont = new StringBuffer();

            cont += "<div id = 'countrySearchPanel'><input id='country9LSearch' value='搜索: \"" + gridName + "\"下的地址信息' onclick='cleanContent(this)'  oninput='country9LSearch(\"" + gridName + "\", " + "this.value);' onpropertychange='country9LSearch(\"" + gridName + "\", " + "this.value);'/><button id='country9LSearchButton'>搜索</button></div>";
            cont += "<div id = 'country9List'><ul id='buildingList2'>";
            for (var i = 0; i < data.length; i++) {
                var address = data[i].nameBak;
                var re = new RegExp("<em>", "g");
                var re2 = new RegExp("</em>", "g");
                address = address.replace(re, "").replace(re2, '');
                if (address.replace(' ', '').indexOf(grid68Name) < 0) {
                    continue;
                }
                var addressId = address + i;
                cont += "<li onclick='getAddressBy9(\"" + address + "\", \" \")'; title=\"" + address.split(" ")[1] + "\">" + data[i].nameBak + "</li>";
            }
            cont += "</ul></div>";

            document.getElementById("subAddrContent").innerHTML = cont.toString();
            document.getElementById("countrySearchPanel").style.height = screenHeight * 0.06 + "px";
        },
        error: function () {
            alert("查询失败");
        }
    });
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
                document.getElementById("country9List").innerHTML = "<ul id='buildingList2><li>未找到相关地址</li><ul>";
            }
            else {
                var cont = new StringBuffer();
                cont += "<ul id='buildingList2'>";
                for (var i = 0; i < data.length; i++) {
                    var address = data[i].nameBak;
                    var re = new RegExp("<em>", "g");
                    var re2 = new RegExp("</em>", "g");
                    address = address.replace(re, "").replace(re2, '');
                    var addressId = address + i;
                    cont += "<li onclick='getAddressBy9(\"" + address + "\", " + "\" \"" + ")'; title=\"" + address.split(" ")[1] + "\">" + data[i].nameBak + "</li>";
                }
                cont += "</ul>";
                document.getElementById("country9List").innerHTML = cont.toString();
            }
        },
        error: function () {
            alert("查询失败");
        }
    });
}

// 由九级地址获取标准地址列表
function getAddressBy9(xqName, ldName) {
    document.getElementById("userAddr").style.display = "block";
    document.getElementById("subAddr").style.display = "none";
    document.getElementById("userAddrContent").innerHTML = "正在查询,请稍等...";
    $.ajax({
        url: "getUserAddrByInput",
        dataType: "json",
        type: "post",
        data: {
            areaCode: areaCode,
            input: xqName + ldName,
            pageNo: userAddrPageNum,
            pageSize: 50
        },
        success: function (data) {
            if (data == null || data.length == 0) {
                document.getElementById("userAddrContent").innerHTML = "所选地点未查询到相关地址信息";
                return;
            }

            data.sort(function(a, b) {
               if (a.name > b.name) {
                   return 1;
               } else if (a.name < b.name) {
                   return -1;
               } else {
                   return 0;
               }
            });

            var cont = new StringBuffer();
            var adList = new StringBuffer();
            var temp = xqName.split(" ");
            var promptName = temp[temp.length - 1];
            var count = 0;
            cont += "<div id = 'userAddSearchPanel'><input id='userAddrSearch' value='搜索: \"" + promptName + ldName + "\"下的地址信息' onclick='cleanContent(this)' oninput='userAddSearch(\"" + xqName + "\", \"" + ldName + "\", " + "this.value);' onpropertychange='userAddSearch(\"" + xqName + "\", \"" + ldName + "\", " + "this.value);'/><button id='userAddrSearchButton'>搜索</button></div>";
            cont += "<div id = 'userAdList'><ul id='userAddList'>";
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
                if (ldName == " " && xqName.split(" ").length > 1 && addName.indexOf(xqName.split(" ")[1]) < 0) {
                    continue;
                }

                var addShortName = addName.substring(addName.indexOf(gridName) + gridName.length);

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
                            cont += "<li onclick='getResAbility($(this).find(\"a\").attr(\"oldSubAreaId\"),$(this).find(\"a\").attr(\"zySubAreaName\"),$(this).find(\"a\").attr(\"addrId\"),$(this).find(\"a\").attr(\"siteId\"),$(this).find(\"a\").attr(\"siteName\"),$(this).find(\"a\").attr(\"siteCode\"),\"" + resAbilityUrl + "\", \"" + addName + "\")'><a oldSubAreaId='" + data[i].oldSubAreaId + "' addrId='" + data[i].id + "' siteId='" + allSiteIds + "' siteName='" + allSiteNames + "' siteCode='" + allSiteCodes + "' zySubAreaName='" + data[i].subAreaName + "' >" + addShortName + "</a></li>";
                        } else {
                            cont += "<li onclick='getResAbility($(this).find(\"a\").attr(\"oldSubAreaId\"),$(this).find(\"a\").attr(\"zySubAreaName\"),$(this).find(\"a\").attr(\"addrId\"),$(this).find(\"a\").attr(\"siteId\"),$(this).find(\"a\").attr(\"siteName\"),$(this).find(\"a\").attr(\"siteCode\"),\"" + resAbilityUrl + "\", \"" + addName + "\")'><a oldSubAreaId='" + data[i].oldSubAreaId + "' addrId='" + data[i].id + "' siteId='" + allSiteIds + "' siteName='" + allSiteNames + "' siteCode='" + allSiteCodes + "' zySubAreaName=''>" + addShortName + "</a></li>";
                        }

                    }
                } else {
                    cont += "<li onclick='getResAbility($(this).find(\"a\").attr(\"oldSubAreaId\"),$(this).find(\"a\").attr(\"zySubAreaName\"),$(this).find(\"a\").attr(\"addrId\"),$(this).find(\"a\").attr(\"siteId\"),$(this).find(\"a\").attr(\"siteName\"),$(this).find(\"a\").attr(\"siteCode\"),\"" + resAbilityUrl + "\", \"" + addName + "\")'><a addrId='" + data[i].id + "' siteId='' siteCode='' siteName=''" + " oldSubAreaId='' zySubAreaName=''>" + addShortName + "</a></li>";
                }
                ++count;
            }
            if (userAddrPageNum == 1 && data.length == 50 && count > 49) {
                cont += "<li onclick='nextPage(\"" + xqName + "\", \"" + ldName + "\")' style='color: #6bb1f7; text-align: right'>下一页</li>";
            } else if (userAddrPageNum == 1) {
                ;
            } else if (userAddrPageNum < 7 && data.length == 50 && count > 49) {
                cont += "<li onclick='previousPage(\"" + xqName + "\", \"" + ldName + "\")' style='color: #6bb1f7; text-align: right'>上一页</li>";
                cont += "<li onclick='nextPage(\"" + xqName + "\", \"" + ldName + "\")' style='color: #6bb1f7; text-align: right'>下一页</li>";
            } else {
                cont += "<li onclick='previousPage(\"" + xqName + "\", \"" + ldName + "\")' style='color: #6bb1f7; text-align: right'>上一页</li>";
                cont += "<li style='color: #6bb1f7; font-size: 0.9em'>未找到您的地址? 请尝试页面顶部搜索功能</li>";
            }
            cont += "</ul></div>";
            if (count == 0) {
                var cont1 = new StringBuffer();
                cont1.append("<table id='resAbility' style='margin-top: 20px;'>" +
                    "<tr><td class = 'rowName'>接入方式</td><td> </td></tr>" +
                    "<tr><td class = 'rowName'>资源能力</td><td>不具备</td></tr>" +
                    "<tr><td class = 'rowName'>理论带宽</td><td> </td></tr>" +
                    "<tr><td class = 'rowName'>适用终端类型</td><td> </td></tr></table>");

                cont1.append("<p class='mapAbiPrompt'>温馨提示：因线路/端口有差异，本查询结果仅供参考，具体以安装人员上门核实结果为准。</p>");
                document.getElementById("userAddrContent").innerHTML = cont1.toString();
                return;
            }

            document.getElementById("userAddrContent").innerHTML = cont.toString();
            document.getElementById("userAddSearchPanel").style.height = screenHeight * 0.06 + "px";
        },
        error: function () {
            document.getElementById("userAddrContent").innerHTML = "数据查询失败...";
        }
    });
}

// 用户地址翻上一页
function previousPage(xqName, ldName) {
    --userAddrPageNum;
    getAddressBy9(xqName, ldName);
}
// 用户地址翻下一页
function nextPage(xqName, ldName) {
    ++userAddrPageNum;
    getAddressBy9(xqName, ldName);
}

// 用户地址搜索
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
                document.getElementById("userAdList").innerHTML = "<ul id='userAddList><li>未找到相关地址</li><ul>";
                return;
            }
            var cont = new StringBuffer();
            cont += "<ul id='userAddList'>";
            for (var i = 0; i < data.length; i++) {
                var addName = data[i].highlightText;
                var re = new RegExp("<em>", "g");
                var re2 = new RegExp("</em>", "g");
                var re3 = new RegExp(" ", "g");
                addName = addName.replace(re, "").replace(re2, "").replace(re3, "");
                if (addName.indexOf(gridName) < 0) {
                    continue;
                }

                var addShortName = addName.substring(addName.indexOf(gridName) + gridName.length);

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
                            cont += "<li onclick='getResAbility($(this).find(\"a\").attr(\"oldSubAreaId\"),$(this).find(\"a\").attr(\"zySubAreaName\"),$(this).find(\"a\").attr(\"addrId\"),$(this).find(\"a\").attr(\"siteId\"),$(this).find(\"a\").attr(\"siteName\"),$(this).find(\"a\").attr(\"siteCode\"),\"" + resAbilityUrl + "\", \"" + addName + "\")'><a oldSubAreaId='" + data[i].oldSubAreaId + "' addrId='" + data[i].id + "' siteId='" + allSiteIds + "' siteName='" + allSiteNames + "' siteCode='" + allSiteCodes + "' zySubAreaName='" + data[i].subAreaName + "' >" + addShortName + "</a></li>";
                        } else {
                            cont += "<li onclick='getResAbility($(this).find(\"a\").attr(\"oldSubAreaId\"),$(this).find(\"a\").attr(\"zySubAreaName\"),$(this).find(\"a\").attr(\"addrId\"),$(this).find(\"a\").attr(\"siteId\"),$(this).find(\"a\").attr(\"siteName\"),$(this).find(\"a\").attr(\"siteCode\"),\"" + resAbilityUrl + "\", \"" + addName + "\")'><a oldSubAreaId='" + data[i].oldSubAreaId + "' addrId='" + data[i].id + "' siteId='" + allSiteIds + "' siteName='" + allSiteNames + "' siteCode='" + allSiteCodes + "' zySubAreaName=''>" + addShortName + "</a></li>";
                        }

                    }
                } else {
                    cont += "<li onclick='getResAbility($(this).find(\"a\").attr(\"oldSubAreaId\"),$(this).find(\"a\").attr(\"zySubAreaName\"),$(this).find(\"a\").attr(\"addrId\"),$(this).find(\"a\").attr(\"siteId\"),$(this).find(\"a\").attr(\"siteName\"),$(this).find(\"a\").attr(\"siteCode\"),\"" + resAbilityUrl + "\", \"" + addName + "\")'><a addrId='" + data[i].id + "' siteId='' siteCode='' siteName=''" + " oldSubAreaId='' zySubAreaName=''>" + addShortName + "</a></li>";
                }
            }
            cont += "</ul>";

            document.getElementById("userAdList").innerHTML = cont.toString();
        },
        error: function () {
            alert("查询失败");
        }
    });
}

// 查找资源能力
function getResAbility(oldSubAreaId, zySubAreaName, addrId, siteIds, siteNames, siteCodes, oipUrl, addrName) {
    document.getElementById("abilityContent").innerHTML = "正在查询,请稍等...";
    document.getElementById("ability").style.display = "block";
    document.getElementById("userAddr").style.display = "none";


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
                var userAddrShowName = gloabAddrName;
                if (gridName && gloabAddrName.indexOf(gridName) > 0) {
                    userAddrShowName = gloabAddrName.substr(gloabAddrName.indexOf(gridName) + gridName.length);
                }
                tableCont.append("<div id='resAbilityPanel'><input id='addrResAbil' value='" + userAddrShowName + "' readonly />");
                //tableCont.append("<button id='sbmitAd' onclick='submitAd();'>提交</button><div>");
                tableCont.append("<table id='resAbility'>");
                if (data.length > 0) {
                    for (var i = 0; i < data.length; i++) {
                        var row = data[i];
                        if (row.productName.indexOf('宽带') < 0) {
                            continue;
                        }
                        tableCont.append("<tr>");
                        tableCont.append("<td class = 'rowName'>接入方式</td>");
                        if (row.terminalPortType == '1') {
                            tableCont.append("<td>ADSL</td>");
                        } else if (row.terminalPortType == '2') {
                            tableCont.append("<td>LAN</td>")
                        } else if (row.terminalPortType == '3') {
                            tableCont.append("<td>FTTH</td>");
                        } else if (row.terminalPortType == '4') {
                            tableCont.append("<td>铜缆</td>");
                        } else {
                            tableCont.append("<td> </td>");
                        }
                        tableCont.append("</tr>");
                        //资源能力
                        tableCont.append("<tr>");
                        tableCont.append("<td class = 'rowName'>资源能力</td>");
                        if (row.ability == '0') {
                            tableCont.append("<td>具备</td>");
                        } else if (row.ability == '1') {
                            tableCont.append("<td>不具备</td>");
                        } else {
                            tableCont.append("<td>&nbsp;</td>");
                        }
                        tableCont.append("</tr>");
                        //理论下行速率
                        tableCont.append("<tr>");
                        tableCont.append("<td class = 'rowName'>理论带宽</td>");
                        if (row.logicRate) {
                            tableCont.append("<td>" + row.logicRate + "</td>");
                        } else {
                            tableCont.append("<td>&nbsp;</td>");
                        }
                        tableCont.append("</tr>");
                        //G/EPON属性
                        tableCont.append("<tr>");
                        tableCont.append("<td class = 'rowName'>适用终端类型</td>");
                        if (row.ponType == '1') {
                            tableCont.append("<td>EPON</td>");
                        } else if (row.ponType == 2) {
                            tableCont.append("<td>GPON</td>");
                        } else {
                            tableCont.append("<td>&nbsp;</td>");
                        }
                        tableCont.append("</tr>");
                    }
                } else {
                    tableCont.append("<tr>");
                    tableCont.append("<td class = 'rowName'>接入方式</td>");
                    tableCont.append("<td> </td>");
                    tableCont.append("</tr>");
                    //资源能力
                    tableCont.append("<tr>");
                    tableCont.append("<td class = 'rowName'>资源能力</td>");
                    tableCont.append("<td> </td>");
                    tableCont.append("</tr>");
                    //理论下行速率
                    tableCont.append("<tr>");
                    tableCont.append("<td class = 'rowName'>理论带宽</td>");
                    tableCont.append("<td> </td>");
                    tableCont.append("</tr>");
                    //G/EPON属性
                    tableCont.append("<tr>");
                    tableCont.append("<td class = 'rowName'>适用终端类型</td>");
                    tableCont.append("<td> </td>");
                    tableCont.append("</tr>");
                }
                tableCont.append("</table>");
                tableCont.append("<p class='mapAbiPrompt'>温馨提示：因线路/端口有差异，本查询结果仅供参考，具体以安装人员上门核实结果为准。</p>");
                winInfo = tableCont.toString();
            } else {
                var noResCont = new StringBuffer();
                noResCont += "<p style='margin-top:20px;font-size:15px;color:#333;'>未查到地址对应宽带办理能力信息!</p>";
                winInfo = noResCont.toString();
            }
            document.getElementById("abilityContent").innerHTML = winInfo;
            document.getElementById("resAbilityPanel").style.height = screenHeight * 0.06 + "px";

        },
        error: function (data) {
            document.getElementById("abilityContent").innerHTML = "数据查询失败...";
        }
    });
}

function userSbmAddr() {
    document.getElementById("userCommit").style.display = "block";
    var msg = new StringBuffer();
    msg.append("<p style='margin:7% 0 3% 0;font-size: 0.9em;color: #222;'>请输入详细地址:</p>");
    msg.append("<div id='userCommitPanel'>");
    msg.append("<input id='userInputAd' type='text' />");
    msg.append("<button id='userCommitButton' onclick='userSubmitAd();'>提交</button>");
    msg.append("</div>");
    msg.append("<p style='display: block;position: absolute;left: 12%;bottom: 1%;font-size: 0.8em;color: #777;z-index: 9;text-align: center;'>* 请确认已输入完整准确的地址信息后提交</p>");
    document.getElementById("userCommitContent").innerHTML = msg.toString();
    document.getElementById("userAddPrompt").style.display = "none";
    document.getElementById("userCommitPanel").style.height = screenHeight * 0.06 + "px";
}

// 提交用户自行输入地址信息
function userSubmitAd() {
    gloabUserCommitName = document.getElementById("userInputAd").value;
    if (gloabUserCommitName.length == 0) {
        alert("请输入详细地址信息");
        return;
    }
    var returnData = {
        'addrId': '',
        'addrName': gloabUserCommitName,
        'subAreaId': '',
        'siteId': '',
        'siteCode': '',
        'siteName': ''
    };

    /*tmp = '';
    for (var tm in returnData) {
        tmp += tm + ': ';
        tmp += returnData[tm] + '\n';
    }
    alert(tmp);*/

    window.returnValue = returnData;
    //window.opener.document.getElementById("WH_COMMON_CUSTOM_INFO/STAND_ADDR").value=returnData;
    window.close();
}

// 提交
function submitAd() {
    //gloabAddrName = document.getElementById("addrResAbil").value;
    var returnData;
    returnData = {
        'addrId': gloabAddrId,
        'addrName': gloabAddrName,
        'subAreaId': gloabOldSubAreaId,
        'siteId': gloabSiteId.split(',')[0],
        'siteCode': gloabSiteCode.split(',')[0],
        'siteName': gloabSiteName.split(',')[0]
    };

    /*tmp = '';
    for (var tm in returnData) {
        tmp += tm + ': ';
        tmp += returnData[tm] + '\n';
    }
    alert(tmp);*/


    window.returnValue = returnData;
    window.close();
}

/* 返回 */
function goback1() {
    document.getElementById("subAddr").style.display = "none";
    document.getElementById("userAddPrompt").style.display = "none";
}
function goback2() {
    userAddrPageNum = 1;
    document.getElementById("userAddr").style.display = "none";
    document.getElementById("subAddr").style.display = "block";
}
function goback3() {
    document.getElementById("ability").style.display = "none";
    document.getElementById("userAddr").style.display = "block";
}
function goback4() {
    document.getElementById("userCommit").style.display = "none";
    document.getElementById("userAddPrompt").style.display = "block";
}

/** String trim **/
String.prototype.trim = function () {
    return this.replace(/^\s\s*/, '').replace(/\s\s*$/, '');
};

/** 拼接字符串 **/
function StringBuffer() {
    this._string_ = [];
}
StringBuffer.prototype.append = function (str) {
    this._string_.push(str);
};
StringBuffer.prototype.toString = function () {
    return this._string_.join('');
};

// 清除搜索框内容
function cleanContent(obj) {
    if (obj.value.indexOf("搜索") >= 0) {
        obj.value = "";
    }
}