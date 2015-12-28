package com.sinovate.ngrms.pahdx.svrinter.mapseladdr.das.dao;

import com.sinovate.ngrms.pahdx.svrinter.addrsearch.das.model.ResAddrBo;

import java.util.List;

/**
 * Created by Administrator on 2015/5/26.
 */
public interface RedisDao {

    /**
     * 根据oldIds获取redis中的信息
     * @param areaCode 区域编码
     * @param oldIds 地址oldid
     * @return
     */
    List<ResAddrBo> getResAddsByIds(String areaCode, List<String> oldIds);

}
