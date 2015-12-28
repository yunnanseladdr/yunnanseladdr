package com.sinovate.ngrms.pahdx.svrinter.mapseladdr.ps.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.sinovate.ngrms.pahdx.svrinter.mapseladdr.das.dao.NearSearchDao;
import com.sinovate.ngrms.pahdx.svrinter.mapseladdr.ps.svc.PsNearSearchSvc;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by limaple on 15/9/16.
 */
@Service
public class PsNearSearchSvcImpl implements PsNearSearchSvc {

    @Autowired
    private NearSearchDao nearSearchDao;
    /**
     * 根据区域id和用户输入的临近号码查询对应的六八级地址
     *
     * @param area_eid 区域id
     * @param number 用户输入的业务号码
     *
     */
    public String getNearAddrInfo(Integer area_eid, String number) {
        return nearSearchDao.getNearAddrInfo(area_eid, number);
    }
}
