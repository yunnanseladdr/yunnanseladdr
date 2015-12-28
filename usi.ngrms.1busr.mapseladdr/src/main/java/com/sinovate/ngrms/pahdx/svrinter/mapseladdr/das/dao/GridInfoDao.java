package com.sinovate.ngrms.pahdx.svrinter.mapseladdr.das.dao;

import com.sinovate.ngrms.pahdx.svrinter.mapseladdr.aui.vo.GridVo;

import java.util.List;

/**
 * Created by limaple on 15/8/12.
 */
public interface GridInfoDao {
    void getGridCharacter();

    List<Integer> matchedGridIds(Double lng, Double lat);

    GridVo queryGrid(Integer wangge_id);

    String get68Ad(Integer wangge_id);
}
