package com.sinovate.ngrms.pahdx.svrinter.mapseladdr.abl.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.sinovate.ngrms.pahdx.svrinter.mapseladdr.abl.svc.AblNearSearchSvc;
import com.sinovate.ngrms.pahdx.svrinter.mapseladdr.ps.svc.PsNearSearchSvc;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

/**
 * Created by limaple on 15/9/16.
 */

@Component
public class AblNearSearchSvcImpl implements AblNearSearchSvc {
    private final static Logger logger = LoggerFactory.getLogger(AblNearSearchSvc.class);

    @Autowired
    @Qualifier("psNearSearchSvcImpl")
    private PsNearSearchSvc psNearSearchSvc;

    /**
     * 根据区域id和用户输入的临近号码查询对应的六八级地址
     *
     * @param area_eid 区域id
     * @param number 用户输入的业务号码
     *
     */
    public String getNearAddInfo(Integer area_eid, String number) {
        if (area_eid == null || number == null || number.isEmpty()) {
            logger.warn("传入就近号码信息不完整");
            return null;
        }
        return psNearSearchSvc.getNearAddrInfo(area_eid, number);
    }
}
