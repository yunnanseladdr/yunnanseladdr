package com.sinovate.ngrms.pahdx.svrinter.mapseladdr.ps.svc;

import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by limaple on 11/5/15.
 * 工程信息查询
 */
@Service
public interface GetProjInfoSvc {
    List<String> getProjInfo(String areaCode, Integer gridId);
    List<String> getProjInfo2(String areaId, String addrId);
}
