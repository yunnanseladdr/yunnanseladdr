package com.sinovate.ngrms.pahdx.svrinter.mapseladdr.ps.impl;

import com.sinovate.ngrms.pahdx.svrinter.mapseladdr.abl.bo.CdcBaseBo;
import com.sinovate.ngrms.pahdx.svrinter.mapseladdr.abl.bo.CdcRmAddrBo;
import com.sinovate.ngrms.pahdx.svrinter.mapseladdr.das.dao.StreamHandler;
import com.sinovate.ngrms.pahdx.svrinter.mapseladdr.das.dao.impl.StreamReadAllBaseDaoImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;

/**
 * Created by Administrator on 2015/6/8.
 */
@ContextConfiguration(locations = {"classpath:app-context.xml"})
public class StreamReadAllBaseDaoImplTest extends AbstractTestNGSpringContextTests {

    @Autowired
    private StreamReadAllBaseDaoImpl streamReadAllBaseDao;

    @Autowired
    private StreamHandler<CdcRmAddrBo> streamHandler;

    @org.testng.annotations.Test(enabled = false)
    public void TestQery() {
        streamReadAllBaseDao.query("select ? tableName from rm_address where id <= ?","UserAddr","381031000000000040312438",streamHandler,CdcBaseBo.class);
    }

}
