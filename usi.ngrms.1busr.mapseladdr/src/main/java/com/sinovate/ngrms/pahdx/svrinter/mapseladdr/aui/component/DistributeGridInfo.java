package com.sinovate.ngrms.pahdx.svrinter.mapseladdr.aui.component;

import com.sinovate.ngrms.pahdx.svrinter.mapseladdr.das.dao.DistributeGridDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

/**
 * Created by limaple on 10/23/15.
 * 功能: 网格详单数据下发本地网
 * ftp: 134.64.160.38:8082
 * 文件名: wg日期.txt(wg20150707.txt)
 * 文件编码: utf-8
 * 文件格式: 每行一条数据, 字段以分号(;)隔开
 */
@Service
public class DistributeGridInfo {
    @Autowired
    private DistributeGridDao distributeGridDao;

    public void run() {
        distributeGridDao.distribute();
    }
}
