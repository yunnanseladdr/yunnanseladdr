package com.sinovate.ngrms.pahdx.svrinter.mapseladdr.util.aws;

import com.sinovate.ngrms.pahdx.svrinter.mapseladdr.util.aws.AddressSegmenterImpl;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * Created by zszhang on 2015/5/20.
 */

public class AddressSegmenterImplTest{

    private static AddressSegmenterImpl addressSegmenter = new AddressSegmenterImpl(null, true);

    @DataProvider(name = "splitAddrData")
    public static Object[][] testSearchData() {
        return new Object[][]{
                new Object[]{null, null},
                new Object[]{"安徽合肥长丰县农村双凤开发区/安徽省汽车工业学校（新校区）", "安徽合肥长丰县 农村 双凤开发区  安徽省汽车工业学校  新校区"},
                new Object[]{"安徽合肥长丰县城区水湖镇/富华嘉园11号楼1，2单元", "安徽合肥长丰县 城 区 水湖镇  富华嘉园 11号 楼 1 2单元"}
        };
    }

    @Test(enabled = true,dataProvider = "splitAddrData")
    public void splitAddr(String addr, String expected) {
        String actual = addressSegmenter.splitAddr(addr);
        Assert.assertEquals(actual,expected);
    }

    @DataProvider(name = "full2HalfData")
    public static Object[][] full2HalfData() {
        return new Object[][]{
                new Object[]{null, null},
                new Object[]{"hello!！ 全角转换，ＤＡＯ ５３２-３２　", "hello   全角转换 DAO 532-32 "}
        };
    }

    @Test(enabled = false,dataProvider = "full2HalfData")
    public void full2Half(String fullStr, String expected) {
        String actual = addressSegmenter.full2Half(fullStr);
        Assert.assertEquals(actual,expected);
    }
}
