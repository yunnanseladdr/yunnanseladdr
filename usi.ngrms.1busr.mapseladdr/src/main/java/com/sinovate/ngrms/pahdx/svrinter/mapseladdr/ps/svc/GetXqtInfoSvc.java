package com.sinovate.ngrms.pahdx.svrinter.mapseladdr.ps.svc;

import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * Created by limaple on 10/17/15.
 */
public interface GetXqtInfoSvc {
    Map<String, Object> getXqtInfo(String areaCode, String productCode, String productInstance);
}
