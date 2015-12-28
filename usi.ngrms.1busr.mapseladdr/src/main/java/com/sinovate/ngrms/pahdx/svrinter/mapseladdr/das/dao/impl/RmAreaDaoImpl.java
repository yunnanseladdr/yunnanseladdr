package com.sinovate.ngrms.pahdx.svrinter.mapseladdr.das.dao.impl;

import com.sinovate.ngrms.pahdx.svrinter.mapseladdr.abl.bo.SubAreaBo;
import com.sinovate.ngrms.pahdx.svrinter.mapseladdr.abl.bo.AreaBo;
import com.sinovate.ngrms.pahdx.svrinter.mapseladdr.abl.bo.StreetBo;
import com.sinovate.ngrms.pahdx.svrinter.mapseladdr.das.dao.RmAreaDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.lang.Object;
import java.math.BigInteger;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author tu.chenghong@ustcinfo.com
 * @单位名称：科大国创—电信资源事业部 USTC Sinovate Software Co.,Ltd.
 * Copyright (c) 2014 All Rights Reserved.
 * @系统名称：NGRMS—下一代网络资源管理系统
 * @工程名称：usi.ngrms.allparent
 * @文件名称:
 * @类路径: com.sinovate.ngrms.pahdx.svrinter.seladdr.das.dao.impl
 * @date 2015-05-21 09:49
 * @desc
 * @see
 */
@Component
public class RmAreaDaoImpl implements RmAreaDao{
    private static final Logger logger = LoggerFactory.getLogger(RmAreaDaoImpl.class);

    @Autowired
    @Qualifier("jdbcTemplate_inv")
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private SimpleSqlSvc simpleSqlSvc;

    /**
     * 查询区域信息
     *
     * @return
     */
    @Override
    public List<AreaBo> queryArea() {
        String sql =simpleSqlSvc.getQRY_AREA_INFO1();
        if(null == sql && sql.equals("")){
            logger.warn("查询区域的sql为空，无法查询!");
            return null;
        }
        List<AreaBo> areaBoList = null;
        try {
            areaBoList = jdbcTemplate.query(sql, new RowMapper<AreaBo>() {
                @Override
                public AreaBo mapRow(ResultSet rs, int i) throws SQLException {
                    AreaBo areaBo = new AreaBo();
                    if(null != rs.getString("id") && null !=rs.getString("name")
                            && null!=rs.getString("code")){
                        areaBo.setId(new BigInteger(rs.getString("id")));
                        areaBo.setName(rs.getString("name"));
                        areaBo.setCode(rs.getString("code"));
                        areaBo.setOldId(rs.getString("oldId"));
                        return areaBo;
                    }
                    return null;
                }
            });
        } catch (DataAccessException e) {
            e.printStackTrace();
            logger.debug(e.toString());
        }
        logger.info("查询到的区域的个数:"+areaBoList.size()+"############");
        return areaBoList;
    }

