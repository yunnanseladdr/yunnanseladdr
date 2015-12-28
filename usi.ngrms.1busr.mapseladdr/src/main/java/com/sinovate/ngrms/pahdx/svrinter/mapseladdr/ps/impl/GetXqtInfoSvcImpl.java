package com.sinovate.ngrms.pahdx.svrinter.mapseladdr.ps.impl;

import com.sinovate.ngrms.pahdx.svrinter.mapseladdr.das.dao.GetXqtInfoDao;
import com.sinovate.ngrms.pahdx.svrinter.mapseladdr.das.dao.impl.GetXqtInfoDaoImpl;
import com.sinovate.ngrms.pahdx.svrinter.mapseladdr.ps.svc.GetXqtInfoSvc;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * Created by limaple on 10/17/15.
 */
public class GetXqtInfoSvcImpl implements GetXqtInfoSvc {
    @Autowired
    private GetXqtInfoDaoImpl getXqtInfoDao;

    public Map<String, Object> getXqtInfo(String areaCode, String productCode, String productInstance) {
        return getXqtInfoDao.getXqtInfo(areaCode, productCode, productInstance);
    }

}
