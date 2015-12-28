package com.sinovate.ngrms.pahdx.svrinter.mapseladdr.das.dao.impl;

import com.sinovate.ngrms.pahdx.svrinter.mapseladdr.aui.vo.GridVo;
import com.sinovate.ngrms.pahdx.svrinter.mapseladdr.das.dao.GridInfoDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by limaple on 15/8/12.
 */
@Repository
public class GridInfoDaoImpl implements GridInfoDao {

    private static final Logger logger = LoggerFactory.getLogger(GridInfoDao.class);

    @Autowired
    @Qualifier("jdbcTemplate_inv")
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private SimpleSqlSvc simpleSqlSvc;

    public void getGridCharacter() {
        String sql = simpleSqlSvc.getQueryGridInfoSql();
        if (null == sql || sql.equals("")) {
            logger.warn("查询网格表的sql为空，无法查询!");
            return;
        }
        logger.info("正在建立网格特征表(约n分钟)...");
        jdbcTemplate.query(sql, rs -> {
            Integer grid_id = rs.getInt("wangge_id");
            String polygon_str = rs.getString("wangge_zuobiao");

            logger.info("---" + grid_id);
            Double lng, lat;

            String[] polygons;
            if (polygon_str.contains("GON")) {
                polygons = polygon_str.split("GON")[1].split("\\)\\),\\(\\(");
            } else {
                polygons = polygon_str.split("\\)\\),\\(\\(");
            }

            for(String polygon: polygons) {
                String[] polygon_ins = polygon.split("\\),\\(");
                for (String polygon_in: polygon_ins) {
                    String[] pointStrs = polygon_in.replace('(', ' ').replace(')', ' ').trim().split(", ");
                    for (String pointStr: pointStrs) {
                        lng = Double.parseDouble(pointStr.split(" ")[0]);
                        lat = Double.parseDouble(pointStr.split(" ")[1]);
                        if (jdbcTemplate.queryForList("select 1 from grid_points where grid_id = ? and lng = ? and lat = ?",
                                new Object[]{grid_id, lng, lat}).size() == 0) {
                            jdbcTemplate.update("insert into grid_points values (?, ?, ?)",
                                    new Object[]{grid_id, lng, lat});
                        }

                    }
                }
            }
        });

        logger.info("...END");
    }

    public List<Integer> matchedGridIds(Double lng, Double lat) {
        double size = 0.004;
        List<Integer> list = new ArrayList<>();
        while (size < 0.05) {
            logger.info("size = " + size);
            list = jdbcTemplate.queryForList(
                    "select distinct grid_id from grid_points where lng BETWEEN ? and ? and lat BETWEEN ? and ?",
                    new Object[]{lng - size, lng + size, lat - size, lat + size},
                    Integer.class
            );
            if (list.size() > 5) {
                return list;
            }
            size += 0.004;
        }
        return list;
    }

    public GridVo queryGrid(Integer wangge_id) {
        return jdbcTemplate.queryForObject(
                "select wangge_dalei, wangge_name, wangge_zuobiao, subarea_name, area_eid from wangge_unit where wangge_id = ? and rownum < 2",
                new Object[]{wangge_id},
                (rs, index) -> {
                    GridVo gridVo = new GridVo();
                    gridVo.setGridType(rs.getString("wangge_dalei"));
                    gridVo.setGridName(rs.getString("wangge_name"));
                    gridVo.setGridPolygon(rs.getString("wangge_zuobiao"));
                    gridVo.setSubareaName(rs.getString("subarea_name"));
                    gridVo.setAreaId(rs.getString("area_eid"));
                    return gridVo;
                }

        );
    }

    public String get68Ad(Integer wangge_id) {
        return jdbcTemplate.queryForObject(
                "select address_name, upaddress_name from wangge_address where wangge_id = ?",
                new Object[]{wangge_id},
                (rs, index) -> {
                    if (rs.getString("upaddress_name") == null || rs.getString("upaddress_name").isEmpty()) {
                        return rs.getString("address_name");
                    } else {
                        return rs.getString("upaddress_name") + rs.getString("address_name");
                    }
                }
        );
    }
}
