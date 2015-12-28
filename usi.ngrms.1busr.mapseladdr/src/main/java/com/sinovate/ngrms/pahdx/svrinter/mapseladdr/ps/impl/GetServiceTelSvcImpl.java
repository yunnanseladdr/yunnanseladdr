package com.sinovate.ngrms.pahdx.svrinter.mapseladdr.ps.impl;

import com.sinovate.ngrms.pahdx.svrinter.mapseladdr.das.dao.impl.GetServiceTelDaoImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by limaple on 11/13/15.
 */
@Service
public class GetServiceTelSvcImpl {
    @Autowired
    private GetServiceTelDaoImpl getServiceTelDao;

    public String getServiceTel(Integer gridId) {
        return getServiceTelDao.getServiceTel(gridId);
    }
}