    /**
     * 查询子区域信息及下面的乡镇
     *
     * @param areaId
     * @param areaName
     * @return
     */
    @Override
    public List<SubAreaBo> querySubArea(BigInteger areaId,String areaName) {
        if(null == areaId || null == areaName || areaName.isEmpty()){
            logger.warn("区域信息不全，无法查询子区域信息!");
            return null;
        }
        String sql = simpleSqlSvc.getQRY_SUBAREA_INFO1();
        if(null == sql && sql.equals("")){
            logger.warn("查询区域的sql为空，无法查询!");
            return null;
        }
        List<SubAreaBo> subAreaBoList = null;
        List<SubAreaBo> subAreaBoLists = new ArrayList<>();


        try {
            String  parent_id = areaId.toString();
            subAreaBoList = jdbcTemplate.query(sql,new Object[] {parent_id}, new RowMapper<SubAreaBo>() {
                @Override
                public SubAreaBo mapRow(ResultSet rs, int i) throws SQLException {
                    SubAreaBo subAreaBo = new SubAreaBo();
                    String name = rs.getString("name");
//                    logger.info(name+"#############");
                    if(null == name){
                        return null;
                    }
                    String rename = name.substring(0, name.length() - 1);
                    if (rename.equals(areaName) && name.substring(name.length()-1, name.length()).equals("市") && !rename.equals("黄山") && !rename.equals("池州") && !rename.equals("宣城")) {
                        logger.info("获得的中心市的名称为："+rename+"###########");
                        String sql1 = simpleSqlSvc.getQRY_QU_INFO1();
                        if(null == sql1 && sql1.equals("")){
                            logger.warn("查询区域的sql为空，无法查询!");
                            return null;
                        }
                        String sub_area_id = rs.getString("id");
                        if (null != sub_area_id) {
                            List<SubAreaBo> subAreaBoList1 = null;
                            subAreaBoList1 = jdbcTemplate.query(sql1, new Object[] {sub_area_id}, new RowMapper<SubAreaBo>() {
                                @Override
                                public SubAreaBo mapRow(ResultSet rs, int i) throws SQLException {
                                    SubAreaBo subAreaBo = new SubAreaBo();
                                    if(null != rs.getString("id") && null !=rs.getString("name")){
                                        subAreaBo.setId(new BigInteger(rs.getString("id")));
                                        subAreaBo.setName(rs.getString("name"));
                                        logger.info("区： " + rs.getString("name"));
                                        subAreaBo.setCode(rs.getString("code"));
                                        subAreaBo.setOldId(rs.getString("oldId"));
                                        List<StreetBo> streets = getStreetTown(new BigInteger(sub_area_id),new BigInteger(rs.getString("id")));
                                        if(null != streets && streets.size() > 0){
                                            subAreaBo.setM_StreetBo(streets);
                                        }


                                    }
                                    return subAreaBo;
                                }
                            });
                            logger.info("查到的区个数："+subAreaBoList1.size());
                            if(null != subAreaBoList1 && subAreaBoList1.size() > 0){
                                subAreaBoLists.addAll(subAreaBoList1);
                            }
                        }
                        return null;
                    } else {
                        if(null != rs.getString("id") && null !=rs.getString("name")
                                && null!=rs.getString("code")){
//                            logger.info(rs.getString("id")+"#####$$$$$$$$$$$$$$$$$$");
                            BigInteger b = new BigInteger(rs.getString("id"));
                            subAreaBo.setId(b);
                            subAreaBo.setName(rs.getString("name"));
                            subAreaBo.setCode(rs.getString("code"));
                            subAreaBo.setOldId(rs.getString("oldId"));
                            List<StreetBo> streets = getStreetTown(null,new BigInteger(rs.getString("id")));
                            if(null != streets && streets.size() > 0){
                                subAreaBo.setM_StreetBo(streets);
                            }
                            return subAreaBo;
                        }

                    }

                    return subAreaBo;
                }
            });
            if(null != subAreaBoList && subAreaBoList.size() > 0){
                subAreaBoLists.addAll(subAreaBoList);
            }
        } catch (DataAccessException e) {
            e.printStackTrace();
            logger.debug(e.toString());
        }
        for (SubAreaBo subAreaBo: subAreaBoList) {
            if (null == subAreaBo)
                subAreaBoLists.remove(subAreaBo);
            else if (null == subAreaBo.getName())
                subAreaBoLists.remove(subAreaBo);
        }
        logger.info("查询到的子区域的个数:"+subAreaBoLists.size()+"############");
        return subAreaBoLists;
    }

    private List<StreetBo> getStreetTown(BigInteger subareaId, BigInteger quId) {

        List<StreetBo> streetBoList = null;

        if(null == subareaId) {
            // 查找县下面的乡镇
            String sql = simpleSqlSvc.getQRY_STRTOWN_INFO1();
            if(null == sql && sql.isEmpty()){
                logger.warn("查询区域的sql为空，无法查询!");
                return null;
            }

            try {
                String  sub_area_id = quId.toString();
                streetBoList = jdbcTemplate.query(sql,new Object[] {sub_area_id} ,new RowMapper<StreetBo>() {
                    @Override
                    public StreetBo mapRow(ResultSet rs, int i) throws SQLException {

                        StreetBo streetBo = new StreetBo();
                        if(null != rs.getString("id") && null !=rs.getString("name")){
                            String id = rs.getString("id");
                            streetBo.setId(new BigInteger(id));
                            streetBo.setName(rs.getString("name"));
                            streetBo.setCode(rs.getString("code"));
                            streetBo.setOldId(rs.getString("oldId"));
                        }

                        return streetBo;
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
                logger.debug(e.toString());
            }
        } else{
            // 查找区下面的乡镇
            String sql1 = simpleSqlSvc.getQRY_STRTOWM_INFO2();
            if(null == sql1 && sql1.isEmpty()){
                logger.warn("查询区域的sql为空，无法查询!");
                return null;
            }

            try {
                String  sub_area_id = subareaId.toString();
                String parent_id = quId.toString();
                streetBoList = jdbcTemplate.query(sql1,new Object[]{sub_area_id,parent_id} ,new RowMapper<StreetBo>() {
                    @Override
                    public StreetBo mapRow(ResultSet rs, int i) throws SQLException {

                        StreetBo streetBo = new StreetBo();
                        String id = rs.getString("id");
                        streetBo.setId(new BigInteger(id));
                        streetBo.setName(rs.getString("name"));
                        streetBo.setCode(rs.getString("code"));
                        streetBo.setOldId(rs.getString("oldId"));
                        return streetBo;
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
                logger.debug(e.toString());
            }
        }
        logger.info("查询到的街道的个数:"+streetBoList.size()+"############");
        return streetBoList;
    }
}
