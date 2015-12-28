package com.sinovate.ngrms.pahdx.svrinter.mapseladdr.abl.svc;

/**
 * Created by limaple on 15/9/16.
 */
public interface AblNearSearchSvc {

    /**
     * 根据区域id和用户输入的临近号码查询对应的六八级地址
     *
     * @param area_eid 区域id
     * @param number 用户输入的业务号码
     *
     */
    public String getNearAddInfo(Integer area_eid, String number);
}
