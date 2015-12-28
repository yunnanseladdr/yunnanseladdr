package com.sinovate.ngrms.pahdx.svrinter.mapseladdr.ps.impl;

import com.sinovate.ngrms.pahdx.svrinter.mapseladdr.aui.vo.SuggestiveAddrVo;
import com.sinovate.ngrms.pahdx.svrinter.mapseladdr.ps.svc.PsSuggestAddrSvc;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2015/5/22.
 */
@ContextConfiguration(locations = {"classpath:app-test-context.xml"})
public class PsSuggestAddrSvcImplTest extends AbstractTestNGSpringContextTests {

    @Autowired
    private PsSuggestAddrSvc psSuggestAddrSvc;

    @DataProvider(name = "testSearchData")
    public static Object[][] testSearchData() {
        return new Object[][] {
            new Object[] {"551","", 1,10,createExepectedResults(0)},
            new Object[] {"551","", 1,10,createExepectedResults(1)},
            new Object[] {"551","", 1,10,createExepectedResults(2)}
        };
    }

    public static List<SuggestiveAddrVo> createExepectedResults(int i) {

        List<SuggestiveAddrVo> suggestiveAddrVos = new ArrayList<>();

        switch (i) {
            case 0 :
                suggestiveAddrVos.add(new SuggestiveAddrVo("姜万郢移民点","1","姜万郢移民点"));
                suggestiveAddrVos.add(new SuggestiveAddrVo("经济技术开发区","4","经济技术开发区"));
                suggestiveAddrVos.add(new SuggestiveAddrVo("华帝润园小区","3","华帝润园小区"));
                break;

            case 1 :
                suggestiveAddrVos.add(new SuggestiveAddrVo("西藏路","6","西藏路"));
                suggestiveAddrVos.add(new SuggestiveAddrVo("芙蓉路","5","芙蓉路"));
                break;

            case 2 :
                suggestiveAddrVos.add(new SuggestiveAddrVo("蜀山区","2","蜀山区"));
                break;

            case 3 :
                break;

            default:
        }
        return suggestiveAddrVos;
    }

    @Test(enabled = false, dataProvider = "testSearchData")
    public void search(String areaCode, String searchText, int pageNo,int pageSize,List<SuggestiveAddrVo> expected) {

        List<SuggestiveAddrVo> actual = psSuggestAddrSvc.getSuggAddr(areaCode,searchText,pageNo,pageSize);
        if(expected == null || actual == null) Assert.assertTrue(actual == expected);
        Assert.assertEquals(actual.size(), expected.size());
        for(int i=0; i<actual.size();i++) {
            Assert.assertEquals(actual.get(i), expected.get(i));
        }
    }
}
