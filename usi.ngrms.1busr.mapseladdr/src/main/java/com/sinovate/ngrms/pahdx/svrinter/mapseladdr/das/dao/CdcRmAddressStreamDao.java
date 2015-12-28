package com.sinovate.ngrms.pahdx.svrinter.mapseladdr.das.dao;


import com.sinovate.ngrms.pahdx.svrinter.mapseladdr.abl.bo.CdcRmAddrBo;

/**
 * 增量变化数据的流式Dao实现
 * @author shen
 * @version 1.0
 * @created 18-五月-2015 13:36:55
 */
public interface CdcRmAddressStreamDao {


    /**
     * 获取不同地市的变化表中的变化数据，并交由handler处理
     * @param handler
     */
    public void procAllChangeAddr(StreamHandler<CdcRmAddrBo> handler);

}