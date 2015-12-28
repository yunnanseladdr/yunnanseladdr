package com.sinovate.ngrms.pahdx.svrinter.mapseladdr.das.elastic;

import com.sinovate.ngrms.pahdx.svrinter.addrsearch.das.model.ResAddrBo;
import com.sinovate.ngrms.pahdx.svrinter.mapseladdr.das.dao.RedisDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2015/5/26.
 */
@ContextConfiguration(locations = {"classpath:app-context.xml"})
public class RedisDaoImplTest extends AbstractTestNGSpringContextTests {

    @Autowired
    private RedisDao redisDao;

    @Test(enabled = false)
    public void getAddrByOldIdsTest() {
        List<String> oldIds = new ArrayList<String>();
        oldIds.add("16577810");
        List<ResAddrBo> addrBos = redisDao.getResAddsByIds("551",oldIds);
        int a ;
    }
}
