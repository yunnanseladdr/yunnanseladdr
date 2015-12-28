package com.sinovate.ngrms.pahdx.svrinter.mapseladdr.das.dao.impl;

import com.sinovate.ngrms.pahdx.svrinter.mapseladdr.das.dao.GetProjInfoDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by limaple on 11/5/15.
 *
 */
@Repository
public class GetProjInfoDaoImpl implements GetProjInfoDao {
    private static final Logger logger = LoggerFactory.getLogger(DistributeGridDaoImpl.class);

    @Autowired
    @Qualifier("jdbcTemplate_inv")
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private SimpleSqlSvc simpleSqlSvc;

    public List<String> getProjInfo(String areaCode, Integer gridId) {
        List<String> projInfo = new ArrayList<String>();

        try{
            jdbcTemplate.query(simpleSqlSvc.getQueryProjInfoByGridIdSql(), new Object[]{gridId}, new RowCallbackHandler() {
                @Override
                public void processRow(ResultSet rs) throws SQLException {
                    projInfo.add(" " + ((rs.getString("itemname") == null)?"":rs.getString("itemname")) + "|| " + ((rs.getString("projcoverarea") == null)?"":rs.getString("projcoverarea")) + "|| " + ((rs.getString("interior_finish_time") == null)?"":rs.getString("interior_finish_time")));
                }
            });
        }catch (Exception e) {
            logger.error("获取工程信息失败");
        }

        return projInfo;
    }

    public List<String> getProjInfo2(String areaId, String addrId) {

        Integer gridId = 0;
        try {
            gridId = jdbcTemplate.queryForObject(simpleSqlSvc.getQueryGridIdByAddrIdsql(), Integer.class, areaId, addrId, areaId, addrId);
            logger.info("该地址ID对应的网格ID为: " + gridId);
        } catch (Exception e) {
            logger.info("根据地址ID查询网格ID失败");
            return null;
        }

        List<String> projInfo = new ArrayList<String>();

        try{
            jdbcTemplate.query(simpleSqlSvc.getQueryProjInfoByGridIdSql(), new Object[]{gridId}, new RowCallbackHandler() {
                @Override
                public void processRow(ResultSet rs) throws SQLException {
                    projInfo.add(" " + ((rs.getString("itemname") == null)?"":rs.getString("itemname")) + "|| " + ((rs.getString("projcoverarea") == null)?"":rs.getString("projcoverarea")) + "|| " + ((rs.getString("interior_finish_time") == null)?"":rs.getString("interior_finish_time")));
                }
            });
        } catch (Exception e) {
            logger.error("获取工程信息失败");
        }

        return projInfo;
    }
}
