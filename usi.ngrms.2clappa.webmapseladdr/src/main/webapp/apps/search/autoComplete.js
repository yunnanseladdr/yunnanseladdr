/*** ajax避免重复提交 ***/
;
(function ($) {
    var f = $.ajax;
    f._reuqestsCache = {};
    $.ajaxSetup({
        mode: "block", index: 0, cache: false, beforeSend: function (a, s) {
            if (s.mode) {
                if (s.mode === "abort" && s.index) {
                    if (f._reuqestsCache[s.index]) {
                        f._reuqestsCache[s.index].abort()
                    }
                }
                f._reuqestsCache[s.index] = a
            }
        }
    });
    $.extend({
        getAjax: function (options) {
            var defaultOption = {type: "post", cache: false, mode: 'abort', index: "111111111"};
            var settingOption = $.extend(true, {}, defaultOption, options);
            if (settingOption.mode === "block" && settingOption.index) {
                if (f._reuqestsCache[settingOption.index]) {
                    return false
                }
                f._reuqestsCache[settingOption.index] = true
            }
            return $.ajax(settingOption)
        }
    });
    $(document).ajaxComplete(function (a, b, c) {
        if (c.index)f._reuqestsCache[c.index] = null
    });
})(jQuery);
(function ($) {
    var errorMsg = "对不起，没有查询到相关地址信息，请尝试在关键词之间加空格隔开";
    var subKeyArray = {};  //用于存放9级地址
    var zyAddress = $("#zyAddress"); //地址文本框
    var showZy = true; //是否查地址所在资源
    var timeout = 300; //keyup 触发间隔时间
    var addressObj; //资源查询时地址ID 局站ID等变量存储
    var areaCode = '';
    var zySubArea = '';
    var realSubArea = '';
    var zyStreetOrTown = '';
    var str8 = '';
    var str9 = '';
    var gloabAddrId = '';
    var gloabAddrName = '';
    var gloabSiteId = '';
    var gloabSiteCode = '';
    var gloabSiteName = '';
    var gloabOldSubAreaId = '';
    var gloabZySubAreaName = '';
    var addressPageNo = '';
    var totalPage = '';
    var pageNo = 1;
    var pageSize = 10;
    //调用地址接口：1；调用本系统：0
    var selaSystem = 0;
    var addrOip = 1;
    var clickOrNot = false;

    var addrOipUrl = "http://134.64.116.90:9102/service/mboss/route?wsdl";

    var code2areaId = {
        '550': 11,
        '551': 2,
        '552': 3,
        '553': 4,
        '554': 5,
        '555': 6,
        '556': 7,
        '557': 8,
        '558': 9,
        '559': 10,
        '561': 12,
        '562': 13,
        '563': 14,
        '564': 15,
        '566': 17,
        '567': 18
    };
    //自动补全类
    var AutoComplete = new function () {
        this.keyDiv = $("#keyDiv");
        this.addressDiv = $("#addressDiv");
        this.functionalKeyArray = [9, 20, 17, 18, 91, 92, 93, 45, 36, 33, 34, 35, 37,
            39, 112, 113, 114, 115, 116, 117, 118, 119, 120, 121, 122, 123, 144, 19, 145, 40, 38, 27];
        //初始化方法
        this.init = function () {
            $("#submitAddr").bind('click', function () {
                var returnData;
                if (GetQueryString("fromsystem") == 'icss') {
                    if (!gloabAddrId && !gloabAddrName) {
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
            });
            $("#submitIcss").bind('click', function () {
                if (!gloabAddrId && !gloabAddrName) {
                    return false;
                }
                var returnData = {
                    'addrId': gloabAddrId,
                    'addrName': gloabAddrName,
                    'subAreaId': gloabOldSubAreaId,
                    'subAreaName': gloabZySubAreaName,
                    'isOpen': '1'
                };
                window.returnValue = returnData;
                //window.opener.document.getElementById("WH_COMMON_CUSTOM_INFO/STAND_ADDR").value=returnData;
                window.close();
            });

            $("#projSearch2").bind('click', function() {
                areaCode = $("#zy_city").val();
                $.ajax({
                    type: "post",
                    url: "getProjectInfo2",
                    dataType: "json",

                    data: {
                        areaId: code2areaId[areaCode],
                        addrId: gloabAddrId
                    },
                    success: function (data) {
                        var tableCont = new StringBuffer();
                        if (data) {
                            tableCont.append("<tr class=\"bg\">");
                            tableCont.append("<td style='width: 70%'>工程名称</td>");
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
                        } else {
                            tableCont.append("未查到相应工程信息");
                        }

                        $("#abilityTable").html(tableCont.toString());
                    },
                    error: function (data) {
                        $("#abilityTable").html("工程信息查询失败");
                    }
                });
            });
            /**
             * 8级分词下拉框绑定点击事件
             */
                //鼠标悬停时选中当前行
            $("#keyList").delegate("li", "mouseover", function () {
                $("#keyList li").removeClass("ct");
                $(this).addClass("ct");
                var addId = $(this).attr('addId');
                var title = $(this).attr('title');
                AutoComplete.show9(addId, title);
            }).delegate("li", "mouseout", function () {
                $("#keyList tr").removeClass("ct");
            });
            //单击选中行后，选中行内容设置到输入框中，并根据8级分词查询地址
            $("#keyList").delegate("li", "click", function () {
                str8 = $(this).attr('title');
                zyAddress.val(str8);
                AutoComplete.hideAutocomplete(AutoComplete.keyDiv);
                $("#loadi").show();
                AutoComplete.getAddressBy8();
            });
            //IE下的改变区域下拉框值时option事件(区域)
            $("#zy_city").change(function () {
                //清除子区域、街道和提示分词等样式
                AutoComplete.cleanHide89AndAddrData();
                AutoComplete.clearStreetOrTownData();
                var evt = window.event;
                var selectObj = evt ? evt.srcElement : null;
                // IE Only
                if (evt && selectObj && evt.offsetY && evt.button != 2
                    && (evt.offsetY > selectObj.offsetHeight || evt.offsetY < 0 )) {

                    // 记录原先的选中项
                    var oldIdx = selectObj.selectedIndex;

                    setTimeout(function () {
                        var option = selectObj.options[selectObj.selectedIndex];
                        // 此时可以通过判断 oldIdx 是否等于 selectObj.selectedIndex
                        // 来判断用户是不是点击了同一个选项,进而做不同的处理.
                        AutoComplete.cleanHide89AndAddrData();
                        AutoComplete.clearStreetOrTownData();

                    }, 60);
                }
            });
            //切换子区域时，查询该子区域下的乡镇、街道信息
            $("#zy_qu").change(function () {
                //$("#zyAddress").val('关键字搜索参考：请输入小区，村庄或周边标志性建筑名，多个关键字之间以空格隔开');
                //清空提示词和地址中的值并且隐藏div
                AutoComplete.cleanHide89AndAddrData();
                AutoComplete.clearStreetOrTownData();
                areaCode = $("#zy_city").val();
                zySubArea = $("#zy_qu").val().split('|')[0];
                if ('0' == zySubArea) {
                    return;
                }
                AutoComplete.getStreetTown(areaCode, zySubArea);
            });

            //IE下的单击option事件(子区域)
            $("#zy_qu").change(function () {
                var evt = window.event;
                var selectObj = evt ? evt.srcElement : null;
                // IE Only
                if (evt && selectObj && evt.offsetY && evt.button != 2
                    && (evt.offsetY > selectObj.offsetHeight || evt.offsetY < 0 )) {

                    // 记录原先的选中项
                    var oldIdx = selectObj.selectedIndex;
                    setTimeout(function () {
                        var option = selectObj.options[selectObj.selectedIndex];
                        // 此时可以通过判断 oldIdx 是否等于 selectObj.selectedIndex
                        // 来判断用户是不是点击了同一个选项,进而做不同的处理.
                        //$("#zyAddress").val('关键字搜索参考：请输入小区，村庄或周边标志性建筑名，多个关键字之间以空格隔开');

                        //清空提示词和地址中的值并且隐藏div
                        AutoComplete.cleanHide89AndAddrData();
                        AutoComplete.clearStreetOrTownData();
                        areaCode = $("#zy_city").val();
                        zySubArea = option.value.split('|')[0];
                        if ('0' == zySubArea) {
                            return;
                        }
                        AutoComplete.getStreetTown(areaCode, zySubArea);
                    }, 60);
                }
            });
            //切换乡镇或街道时，查询该乡镇或这个街道下的8级分词信息
            $("#zy_jd").delegate("option", "click", function () {
                //$("#zyAddress").val('关键字搜索参考：请输入小区，村庄或周边标志性建筑名，多个关键字之间以空格隔开');
                //清空提示词和地址中的值并且隐藏div
                AutoComplete.cleanHide89AndAddrData();
                areaCode = $("#zy_city").val();
                zySubArea = $("#zy_qu").val().split('|')[0];
                realSubArea = $("#zy_qu").val().split('|')[2];
                zyStreetOrTown = $("#zy_jd").val();
                if ('' == zyStreetOrTown) {
                    return;
                }

                if (areaCode == '0' || areaCode == '') {
                    alert('请选择区域！');
                    return false;
                }
                if (zySubArea == '' || zySubArea == '0') {
                    alert('请选择子区域！');
                    return false;
                }
                if (realSubArea == '' || realSubArea == '0') {
                    alert('请选择子区域！');
                    return false;
                }
                AutoComplete.get89addrByStr(areaCode, zySubArea, realSubArea, zyStreetOrTown, pageNo, pageSize);

            });
            //IE下的单击option事件(街道或乡镇信息)
            $("#zy_jd").change(function () {
                var evt = window.event;
                var selectObj = evt ? evt.srcElement : null;
                // IE Only
                if (evt && selectObj && evt.offsetY && evt.button != 2
                    && (evt.offsetY > selectObj.offsetHeight || evt.offsetY < 0 )) {

                    // 记录原先的选中项
                    var oldIdx = selectObj.selectedIndex;

                    setTimeout(function () {
                        var option = selectObj.options[selectObj.selectedIndex];
                        // 此时可以通过判断 oldIdx 是否等于 selectObj.selectedIndex
                        // 来判断用户是不是点击了同一个选项,进而做不同的处理.
                        //清空提示词和地址中的值并且隐藏div
                        AutoComplete.cleanHide89AndAddrData();
                        areaCode = $("#zy_city").val();
                        zySubArea = $("#zy_qu").val().split('|')[0];
                        realSubArea = $("#zy_qu").val().split('|')[2];
                        zyStreetOrTown = option.value;
                        if ('' == zyStreetOrTown) {
                            return;
                        }
                        if (areaCode == '0' || areaCode == '') {
                            alert('请选择区域！');
                            return false;
                        }
                        if (zySubArea == '' || zySubArea == '0') {
                            alert('请选择子区域！');
                            return false;
                        }
                        AutoComplete.get89addrByStr(areaCode, zySubArea, realSubArea, zyStreetOrTown, pageNo, pageSize);
                    }, 60);
                }
            });
            /**
             * 地址下拉框绑定点击事件
             */
                //单击选中行后，选中行内容设置到输入框中，并执行callback函数
            $("#addressDiv ul").delegate("li", "click", function () {
                var add1 = $(this).find('a').text();
                zyAddress.val(add1/*.replaceAll('</em>','').replaceAll('<em>','')*/);
                //AutoComplete.hideAutocomplete(AutoComplete.addressDiv);
                if (showZy) {
                    //console.log('addrId:'+$(this).find('a').attr('addrId'));
                    //console.log('siteId:'+$(this).find('a').attr('siteId'));
                    gloabSiteId = $(this).find('a').attr('siteId');
                    gloabSiteName = $(this).find('a').attr('siteName');
                    gloabSiteCode = $(this).find('a').attr('siteCode');
                    gloabAddrId = $(this).find('a').attr('addrId');
                    gloabAddrName = $(this).find('a').text().replace(/<em>/g, '').replace(/<\/em>/g, '');
                    gloabOldSubAreaId = $(this).find('a').attr('oldSubAreaId');
                    gloabZySubAreaName = $(this).find('a').attr('zySubAreaName');
                    //测试
                    //AutoComplete.getResAbility($("#zy_city").val(),$(this).find('a').attr('addrId'),siteIds,siteNames,"http://134.64.110.182:9999/service/mboss/route?wsdl");
                    //生产
                    AutoComplete.getResAbility($("#zy_city").val(), $(this).find('a').attr('addrId'), gloabSiteId, gloabSiteName, "http://134.64.116.90:9102/service/mboss/route?wsdl");
                    //AutoComplete.getAbility($("#zy_city").val(),$(this).find('a').attr('addrId'),$(this).find('a').attr('siteId'));
                }
            });
            /**
             *  9级分词绑定点击事件
             */
            $("#subKeyList").delegate("a", "click", function () {
                var faddress = $(this).attr('faddress');
                var saddress = $(this).html();
                str8 = faddress;
                str9 = saddress;
                zyAddress.val(faddress + " " + saddress);
                AutoComplete.hideAutocomplete(AutoComplete.keyDiv);
                $("#loadi").show();
                AutoComplete.getAddressBy9(faddress, saddress);
            });
            /**
             * 点击上一页
             */
            $("#lastPage").click(function () {
                pageNo = $('#currentPageNo').val();
                if (pageNo == 1) {
                    alert('已是第一页！');
                    return;
                }
                pageNo--;
                areaCode = $("#zy_city").val();
                zySubArea = $("#zy_qu").val().split('|')[0];
                realSubArea = $("#zy_qu").val().split('|')[2];
                if (zySubArea == '0') {
                    zySubArea = '';
                    realSubArea = '';
                }

                zyStreetOrTown = $("#zy_jd").val();
                var address = $("#zyAddress").val();
                var keyArray = [];
                keyArray = address.split(' ');
                //用户输入的关键词的个数
                var count = 0;
                for (var str in keyArray) {
                    if ('' != keyArray[str] && keyArray[str]) {
                        count++;
                    }
                }
                AutoComplete.beforeSend();
                if ($('#isSearchAddress').attr("checked") == 'checked') {
                    AutoComplete.getUserAddrByGolbalSear(areaCode, address, pageNo, pageSize);
                } else {
                    AutoComplete.getUserAddrByInput(areaCode, zySubArea, realSubArea, zyStreetOrTown, address, pageNo, pageSize);
                }
                $('#currentPageNo').val(pageNo);
            });
            /**
             * 点击下一页
             */
            $("#nextPage").click(function () {
                pageNo = $('#currentPageNo').val();
                if (pageNo >= 10) {
                    alert('对不起，查询范围过大，请输入更多关键词！');
                    return;
                }
                pageNo++;
                areaCode = $("#zy_city").val();
                zySubArea = $("#zy_qu").val().split('|')[0];
                realSubArea = $("#zy_qu").val().split('|')[2];
                if (zySubArea == '0') {
                    zySubArea = '';
                    realSubArea = '';
                }
                zyStreetOrTown = $("#zy_jd").val();
                var address = $("#zyAddress").val();
                var keyArray = [];
                keyArray = address.split(' ');
                //用户输入的关键词的个数
                var count = 0;
                for (var str in keyArray) {
                    if ('' != keyArray[str] && keyArray[str]) {
                        count++;
                    }
                }
                //var addressCodePattern = address;
                var addressNamePattern = '', addressCodePattern = '';
                AutoComplete.beforeSend();
                if ($('#isSearchAddress').attr("checked") == 'checked') {
                    AutoComplete.getUserAddrByGolbalSear(areaCode, address, pageNo, pageSize);
                } else {
                    AutoComplete.getUserAddrByInput(areaCode, zySubArea, realSubArea, zyStreetOrTown, address, pageNo, pageSize);
                }
                $('#currentPageNo').val(pageNo);
            });

            //点击查询/提交按钮
            $('#searchButton').click(function () {
                if (GetQueryString("fromsystem") != 'icss' || !GetQueryString("fromsystem")) {
                    gloabAddrName = document.getElementById("zyAddress").value.replace(/<em>/g, '').replace(/<\/em>/g, '');
                    if (gloabAddrName.length == 0 || gloabAddrName == "关键字搜索参考：请输入小区，村庄或周边标志性建筑名，多个关键字之间以空格隔开") {
                        alert("请输入详细地址信息");
                        return;
                    }
                    var returnData = {
                        'addrId': gloabAddrId,
                        'addrName': gloabAddrName,
                        'subAreaId': gloabOldSubAreaId,
                        'siteId': gloabSiteId.split(',')[0],
                        'siteCode': gloabSiteCode.split(',')[0],
                        'siteName': gloabSiteName.split(',')[0]
                    };
                    var par = parseParam(returnData);
                    document.getElementById('myfarme').src = "http://ah.189.cn/netup/comm/broadresource/c.jsp?" + par;

                    window.returnValue = returnData;
                    //window.opener.document.getElementById("WH_COMMON_CUSTOM_INFO/STAND_ADDR").value=returnData;
                    window.close();
                } else {
                    //记录调用地址查询接口的次数
                    if (!clickOrNot) {
                        $.ajax({
                            type: "post",
                            url: "SetOipCount",
                            dataType: "json",
                            beforeSend: function () {

                            },
                            data: {},
                            success: function (data) {

                            },
                            error: function (data) {

                            }
                        });
                        clickOrNot = true;
                    }
                    areaCode = $("#zy_city").val();
                    zySubArea = $("#zy_qu").val().split('|')[0];
                    if (zySubArea == '0') {
                        zySubArea = '';
                        realSubArea = '';
                    }
                    zyStreetOrTown = $("#zy_jd").val();
                    var address = $("#zyAddress").val();
                    if (address == '关键字搜索参考：请输入小区，村庄或周边标志性建筑名，多个关键字之间以空格隔开') {
                        address = '';
                    }
                    AutoComplete.getaddressByOIP(areaCode, zySubArea, zyStreetOrTown, address, pageNo, pageSize);
                    /*if(!gloabAddrId && !gloabAddrName){
                     return false;
                     }
                     var returnData = {'addrId':gloabAddrId,'addrName':gloabAddrName, 'subAreaId':gloabOldSubAreaId,'subAreaName':gloabZySubAreaName,'isOpen':'0'};
                     window.returnValue = returnData;
                     //window.opener.document.getElementById("WH_COMMON_CUSTOM_INFO/STAND_ADDR").value=returnData;
                     window.close();*/
                }
            });

            //临近号码查询
            $("#lj_city").change(function () {
                document.getElementById("zy_city").selectedIndex = document.getElementById("lj_city").selectedIndex;
            });
            $("#searchArInput").click(function () {
                if (document.getElementById("lj_city").selectedIndex == 0) {
                    $("#searchArInput").val("请在选择区域后输入号码");

                } else if ($("#searchArInput").val() == "请在选择区域后输入号码" || $("#searchArInput").val() == "临近号码查询") {
                    $("#searchArInput").val("");
                }
            });
            $("#searchArButton").click(function () {
                $("#addressDiv").hide();
                $("#subKeyDiv").hide();
                $("#keyDiv").hide();
                $("#queryResult").hide();
                $("#noResultDiv").hide();
                $("#aroundAddr").hide();
                var cityIndex = document.getElementById("lj_city").selectedIndex;
                if (cityIndex == 0 || $("#searchArInput").val() == "" || $("#searchArInput").val() == "请在选择区域后输入号码" || $("#searchArInput").val() == "临近号码查询") {
                    $("#searchArInput").val("请在选择区域后输入号码");
                    return;
                }
                $.ajax({
                    url: "searchAround",
                    dataType: "json",
                    type: "post",
                    beforeSend: function () {
                        $("#loadi").show();
                        $("#queryResult").hide();
                    },
                    data: {
                        area_eid: code2areaId[$("#lj_city").val()],
                        number: $("#searchArInput").val().replace(/ /g, '')
                    },
                    success: function (data) {
                        if (data.length == 0) {
                            document.getElementById("aroundAddr").innerHTML = "根据此号码未查找到相关地址信息";
                            $("#zyAddress").val('关键字搜索参考：请输入小区，村庄或周边标志性建筑名，多个关键字之间以空格隔开');
                        } else {
                            var address = data.split("+++")[0];
                            document.getElementById("aroundAddr").innerHTML = "地址: " + address;
                            if (address.length > 2) {
                                address = address.substr(0, 2) + " " + address.substr(2);
                            }
                            $("#zyAddress").val(address);
                        }
                        $("#aroundAddr").show();
                        $("#loadi").hide();
                        var areaCode = $("#zy_city").val();
                        var content = $("#zyAddress").val();
                        if (content != '关键字搜索参考：请输入小区，村庄或周边标志性建筑名，多个关键字之间以空格隔开' && areaCode != '0' && content.length > 2) {
                            window.isAroundSearch = 1;
                            AutoComplete.getUserAddrByInput(areaCode, zySubArea, realSubArea, zyStreetOrTown, address, pageNo, pageSize);
                        }
                    },
                    error: function () {
                        $("#loadi").hide();
                    }
                });

            });

            //在当前页的显示框中按enter按钮
            $('#currentPageNo').bind('keypress', function (event) {
                if (event.keyCode == "13") {
                    var currentPageNo = $('#currentPageNo').val();
                    if (currentPageNo > 10) {
                        alert("对不起，查询范围过大，请添加更多的关键词!");
                        return;
                        $('#currentPageNo').val(10);
                    }
                    if (currentPageNo <= 0 || currentPageNo == '') {
                        $('#currentPageNo').val(1);
                    }
                    //alert('你输入的内容为：' + $('#currentPageNo').val());
                    pageNo = $('#currentPageNo').val();
                    areaCode = $("#zy_city").val();
                    zySubArea = $("#zy_qu").val().split('|')[0];
                    realSubArea = $("#zy_qu").val().split('|')[2];
                    if (zySubArea == '0') {
                        zySubArea = '';
                        realSubArea = '';
                    }
                    zyStreetOrTown = $("#zy_jd").val();
                    var address = $("#zyAddress").val();
                    var keyArray = [];
                    keyArray = address.split(' ');
                    //用户输入的关键词的个数
                    var count = 0;
                    for (var str in keyArray) {
                        if ('' != keyArray[str] && keyArray[str]) {
                            count++;
                        }
                    }
                    AutoComplete.beforeSend();
                    if ($('#isSearchAddress').attr("checked") == 'checked') {
                        AutoComplete.getUserAddrByGolbalSear(areaCode, address, pageNo, pageSize);
                    } else {
                        AutoComplete.getUserAddrByInput(areaCode, zySubArea, realSubArea, zyStreetOrTown, address, pageNo, pageSize);
                    }
                }
            });

            $("#goBut").click(function () {
                var currentPageNo = $('#currentPageNo').val();
                if (currentPageNo > 10) {
                    alert("对不起，查询范围过大，请添加更多的关键词!");
                    $('#currentPageNo').val(10);
                    return;

                }
                if (currentPageNo <= 0 || currentPageNo == '') {
                    $('#currentPageNo').val(1);
                }
                //alert('你输入的内容为：' + $('#currentPageNo').val());
                pageNo = $('#currentPageNo').val();
                areaCode = $("#zy_city").val();
                zySubArea = $("#zy_qu").val().split('|')[0];
                realSubArea = $("#zy_qu").val().split('|')[2];
                if (zySubArea == '0') {
                    zySubArea = '';
                    realSubArea = '';
                }
                zyStreetOrTown = $("#zy_jd").val();
                var address = $("#zyAddress").val();
                var keyArray = [];
                keyArray = address.split(' ');
                //用户输入的关键词的个数
                var count = 0;
                for (var str in keyArray) {
                    if ('' != keyArray[str] && keyArray[str]) {
                        count++;
                    }
                }
                AutoComplete.beforeSend();
                if ($('#isSearchAddress').attr("checked") == 'checked') {
                    AutoComplete.getUserAddrByGolbalSear(areaCode, address, pageNo, pageSize);
                } else {
                    AutoComplete.getUserAddrByInput(areaCode, zySubArea, realSubArea, zyStreetOrTown, address, pageNo, pageSize);
                }
            });
        };
        //9级分词显示
        this.show9 = function (addId, title) {
            var subKey = subKeyArray[addId];
            var cont = new StringBuffer();
            if (!subKey) {
                cont.append("<div style='text-align:center;padding-top:80px;'>没有下级地址信息<br/>您可以直接点击左侧地址进行查询(⊙o⊙)…</div>");
                $("#subKeyDiv").find('ul').html(cont.toString());
                $("#subKeyDiv").show();
                return;
            }
            var subkey1 = subKey.split(" ");
            if (subkey1.length > 0) {
                cont.append("<li>");
                for (var i = 0; i < subkey1.length; i++) {
                    cont.append("<a href='javascript:void(0);' faddress='" + title + "' title='" + subkey1[i] + "'>" + subkey1[i] + "</a>");
                }
                cont.append("</li>");
            } else {
                cont.append("<div style='text-align:center;padding-top:80px;'>没有下级地址信息<br/>您可以直接点击左侧地址进行查询(⊙o⊙)…</div>");
            }
            $("#subKeyDiv").find('ul').html(cont.toString());
            $("#subKeyDiv").show();
        };
        //8级分词 查询地址
        this.getAddressBy8 = function () {
            areaCode = $("#zy_city").val();
            zySubArea = $("#zy_qu").val().split('|')[0];
            realSubArea = $("#zy_qu").val().split('|')[2];
            if (zySubArea == '0') {
                zySubArea = '';
                realSubArea = '';
            }
            zyStreetOrTown = $("#zy_jd").val();
            var address = zyAddress.val();
            AutoComplete.getUserAddrByInput(areaCode, zySubArea, realSubArea, zyStreetOrTown, address, pageNo, pageSize);
        };
        //9级分词 查询地址
        this.getAddressBy9 = function (faddress, saddress) {
            areaCode = $("#zy_city").val();
            zySubArea = $("#zy_qu").val().split('|')[0];
            realSubArea = $("#zy_qu").val().split('|')[2];
            if (zySubArea == '0') {
                zySubArea = '';
                realSubArea = '';
            }
            zyStreetOrTown = $("#zy_jd").val();
            var address = zyAddress.val();
            AutoComplete.getUserAddrByInput(areaCode, zySubArea, realSubArea, zyStreetOrTown, address, pageNo, pageSize)
        };
        this.cleanHide89AndAddrData = function () {
            $("#keyList").html('');
            $("#subKeyList").html('');
            $("#all_add").html('');
            $("#addressDiv").hide();
            $("#subKeyDiv").hide();
            $("#keyDiv").hide();
            $("#queryResult").hide();
            str8 = '';
            str9 = '';
            //$('#zyAddress').val('关键字搜索参考：请输入小区，村庄或周边标志性建筑名，多个关键字之间以空格隔开');
        };
        this.clearStreetOrTownData = function () {
            $("#zy_jd").html("<option value=''>--请选择--</option>");
        };
        //自动补全方法 查询分词
        this.autocomplete = function () {
            if ($("body").length > 0) {
                AutoComplete.init();//初始化信息
            }
            //为绑定自动补全功能的输入框jquery对象
            var $this = this;
            //为节点绑定配置数据
            $this.data("config", this.config);
            //输入框keyup事件
            $this.keyup(function (event) {
                pageNo = 1;
                pageSize = 10;
                var k = event.keyCode;
                var node = event.currentTarget;
                var ctrl = event.ctrlKey;
                var isFunctionalKey = false;//按下的键是否是功能键
                for (var i = 0; i < AutoComplete.functionalKeyArray.length; i++) {
                    if (k == AutoComplete.functionalKeyArray[i]) {
                        isFunctionalKey = true;
                        break;
                    }
                }
                //k键值不是功能键 且是ctrl+v时才触发自动补全功能
                if (!isFunctionalKey && (!ctrl || (ctrl && k == 86))) {
                    //读取节点上的配置数据
                    var keyword_ = $.trim($(node).val());
                    if (keyword_ == null || keyword_ == "") {
                        AutoComplete.hideAutocomplete(AutoComplete.keyDiv);
                        return;
                    }
                    //ajax请求数据
                    var areaCode = $("#zy_city").val();
                    if (areaCode == '0') {
                        alert('请先选择区域');
                        AutoComplete.emptyAddress();
                        return;
                    }
                    zySubArea = $("#zy_qu").val().split('|')[0];
                    realSubArea = $("#zy_qu").val().split('|')[2];
                    if (zySubArea == '0') {
                        zySubArea = '';
                        realSubArea = '';
                    }
                    zyStreetOrTown = $("#zy_jd").val();
                    var address = zyAddress.val();
                    var keyArray = [];
                    keyArray = address.split(' ');
                    //用户输入的关键词的个数
                    var count = 0;
                    for (var str in keyArray) {
                        if ('' != keyArray[str] && keyArray[str]) {
                            count++;
                        }
                    }
                    $("#queryResult").hide();
                    AutoComplete.beforeSend();
                    //判断是否是全地址查询，如果输全地址查询则直接查询用户地址
                    if ($('#isSearchAddress').attr("checked") == 'checked') {
                        AutoComplete.getUserAddrByGolbalSear(areaCode, address, pageNo, pageSize);
                    } else {
                        //判断用户输入关键词个数
                        if ((address.indexOf(" ") >= 0 && count > 1) || (address.indexOf("[") > 0)) {
                            //用户输入的关键词的个数大于1个,则查询用户地址
                            AutoComplete.getUserAddrByInput(areaCode, zySubArea, realSubArea, zyStreetOrTown, address, pageNo, pageSize);
                        } else {
                            //用户输入一个关键词则查询提示地址
                            AutoComplete.getSuggAddrByInput(areaCode, zySubArea, realSubArea, zyStreetOrTown, address, pageNo, pageSize);
                        }

                    }
                }
            });
        };
        this.getStreetTown = function (areaCode, zySubArea) {//获得街道乡镇信息
            $.ajax({
                url: "getStreetNames",
                dataType: "json",
                type: "post",
                beforeSend: function () {
                    $("#loadi").show();
                },
                data: {
                    areaCode: areaCode,
                    subAreaName: zySubArea
                },
                success: function (data) {
                    //查询街道或者乡镇信息
                    AutoComplete.makeStreetOrTownName(data);
                },
                error: function () {
                    $("#zy_jd").html("<option value=''>--请选择--</option>");
                    $("#loadi").hide();
                }
            });

        };
        this.get89addrByStr = function (areaCode, zySubArea, realSubArea, zyStreetOrTown, pageNo, pageSize) {//点击乡镇或街道时查询89级地址
            $.ajax({
                url: "getSuggAddr",
                dataType: "json",
                type: "post",
                beforeSend: function () {
                    $("#loadi").show();
                },
                data: {
                    areaCode: areaCode,
                    subAreaName: zySubArea,
                    /*  realSubAreaName:realSubArea,*/
                    streetName: zyStreetOrTown,
                    pageNo: pageNo,
                    pageSize: 10
                },
                success: function (data) {
                    $("#loadi").hide();
                    //查询提示分词
                    AutoComplete.makeKeyContAndShow(data);

                },
                error: function () {
                    $("#loadi").hide();
                }
            });
        };
        this.getUserAddrByInput = function (areaCode, subAreaName, realSubArea, stOrTnName, address, pageNo, pageSize) {//根据用户输入查找用户地址
            var isAroundSearch_dup = 0;
            if (window.isAroundSearch == 1) {
                isAroundSearch_dup = 1;
                window.isAroundSearch = 0;
            }

            if (subAreaName == '0') {
                subAreaName = '';
            }

            if (address.trim().indexOf(" ") < 0) {
                address = $("#zy_city").find("option:selected").text() + " " + address;
            }

            $.ajax({
                url: "getUserAddrBySuggest",
                dataType: "json",
                type: "post",
                beforeSend: function () {
                    $("#loadi").show();
                    $("#queryResult").hide();
                },
                data: {
                    areaCode: areaCode,
                    subAreaName: subAreaName,
                    /* realSubAreaName:realSubArea,*/
                    streetName: stOrTnName,
                    input: address,
                    pageNo: pageNo,
                    pageSize: pageSize
                },
                success: function (data) {
                    if (isAroundSearch_dup == 1 && data.length > 0) {
                        AutoComplete.getAroundAddrAbility(data[0]);
                    } else {
                        //查询末梢地址数据
                        AutoComplete.makeAddressContAndShow(data, selaSystem)
                    }
                },
                error: function () {
                    $("#loadi").hide();
                }
            });

        };
        this.getUserAddrByGolbalSear = function (areaCode, address, pageNo, pageSize) {//全地址查询
            $.ajax({
                url: "getUserAddrByInput",
                dataType: "json",
                type: "post",
                beforeSend: function () {
                    $("#loadi").show();
                },
                data: {
                    areaCode: areaCode,
                    input: address,
                    pageNo: pageNo,
                    pageSize: pageSize
                },
                success: function (data) {
                    //查询末梢地址数据
                    AutoComplete.makeAddressContAndShow(data, selaSystem)

                },
                error: function () {
                    $("#loadi").hide();
                }
            });
        };
        this.getSuggAddrByInput = function (areaCode, subAreaName, realSubArea, stOrTownName, address, pageNo, pageSize) {//根据用户输入获得89级地址
            $.ajax({
                url: "getSuggAddrWithInput",
                dataType: "json",
                type: "post",
                beforeSend: function () {
                    $("#loadi").show();
                },
                data: {
                    areaCode: areaCode,
                    subAreaName: subAreaName,
                    /* realSubAreaName:realSubArea,*/
                    streetName: stOrTownName,
                    input: address,
                    pageSize: 10,
                    pageNo: pageNo
                },
                success: function (data) {
                    if (GetQueryString("fromsystem") == 'icss' && (data == null || !data.length)) {
                        AutoComplete.getUserAddrByInput(areaCode, subAreaName, realSubArea, stOrTownName, address, pageNo, pageSize);
                    } else {
                        //查询提示分词
                        AutoComplete.makeKeyContAndShow(data);
                    }

                    $("#loadi").hide();

                },
                error: function () {
                    $("#loadi").hide();
                }
            });
        };
        //封装发送请求
        this.sendHttp = function (areaCode, subAreaCode, addressNamePattern, addressCodePattern, type) {
            $.getAjax({
                url: url,
                contentType: "application/x-www-form-urlencoded",
                dataType: "json",
                data: {
                    areaCode: areaCode,
                    subAreaCode: subAreaCode,
                    addressNamePattern: addressNamePattern,
                    addressCodePattern: addressCodePattern
                },
                success: function (msg) {
                    if (type == 1) {
                        AutoComplete.makeKeyContAndShow(msg)
                    } else {
                        AutoComplete.makeAddressContAndShow(msg)
                    }

                }
            });
        };
        //隐藏自动补全的下拉框
        this.hideAutocomplete = function (_obj) {
            _obj.hide();
        },
            this.beforeSend = function () {
                AutoComplete.showOne('loadi');
            },
            this.showOne = function (id) {
                var ids = ["keyDiv", "addressDiv", "queryResult", "loadi", "noResultDiv"];
                for (var i = 0; i < ids.length; i++) {
                    if (id == ids[i]) {
                        $("#" + ids[i]).show();
                    } else {
                        $("#" + ids[i]).hide();
                    }
                }
            };
        //动态生成乡镇地址的下拉菜单
        this.makeStreetOrTownName = function (data) {
            $("#loadi").hide();
            $("#zy_jd").html('');
            if (data) {
                var optionBuffer = new StringBuffer();
                optionBuffer.append("<option value=''>--请选择--</option>");
                for (var i = 0; i < data.length; i++) {
                    if (data[i].name != '/') {
                        optionBuffer.append("<option value=" + data[i].name + ">" + data[i].name + "</option>");
                    }
                }
                $("#zy_jd").html(optionBuffer.toString());
                //$("#zy_jd")[0].innerHTML = optionBuffer.toString();
            } else {
                $("#zy_jd").html("<option value=''>--请选择--</option>");
                //$("#zy_jd")[0].innerHTML = "<option value=''>--请选择--</option>";
            }
        };
        //组装分词自动补全下拉框并显示
        this.makeKeyContAndShow = function (_data) {
            AutoComplete.resetStr89();
            this.cleanHide89AndAddrData();
            $("#loadi").hide();
            $("#keyDiv").hide();
            $("#addressDiv").hide();
            $("#queryResult").hide();
            var keyDiv = $("#keyDiv");
            if (_data == null || !_data.length) {
                cont = "<div class='tips_font'>" + errorMsg + "</div>";
                $("#noResultDiv").html(cont);
                $("#noResultDiv").show();
                return;
            }
            $("#noResultDiv").hide();
            var cont = new StringBuffer();
            for (var i = 0; i < _data.length; i++) {
                var address = _data[i].nameBak;
                var re = new RegExp("<em>", "g");
                var re2 = new RegExp("</em>", "g");
                address = address.replace(re, "").replace(re2, '');
                cont += "<li addId='" + address + i + "' title='" + address + "'>" + _data[i].nameBak + "</li>";
                subKeyArray["" + address + i + ""] = _data[i].subNames;
            }
            $("#keyList").html(cont.toString());
            keyDiv.show();
            //重新定位
            //_dialog.position(null,'50px');
            //AutoComplete.position();
        };
        //重新定位
        this.position = function () {
            var scllTop = parent.document.documentElement.scrollTop;
            _dialog.position(null, scllTop / 8 + 20 + 'px');
        };
        //组装地址自动补全下拉框并显示
        this.makeAddressContAndShow = function (_data, flag) {
            //如果是调用地址查询接口，则隐藏分页按钮
            if (flag == 1) {
                $("#fenyediv").hide();
            }
            this.cleanHide89AndAddrData();
            $("#loadi").hide();
            $("#queryResult").hide();
            $("#addressDiv").hide();
            var addressDiv = $("#addressDiv");
            if (_data == null || !_data.length) {
                cont = "<div class='tips_font'>" + errorMsg + "</div>";
                $("#noResultDiv").html(cont);
                $("#loadi").hide();
                $("#noResultDiv").show();
                return;
            }
            $("#noResultDiv").hide();
            var cont = new StringBuffer();
            for (var i = 0; i < _data.length; i++) {
                //################需要添加
                if (_data[i].sites) {
                    if (_data[i].sites.length > 0) {
                        var allSiteNames = '';
                        var allSiteIds = '';
                        var allSiteCodes = '';
                        for (var ii = 0; ii < _data[i].sites.length; ii++) {
                            if (ii == _data[i].sites.length - 1) {
                                allSiteNames += _data[i].sites[ii].siteName;
                                allSiteIds += _data[i].sites[ii].siteId;
                                allSiteCodes += _data[i].sites[ii].siteCode;
                            } else {
                                allSiteNames += _data[i].sites[ii].siteName + ',';
                                allSiteIds += _data[i].sites[ii].siteId + ',';
                                allSiteCodes += _data[i].sites[ii].siteCode + ',';
                            }
                        }
                        if (_data[i].subAreaName) {
                            cont += "<li><a zySubAreaName='" + _data[i].subAreaName + "' oldSubAreaId='" + _data[i].oldSubAreaId + "' addrId='" + _data[i].id + "' siteId='" + allSiteIds + "' siteName='" + allSiteNames + "' siteCode='" + allSiteCodes + "'>" + _data[i].highlightText + "</a></li>";
                        } else {
                            cont += "<li><a zySubAreaName='' oldSubAreaId='" + _data[i].oldSubAreaId + "' addrId='" + _data[i].id + "' siteId='" + allSiteIds + "' siteName='" + allSiteNames + "' siteCode='" + allSiteCodes + "'>" + _data[i].highlightText + "</a></li>";
                        }

                    }
                } else {
                    cont += "<li><a addrId='" + _data[i].id + "' siteId='' siteCode='' siteName=''" + " oldSubAreaId='' zySubAreaName=''>" + _data[i].highlightText + "</a></li>";
                }
            }
            addressDiv.find("ul").html(cont.toString());
            /*if (pageNo && pageNo == '1') {
             //设置总数
             //$('#total').html("共计"+_data[0].total+"条");
             //将当前页数设为1
             addressPageNo = 1;
             $('#currentPageNo').val(addressPageNo);
             //计算总页数
             totalPage = _data[0].total%100==0?_data[0].total/100:Math.floor(_data[0].total/100)+1;
             //设置总页数
             $('#totalPage').val(totalPage);
             }else {
             $('#currentPageNo').val(addressPageNo);
             }*/
            $('#currentPageNo').val(pageNo);
            addressDiv.show();
            if (flag == 0) {
                $("#fenyediv").show();
            }

            addressDiv.find("li").each(function (index) {
                $(this).data("jsonData", _data[index]);
            });
        };
        //清空地址输入框
        this.emptyAddress = function () {
            $('#zyAddress').val('关键字搜索参考：请输入小区，村庄或周边标志性建筑名，多个关键字之间以空格隔开');
        };
        this.addRessCallback = function (data) {
            var rjson = $.toJSON(data);
            var obj = eval('(' + rjson + ')');
            //由JSON字符串转换为JSON对象
            addressObj = obj;
            addressObj.subAreaId = $("#zy_qu").val().split('|')[1];
            AutoComplete.addressQuery();
            getAddressInfo(addressObj);
        };
        this.resetStr89 = function () {
            str8 = '';
            str9 = '';
        };
        this.getaddressByOIP = function (areaCode, zySubArea, zyStreetOrTown, address) {
            $.ajax({
                type: "post",
                url: "getAddrBySearch",
                dataType: "json",
                beforeSend: function () {
                    $("#addressDiv").hide();
                    $("#loadi").show();
                    $("#zy_tab").hide();
                    $("#noResultDiv").hide();
                    $("#keyDiv").hide();
                    $("#queryResult").hide();
                },
                data: {
                    areaCode: areaCode,
                    subAreaName: zySubArea,
                    streetName: zyStreetOrTown,
                    input: address,
                    oipUrl: addrOipUrl,
                    sysCode: 1001,
                    pageNo: 1,
                    pageSize: 10
                },
                success: function (data) {
                    AutoComplete.makeAddressContAndShow(data, addrOip);
                },
                error: function (data) {
                    $("#addressDiv").hide();
                    var cont = "<div class='tips_font'>" + "调用地址查询失败" + "</div>";
                    $("#noResultDiv").html(cont);
                    $("#noResultDiv").show();
                    $("#loadi").hide();
                }
            });
        };
        this.getAbility = function (areaCode, addrId, siteId) {
            $.ajax({
                type: "post",
                url: "findAbility",
                dataType: "json",
                beforeSend: function () {
                    $("#addressDiv").hide();
                    $("#loadi").show();
                },
                data: {
                    areaCode: areaCode,
                    addrId: addrId,
                    siteId: siteId
                },
                success: function (data) {
                    $("#loadi").hide();
                    $("#jrfs").html(data[0].jiRuFangShi);
                    $("#lldk").html(data[0].liLunDaiKuan);
                    $("#jbzy").html(data[0].isJuBei);
                    $("#gepon").html(data[0].gePon);
                    $("#queryResult").show();
                },
                error: function (data) {
                    $("#addressDiv").hide();
                    $("#queryResult").html('数据查询失败2...');
                    $("#queryResult").show();
                }
            });
        };
        this.getResAbility = function (areaCode, addrId, siteIds, siteNames, oipUrl) {
            $.ajax({
                type: "post",
                url: "getResAbility",
                dataType: "json",
                beforeSend: function () {
                    $("#addressDiv").hide();
                    $("#loadi").show();
                },
                data: {
                    'areaCode': areaCode,
                    'addrId': addrId,
                    'siteIds': siteIds,
                    'siteNames': siteNames,
                    'oipUrl': oipUrl
                },
                success: function (data) {
                    $("#loadi").hide();
                    $("#noResultDiv").hide();
                    $("#abilityTable").html();
                    if (data) {
                        var tableCont = new StringBuffer();
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
                            $("#abilityTable").html(tableCont.toString());
                            if (GetQueryString("fromsystem") == 'icss') {
                                $("#projInfo").html("<button id='projSearch2'>工程信息查询</button>");
                            }

                            $("#queryResult").show();
                        }
                    } else {
                        $("#addressDiv").hide();
                        $("#queryResult").html('未查到该地址对应资源能力信息!');
                        $("#queryResult").show();
                    }

                },
                error: function (data) {
                    $("#addressDiv").hide();
                    $("#queryResult").html('数据查询失败2...');
                    $("#queryResult").show();
                }
            });
        };
        this.addressQuery = function () {
            AutoComplete.resetStr89();
            var areaCode = $("#zy_city").val();
            var address = $("#zyAddress").val();
            if (address == '' || address == null || address == 'undefined') {
                alert('请输入地址查询');
                return;
            }
            if (addressObj) {
                $.ajax({
                    type: 'POST',
                    url: "/netup/broadbandres/broadResource.action",
                    data: {areaCode: areaCode, addressId: addressObj.addressId, siteId: addressObj.siteId},
                    dataType: "json",
                    beforeSend: function () {
                        $("#addressDiv").hide();
                        $("#loadi").show();
                    },
                    success: function (result) {
                        $("#jrfs").html(result.terminalPortType);
                        $("#lldk").html(result.logicRate);
                        $("#jbzy").html(result.ability);
                        $("#loadi").hide();
                        $("#queryResult").show();

                    },
                    error: function () {
                        alert("查询失败");
                        $("#queryResult").html('数据查询失败3...');
                        $("#queryResult").show();
                    }
                });
            } else {
                $("#queryResult").show();
            }
        };
        this.getAroundAddrAbility = function (data) {
            var addrId = data.id;
            var siteNames = '';
            var siteIds = '';
            if (data.sites) {
                if (data.sites.length > 0) {

                    for (var ii = 0; ii < data.sites.length; ii++) {
                        if (ii == data.sites.length - 1) {
                            siteNames += data.sites[ii].siteName;
                            siteIds += data.sites[ii].siteId;
                        } else {
                            siteNames += data.sites[ii].siteName + ',';
                            siteIds += data.sites[ii].siteId + ',';
                        }
                    }

                }
            }
            AutoComplete.getResAbility($("#zy_city").val(), addrId, siteIds, siteNames, "http://134.64.116.90:9102/service/mboss/route?wsdl");
        };
    };

    $.fn.autocomplete = AutoComplete.autocomplete;
})(jQuery)

/** 拼接字符串 **/
;
function StringBuffer() {
    this._string_ = [];
}
StringBuffer.prototype.append = function (str) {
    this._string_.push(str);
}
;
StringBuffer.prototype.toString = function () {
    return this._string_.join('');
};
