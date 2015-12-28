package com.sinovate.ngrms.pahdx.svrinter.mapseladdr.das.dao.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.sinovate.ngrms.pahdx.svrinter.mapseladdr.das.dao.GridInfoDao;
import com.sinovate.ngrms.pahdx.svrinter.mapseladdr.das.dao.NearSearchDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by limaple on 15/9/16.
 */
@Repository
public class NearSearchDaoImpl implements NearSearchDao {
    private static final Logger logger = LoggerFactory.getLogger(GridInfoDao.class);

    @Autowired
    @Qualifier("jdbcTemplate_inv")
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private SimpleSqlSvc simpleSqlSvc;

    @Override
    public String getNearAddrInfo(Integer area_eid, String number) {
        if (area_eid == null || number == null || number.isEmpty()) {
            logger.warn("传入就近号码信息不完整");
            return null;
        }
        String queryNearGrid = simpleSqlSvc.getQueryNearGridSql();

        String add = jdbcTemplate.query(queryNearGrid, new Object[]{area_eid, area_eid, area_eid, number, area_eid}, new ResultSetExtractor<String>() {
            @Override
            public String extractData(ResultSet resultSet) throws SQLException, DataAccessException {
                if (resultSet.next()) {
                    return resultSet.getString("chinese_character_match") + "+++" + resultSet.getInt("mme_eid");
                }
                return null;
            }
        });


        logger.info("就近号码获取用户地址名称为: " + add);
        return ((add == null)?"":add);
    }
}
