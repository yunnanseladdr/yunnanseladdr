package com.sinovate.ngrms.pahdx.svrinter.mapseladdr.ps.impl;

import com.sinovate.ngrms.pahdx.svrinter.mapseladdr.ps.commu.ResAddrSchSvc;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.Test;

import java.io.IOException;

/**
 * Created by Administrator on 2015/5/28.
 */
@ContextConfiguration(locations = {"classpath:app-context.xml"})
public class ResAddrSchSvcImplTest extends AbstractTestNGSpringContextTests {
    @Autowired
    private ResAddrSchSvc resAddrSchSvc;

    @Test(enabled = false)
    public void test() {
        try {
            resAddrSchSvc.getResAddr("551", "%兴园小区%", 1, 100, "http://134.64.110.182:9999/service/mboss/route","1001");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
