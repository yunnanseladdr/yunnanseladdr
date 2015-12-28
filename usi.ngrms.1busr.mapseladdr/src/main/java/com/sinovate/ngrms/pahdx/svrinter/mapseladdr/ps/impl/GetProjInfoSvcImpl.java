package com.sinovate.ngrms.pahdx.svrinter.mapseladdr.ps.impl;

import com.sinovate.ngrms.pahdx.svrinter.mapseladdr.das.dao.GetProjInfoDao;
import com.sinovate.ngrms.pahdx.svrinter.mapseladdr.das.dao.impl.GetProjInfoDaoImpl;
import com.sinovate.ngrms.pahdx.svrinter.mapseladdr.ps.svc.GetProjInfoSvc;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by limaple on 11/5/15.
 */
@Service
public class GetProjInfoSvcImpl implements GetProjInfoSvc {

    @Autowired
    private GetProjInfoDaoImpl getProjInfoDao;

    public List<String> getProjInfo(String areaCode, Integer gridId) {
        return getProjInfoDao.getProjInfo(areaCode, gridId);
    }

    public List<String> getProjInfo2(String areaId, String addrId) {
        return getProjInfoDao.getProjInfo2(areaId, addrId);
    }

}
