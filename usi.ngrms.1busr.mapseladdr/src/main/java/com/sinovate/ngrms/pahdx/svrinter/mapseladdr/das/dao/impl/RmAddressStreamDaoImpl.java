package com.sinovate.ngrms.pahdx.svrinter.mapseladdr.das.dao.impl;

import com.sinovate.ngrms.pahdx.svrinter.mapseladdr.abl.bo.CdcRmAddrBo;
import com.sinovate.ngrms.pahdx.svrinter.mapseladdr.das.dao.RmAddressStreamDao;
import com.sinovate.ngrms.pahdx.svrinter.mapseladdr.das.dao.StreamHandler;
import com.sinovate.ngrms.pahdx.svrinter.mapseladdr.das.dao.StreamReadAllBaseDao;
import com.sinovate.ngrms.pahdx.svrinter.mapseladdr.ps.svc.AdminDivisionSvc;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigInteger;


/**
 * Created by zszhang on 2015/5/19.
 */
@Component
public class RmAddressStreamDaoImpl implements RmAddressStreamDao {
    private final static Logger logger = LoggerFactory.getLogger(RmAddressStreamDaoImpl.class);

    @Autowired
    private StreamReadAllBaseDao<CdcRmAddrBo> cdcRmAddrBoStreamReadAllBaseDao;

    @Autowired
    private SimpleSqlSvc simpleSqlSvc;

    @Autowired
    private AdminDivisionSvc adminDivisionSvc;

    @Override
    public void procAllSuggAddr(String areaCode,StreamHandler<CdcRmAddrBo> handler) {
        BigInteger region_id = adminDivisionSvc.getAreaId(areaCode);
        if(null != region_id) {
            cdcRmAddrBoStreamReadAllBaseDao.query(simpleSqlSvc.getQRY_89ADDR_NAME(), region_id, handler, CdcRmAddrBo.class);
        }
    }

    @Override
    public void procAllUserAddr(String areaCode,StreamHandler<CdcRmAddrBo> handler) {
        BigInteger region_id = adminDivisionSvc.getAreaId(areaCode);
        if(null != region_id){
            cdcRmAddrBoStreamReadAllBaseDao.query(simpleSqlSvc.getQRY_ADDR_NOSUB(), region_id,handler, CdcRmAddrBo.class);
        }

    }
}
