package com.sinovate.ngrms.pahdx.svrinter.mapseladdr.ps.impl;

import com.sinovate.ngrms.pahdx.svrinter.mapseladdr.aui.vo.UserAddrVo;
import com.sinovate.ngrms.pahdx.svrinter.mapseladdr.ps.svc.PsUserAddrSvc;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zszhang on 2015/5/22.
 */
@ContextConfiguration(locations = {"classpath:app-test-context.xml"})
public class PsUserAddrSvcImplTest extends AbstractTestNGSpringContextTests {

    @Autowired
    private PsUserAddrSvc psUserAddrSvc;

    @DataProvider(name="testSearchData")
    public static Object[][] testSearchData() {
        return new Object[][] {
                new Object[] {"551", "龙泉中路 rts 406s", 1, 10, createExepectedResults(0)},
                new Object[] {"551", "白马 wdw smq", 1, 10, createExepectedResults(1)},
                new Object[] {"551", "吃糖 3b-11 101", 1, 10, createExepectedResults(2)},
        };
    }

    public static List<UserAddrVo> createExepectedResults(int i) {
        List<UserAddrVo> userAddrVos = new ArrayList<>();
        switch (i) {
            case 0:
                userAddrVos.add(new UserAddrVo("安徽合肥肥东县城区店埠镇青年路<em>瑞泰尚园</em>05幢3单元<em>406室</em>","6","安徽合肥肥东县城区店埠镇青年路瑞泰尚园05幢3单元406室"));
                userAddrVos.add(new UserAddrVo("安徽合肥肥东县城区店埠镇<em>龙泉中路</em>/张维升熟食店门前杆/用户003","4","安徽合肥肥东县城区店埠镇龙泉中路/张维升熟食店门前杆/用户003"));
                userAddrVos.add(new UserAddrVo("安徽合肥肥东县城区店埠镇青年路<em>瑞泰尚园</em>05幢3单元405室","5","安徽合肥肥东县城区店埠镇青年路瑞泰尚园05幢3单元405室"));
                break;
            case 1:
                userAddrVos.add(new UserAddrVo("安徽合肥肥东县农村长临河镇<em>白马行政村</em><em>洼地吴自然村</em><em>孙明权门前杆上用户4</em>","2","安徽合肥肥东县农村长临河镇白马行政村洼地吴自然村孙明权门前杆上用户4"));
                userAddrVos.add(new UserAddrVo("安徽合肥肥东县农村长临河镇<em>白马行政村</em><em>洼地吴自然村</em>袁凤家门前杆上用户1","3","安徽合肥肥东县农村长临河镇白马行政村洼地吴自然村袁凤家门前杆上用户1"));
                break;
            case 2:
                userAddrVos.add(new UserAddrVo("安徽合肥合肥市蜀山区樊洼路金色池塘<em>3B-11幢</em>1单元1层覆盖101-602","7","安徽合肥合肥市蜀山区樊洼路金色池塘3B-11幢1单元1层覆盖101-602"));
                break;
            case 3:
                break;
            default:
        }

        return userAddrVos;
    }

    @Test(enabled = false,dataProvider = "testSearchData")
    public void search(String areaCode, String searchText, int pageNo,int pageSize, List<UserAddrVo> expected){
        List<UserAddrVo> actual = psUserAddrSvc.getUserAddr(areaCode,searchText,pageNo,pageSize);
        if( expected == null || actual == null ) Assert.assertTrue(actual == expected);
        Assert.assertEquals(actual.size(), expected.size());
        for(int i=0; i<actual.size();i++) {
            Assert.assertEquals(actual.get(i), expected.get(i));
        }
    }
}
