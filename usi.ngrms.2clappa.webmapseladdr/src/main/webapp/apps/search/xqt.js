/**
 * Created by limaple on 10/17/15.
 * 小前台交互处理
 */

// 小前台传入参数处理
function parseXML(text) {
    try //Internet Explorer
    {
        var xmlDoc = new ActiveXObject("Microsoft.XMLDOM");
        xmlDoc.async = "false";
        xmlDoc.loadXML(text);
        return xmlDoc;
    }
    catch (e) {
        try //Firefox, Mozilla, Opera, etc.
        {
            var parser = new DOMParser();
            xmlDoc = parser.parseFromString(text, "text/xml");
            return xmlDoc;
        }
        catch (e) {
            alert(e.message);
            return -1;
        }
    }
}

$(document).ready(function () {
    if (GetQueryString("param")) {
        var xmlDoc = parseXML(GetQueryString("param"));
        try {
            window.xqt_areaCode = xmlDoc.getElementsByTagName("info")[0].getElementsByTagName("areaCode")[0].childNodes[0].nodeValue;
            window.xqt_productCode = xmlDoc.getElementsByTagName("info")[0].getElementsByTagName("productCode")[0].childNodes[0].nodeValue;
            window.xqt_productInstance = xmlDoc.getElementsByTagName("info")[0].getElementsByTagName("productInstance")[0].childNodes[0].nodeValue;
            window.xqt_operator = xmlDoc.getElementsByTagName("info")[0].getElementsByTagName("operator")[0].childNodes[0].nodeValue;
            window.xqt_isMap = xmlDoc.getElementsByTagName("info")[0].getElementsByTagName("isMap")[0].childNodes[0].nodeValue;
            window.xqt_paramExisteFlag = 1;
        } catch (e) {
            window.xqt_paramExisteFlag = 0;
        }

        if (xqt_paramExisteFlag == 1) {
            if (xqt_isMap == "1") {
                document.getElementById("mapTab").click();
                var $sel_city = $("#sel_city");
                $sel_city.find("option").each(function () {
                    if ($(this).val() == xqt_areaCode) {
                        $(this).attr("selected", true);
                    }
                });
                var $zy_city = $("#zy_city");
                $zy_city.find("option").each(function () {
                    if ($(this).val() == xqt_areaCode) {
                        $(this).attr("selected", true);
                    }
                });
                $sel_city.prop("disabled", "true");
                $zy_city.prop("disabled", "true");
            }

            if (xqt_productCode && xqt_productInstance) {
                $.ajax({
                    type: "post",
                    url: "getXqtDataByProduct",
                    dataType: "json",
                    beforeSend: function () {

                    },
                    data: {
                        areaCode: xqt_areaCode,
                        productCode: xqt_productCode,
                        productInstance: xqt_productInstance
                    },
                    success: function (data) {
                        /*console.info(data['exchcode']);
                        console.info(data['exchname']);*/


                    },
                    error: function (data) {

                    }
                });
            }
        }
    }


});