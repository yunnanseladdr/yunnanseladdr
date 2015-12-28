package com.sinovate.ngrms.pahdx.svrinter.mapseladdr.das.dao.impl;

import com.sinovate.ngrms.pahdx.svrinter.mapseladdr.das.dao.GetServiceTelDao;
import com.sinovate.ngrms.pahdx.svrinter.mapseladdr.das.dao.GridInfoDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

/**
 * Created by limaple on 11/13/15.
 */
@Repository
public class GetServiceTelDaoImpl implements GetServiceTelDao {
    private static final Logger logger = LoggerFactory.getLogger(GetServiceTelDaoImpl.class);


    @Autowired
    @Qualifier("jdbcTemplate_inv")
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private SimpleSqlSvc simpleSqlSvc;

    public String getServiceTel(Integer gridId) {
        String sql = simpleSqlSvc.getQueryTelByGridIdSql();
        String serviceTel = "";
        try{
            serviceTel = jdbcTemplate.queryForObject(sql, String.class, gridId);
        } catch(Exception e) {
            logger.info("未根据网格ID: " + gridId + "查到装维号码");
        }
        return serviceTel;
    }
}
