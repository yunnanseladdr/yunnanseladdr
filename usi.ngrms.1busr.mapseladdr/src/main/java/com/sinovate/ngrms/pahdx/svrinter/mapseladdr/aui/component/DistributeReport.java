package com.sinovate.ngrms.pahdx.svrinter.mapseladdr.aui.component;

import com.sinovate.ngrms.pahdx.svrinter.mapseladdr.ps.svc.DistributeReportSvc;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;

/**
 * Created by limaple on 11/27/15.
 * 下发产品和设备网格单元[完全/部分]不一致数据报表
 */
public class DistributeReport {
    @Autowired
    private DistributeReportSvc distributeReportSvc;

    public void run() {
        distributeReportSvc.distrCompletelyNotMatch();
        distributeReportSvc.distrPartlyNotMatch();
    }

}
