var addressObj;
$(function(){
    //填充地市
    var GLATNID = $("#GLATNID").val();
    if(GLATNID){
        $("#zy_city").val(GLATNID);
        $("#zy_city").change();
        $("#zy_city").attr('disabled','disabled');
    }
    //地址栏获取和失去焦点事件
    $("#zyAddress").focus(function(){
        if($(this).val()=='关键字搜索参考：请输入小区，村庄或周边标志性建筑名，多个关键字之间以空格隔开'){
            $(this).val('');
        }
    }).blur(function(){
        if($(this).val()==''){
            $(this).val('关键字搜索参考：请输入小区，村庄或周边标志性建筑名，多个关键字之间以空格隔开');
        }
    })
    //地址栏绑定自动补全事件
    $("#zyAddress").autocomplete();
})
//区域
var zy_arr = new Array();
zy_arr[0] = [new Option('--请选择--','0',true,true)];
zy_arr[1] = [new Option('--请选择--','0',true,true),//(显示值，值)
    new Option('瑶海区','瑶海区|48124|合肥市'),new Option('庐阳区','庐阳区|48124|合肥市'),new Option('蜀山区','蜀山区|48124|合肥市'),new Option('包河区','包河区|48124|合肥市'),
    new Option('长丰县','长丰县|48123|长丰县'), new Option('肥东县','肥东县|48121|肥东县'),new Option('肥西县','肥西县|48122|肥西县'),new Option('庐江县','庐江县|24329|庐江县'),
    new Option('巢湖市','巢湖市|24328|巢湖市')];
zy_arr[2] = [new Option('--请选择--','0',true,true),new Option('镜湖区','镜湖区|33822|芜湖市'),new Option('弋江区','弋江区|33822|芜湖市'),new Option('鸠江区','鸠江区|33822|芜湖市'),
    new Option('三山区','三山区|33822|芜湖市'),new Option('南陵县','南陵县|33823|南陵县'),new Option('芜湖县','芜湖县|33825|芜湖县'),new Option('繁昌县','繁昌县|33824|繁昌县'),
    new Option('无为县','无为县|24326')];
zy_arr[3] = [new Option('--请选择--','0',true,true),new Option('龙子湖区','龙子湖区|4602|蚌埠市'),new Option('蚌山区','蚌山区|4602|蚌埠市'),new Option('禹会区','禹会区|4602|蚌埠市'),
    new Option('淮上区','淮上区|4602|蚌埠市'),new Option('怀远县','怀远县|4605|怀远县'),new Option('五河县','五河县|4603|五河县'),new Option('固镇县','固镇县|4604|固镇县')];
zy_arr[4] = [new Option('--请选择--','0',true,true),new Option('大通区','大通区|6497|淮南市'),new Option('田家庵区','田家庵区|6497|淮南市'),new Option('谢家集区','谢家集区|6497|淮南市'),
    new Option('八公山区','八公山区|6497|淮南市'),new Option('潘集区','潘集区|6497|淮南市'), new Option('凤台县','凤台县|6498|凤台县')];
zy_arr[5] = [new Option('--请选择--','0',true,true),new Option('花山区','花山区|17383|马鞍山市'),new Option('雨山区','雨山区|17383|马鞍山市'),new Option('博望区','博望区|17383|马鞍山市'),
    new Option('含山县','含山县|24327|含山县'),new Option('和县','和县|24325|和县'),new Option('当涂县','当涂县|17382当涂县')];
zy_arr[6] = [new Option('--请选择--','0',true,true),new Option('杜集区','杜集区|16444|淮北市'),new Option('相山区','相山区|16444|淮北市'),new Option('烈山区','烈山区|16444|淮北市'),new Option('濉溪县','濉溪县|16443|濉溪县')];
zy_arr[7] = [new Option('--请选择--','0',true,true),new Option('铜官山区','铜官山区|11319|铜陵市'),new Option('狮子山区','狮子山区|11319|铜陵市'),new Option('郊区','郊区|11319|铜陵市'),new Option('铜陵县','铜陵县|11320|铜陵县')];
zy_arr[8] = [new Option('--请选择--','0',true,true),new Option('迎江区','迎江区|12029|安庆市'),new Option('大观区','大观区|12029|安庆市'),new Option('宜秀区','宜秀区|12029|安庆市'),
    new Option('怀宁县','怀宁县|12027|怀宁县'),new Option('枞阳县','枞阳县|12026|枞阳县'),
    new Option('潜山县','潜山县|12022|潜山县'),new Option('太湖县','太湖县|12028|太湖县'),new Option('宿松县','宿松县|12030|宿松县'),new Option('望江县','望江县|12025|望江县'),new Option('岳西县','岳西县|12023|岳西县'),new Option('桐城市','桐城市|12024|桐城市')];
