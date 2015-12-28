package com.sinovate.ngrms.pahdx.svrinter.mapseladdr.das.dao;

import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by limaple on 11/5/15.
 */
@Service
public interface GetProjInfoDao {
    List<String> getProjInfo(String areaCode, Integer gridId);
    List<String> getProjInfo2(String areaId, String addrId);
}
