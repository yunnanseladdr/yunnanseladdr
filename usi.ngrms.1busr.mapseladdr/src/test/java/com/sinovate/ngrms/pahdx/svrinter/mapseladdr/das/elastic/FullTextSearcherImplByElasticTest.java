package com.sinovate.ngrms.pahdx.svrinter.mapseladdr.das.elastic;

import com.sinovate.ngrms.pahdx.svrinter.mapseladdr.das.fullsearch.FullTextSearcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by zszhang on 2015/5/22.
 */
@ContextConfiguration(locations = {"classpath:app-test-context.xml"})
public class FullTextSearcherImplByElasticTest extends AbstractTestNGSpringContextTests {
    @Autowired
    FullTextSearcher fullTextSearcher;
    private final static String dbname = "testngrmsaddr551";

    @DataProvider(name = "testSearchData")
    public static Object[][] testSearchData() {
        return new Object[][]{
                new Object[]{dbname, "UserAddr", "龙泉中路 rts 406s", 1, 10, getQueryResult(0)},
                new Object[]{dbname,"UserAddr", "白马 wdw smq", 1, 10, getQueryResult(1)},
                new Object[]{dbname, "UserAddr", "吃糖 3b-11 101", 1, 10, getQueryResult(2)}
        };
    }

    private static List<Map<String, Object>> getQueryResult(int i) {
        List<Map<String, Object>> maps = new ArrayList<>();
        switch (i) {
            case 0:
                maps.add(createMap("4", "安徽合肥肥东县城区店埠镇龙泉中路/张维升熟食店门前杆/用户003",
                        "安徽合肥肥东县城区店埠镇<em>龙泉中路</em>/张维升熟食店门前杆/用户003"));
                maps.add(createMap("6", "安徽合肥肥东县城区店埠镇青年路瑞泰尚园05幢3单元406室",
                        "安徽合肥肥东县城区店埠镇青年路<em>瑞泰尚园</em>05幢3单元<em>406室</em>"));
                maps.add(createMap("5", "安徽合肥肥东县城区店埠镇青年路瑞泰尚园05幢3单元405室",
                        "安徽合肥肥东县城区店埠镇青年路<em>瑞泰尚园</em>05幢3单元405室"));
                break;
            case 1:
                maps.add(createMap("2", "安徽合肥肥东县农村长临河镇白马行政村洼地吴自然村孙明权门前杆上用户4",
                        "安徽合肥肥东县农村长临河镇<em>白马行政村</em><em>洼地吴自然村</em><em>孙明权门前杆上用户4</em>"));
                maps.add(createMap("3", "安徽合肥肥东县农村长临河镇白马行政村洼地吴自然村袁凤家门前杆上用户1",
                        "安徽合肥肥东县农村长临河镇<em>白马行政村</em><em>洼地吴自然村</em>袁凤家门前杆上用户1"));
                break;
            case 2:
                maps.add(createMap("7", "安徽合肥合肥市蜀山区樊洼路金色池塘3B-11幢1单元1层覆盖101-602",
                        "安徽合肥合肥市蜀山区樊洼路<em>金色池塘</em><em>3B-11幢</em>1单元1层覆盖101-602"));
                break;
            default:
        }
        return maps;
    }

    private static Map<String, Object> createMap(String id, String name, String highlightText) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", id);
        map.put("name", name);
        map.put("highlightText", highlightText);
        return map;
    }

    @Test(enabled = false,dataProvider = "testSearchData")
    public void search(String dbname, String tablename, String searchText, int pageNo, int pageSize, List<Map<String, Object>> expected) {
        List<Map<String, Object>> actual = fullTextSearcher.search(dbname, tablename, searchText, pageNo, pageSize);
        if (expected == null) Assert.assertTrue(actual == expected);
        for (int i = 0; i < expected.size(); i++) {
            Map<String, Object> e = expected.get(i);
            Map<String, Object> a = actual.get(i);
            Assert.assertEquals(a.get("id"), e.get("id"));
            Assert.assertEquals(a.get("name"), e.get("name"));
            Assert.assertEquals(a.get("highlightText"), e.get("highlightText"));
        }
    }

}