zy_arr[9] = [new Option('--请选择--','0',true,true),new Option('黄山市','黄山市|11111|黄山市'),new Option('黄山景区','黄山景区|11111|黄山景区'),new Option('黄山区','黄山区|45659|黄山区'),new Option('徽州区','徽州区|45656|徽州区'),new Option('歙县','歙县|45658|歙县'),new Option('休宁县','休宁县|45655|休宁县'),
    new Option('黟县','黟县|45660|黟县'),new Option('祁门县','祁门县|45661|祁门县')];
zy_arr[10] = [new Option('--请选择--','0',true,true),new Option('琅琊区','琅琊区|7754|滁州市'),new Option('南谯区','南谯区|7754|滁州市'),
    new Option('来安县','来安县|7752|来安县'),new Option('全椒县 ','全椒县|7756|全椒县'),new Option('定远县','定远县|7750|定远县'),new Option('凤阳县','凤阳县|7755|凤阳县'),
    new Option('天长市','天长市|7753|天长市'),new Option('明光市','明光市|7751|明光市')];
zy_arr[11] = [new Option('--请选择--','0',true,true) ,new Option('开发区','开发区|11111|阜阳市'),new Option('颍州区','颍州区|27354|阜阳市'),new Option('颍东区','颍东区|27354|阜阳市'),new Option('颍泉区','颍泉区|27354|阜阳市'),
    new Option('临泉县 ','临泉县|27356|临泉县'),new Option('太和县 ','太和县|27351|太和县'),new Option('阜南县','阜南县|27353|阜南县'),
    new Option('颍上县','颍上县|27352|颍上县'),new Option('界首市','界首市|27355|界首市')];
zy_arr[12] = [new Option('--请选择--','0',true,true),new Option('埇桥区','埇桥区|20201|宿州市'),new Option('砀山县','砀山县|20200|砀山县'),new Option('萧县','萧县|20203|萧县'),
    new Option('灵璧县','灵璧县|20202|灵璧县'),new Option('泗县 ','泗县|20199|泗县')];
zy_arr[13] = [new Option('--请选择--','0',true,true),new Option('市区','市区|11111|六安市'),new Option('叶集','叶集|11111|六安市'),new Option('金安区','金安区|22|六安市'),
    new Option('裕安区','裕安区|22|六安市'),new Option('寿县','寿县|23|寿县'),new Option('霍邱县','霍邱县|20|霍邱县'),
    new Option('舒城县 ','舒城县|24|舒城县'),new Option('金寨县 ','金寨县|21|金寨县'),new Option('霍山县','霍山县|19|霍山县')];
zy_arr[14] = [new Option('--请选择--','0',true,true),new Option('谯城区','谯城区|40972|亳州市'),new Option('涡阳县','涡阳县|40971|涡阳县'),new Option('蒙城县','蒙城县|40973|蒙城县'),new Option('利辛县','利辛县|40970|利辛县')];
zy_arr[15] = [new Option('--请选择--','0',true,true),new Option('池州市','池州市|32313902|池州市'),new Option('东至县','东至县|31903|东至县'),new Option('石台县','石台县|31900|石台县'),new Option('青阳县','青阳县|31901|青阳县'),new Option('九华景区','九华景区|31899|九华山景区')];
zy_arr[16] = [new Option('--请选择--','0',true,true),new Option('宣城市','宣城市|11111|宣城市'),new Option('郎溪县','郎溪县|43421|郎溪县'),new Option('广德县','广德县|43424|广德县'),new Option('泾县','泾县|43419|泾县'),new Option('绩溪县','绩溪县|43422|绩溪县'),new Option('旌德县','旌德县|43423|旌德县'),
    new Option('宁国市','宁国市|43418|宁国市')];
/** 切换地市 **/
function zy_changerx(index){
    restAddress();
    document.getElementById('zy_qu').length = 0;
    for(var i=zy_arr[index].length;i>0;i--){
        document.getElementById('zy_qu').options[i-1] = zy_arr[index][i-1];
    }
}
//切换区
function zy_changeQu(){
    restAddress();
}
//重置查询文本
function restAddress(){
    if($("#zyAddress").val()!='关键字搜索参考：请输入小区，村庄或周边标志性建筑名，多个关键字之间以空格隔开'){
        $("#zyAddress").val('关键字搜索参考：请输入小区，村庄或周边标志性建筑名，多个关键字之间以空格隔开');
    }

}