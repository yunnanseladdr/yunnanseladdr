package com.sinovate.ngrms.pahdx.svrinter.mapseladdr.das.dao.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

/**
 * Created by limaple on 10/17/15.
 */
@Repository
public class GetXqtInfoDaoImpl {

    @Autowired
    @Qualifier("jdbcTemplate_inv")
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private SimpleSqlSvc simpleSqlSvc;

    public Map<String, Object> getXqtInfo(String areaCode, String productCode, String productInstance) {

        return jdbcTemplate.queryForMap(simpleSqlSvc.getQueryXqtInfoSql(), productInstance, productCode, areaCode);

    }

}
