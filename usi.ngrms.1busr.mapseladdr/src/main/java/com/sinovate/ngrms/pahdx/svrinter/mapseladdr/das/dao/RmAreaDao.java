package com.sinovate.ngrms.pahdx.svrinter.mapseladdr.das.dao;

import com.sinovate.ngrms.pahdx.svrinter.mapseladdr.abl.bo.SubAreaBo;
import com.sinovate.ngrms.pahdx.svrinter.mapseladdr.abl.bo.AreaBo;

import java.math.BigInteger;
import java.util.List;

/**
 * @author tu.chenghong@ustcinfo.com
 * @单位名称：科大国创—电信资源事业部 USTC Sinovate Software Co.,Ltd.
 * Copyright (c) 2014 All Rights Reserved.
 * @系统名称：NGRMS—下一代网络资源管理系统
 * @工程名称：usi.ngrms.allparent
 * @文件名称:
 * @类路径: com.sinovate.ngrms.pahdx.svrinter.seladdr.das.dao.impl
 * @date 2015-05-21 09:34
 * @desc  查询区域、子区域、乡镇街道
 * @see
 */
public interface RmAreaDao {

    /**
     * 查询区域信息
     * @param
     * @return
     */
    public List<AreaBo> queryArea();

    /**
     * 查询子区域信息
     * @param areaId
     * @param areaName
     * @return
     */
    public List<SubAreaBo> querySubArea(BigInteger areaId,String areaName);

}
