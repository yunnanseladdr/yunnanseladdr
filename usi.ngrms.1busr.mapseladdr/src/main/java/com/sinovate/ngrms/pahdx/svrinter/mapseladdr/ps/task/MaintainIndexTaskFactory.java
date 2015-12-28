package com.sinovate.ngrms.pahdx.svrinter.mapseladdr.ps.task;

import com.sinovate.ngrms.pahdx.svrinter.mapseladdr.ps.svc.BuildFullTextSvc;
import com.sinovate.ngrms.pahdx.svrinter.mapseladdr.abl.bo.CdcBaseBo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Created by zszhang on 2015/5/22.
 */
@Component
public class MaintainIndexTaskFactory<T extends CdcBaseBo> {
    @Autowired
    private BuildFullTextSvc<T> buildFullTextSvc;

    public MaintainIndexTaskFactory() {
    }

    public MaintainIndexTask<T> getTaskBean(final List<T> data, int action, String areaCode) {
        return new MaintainIndexTask<T>(this.buildFullTextSvc, data, action,areaCode);
    }
}
