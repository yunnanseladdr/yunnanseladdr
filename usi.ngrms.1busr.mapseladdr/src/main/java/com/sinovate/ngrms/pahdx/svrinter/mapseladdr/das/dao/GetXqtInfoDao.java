package com.sinovate.ngrms.pahdx.svrinter.mapseladdr.das.dao;

import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * Created by limaple on 10/17/15.
 */
@Component
public interface GetXqtInfoDao {
    Map<String, Object> getXqtInfo(String areaCode, String productCode, String productInstance);
}
