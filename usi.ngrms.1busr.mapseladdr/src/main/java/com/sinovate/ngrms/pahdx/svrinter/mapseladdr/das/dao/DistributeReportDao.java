package com.sinovate.ngrms.pahdx.svrinter.mapseladdr.das.dao;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.stereotype.Repository;

import java.io.*;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;

/**
 * Created by limaple on 11/27/15.
 * 下发产品和设备网格单元[完全/部分]不一致数据报表
 */
@Repository
public class DistributeReportDao {
    private static final Logger logger = LoggerFactory.getLogger(DistributeReportDao.class);

    private FileWriter fw;
    private int loop;


    @Autowired
    @Qualifier("jdbcTemplate_d19")
    private JdbcTemplate jdbcTemplate;

    public void writeCompletelyFile(String fileFullName) {
        loop = 0;
        try {
            fw = new FileWriter(fileFullName, true);
            fw.append("区域名称\t子区域名称\t产品关联的网格单元id\t产品关联网格单元名称\t产品关联网格单元属性\t产品关联营业部\t产品id\t产品状态\t")
                    .append("产品编码\t产品实例\t业务号码\t电话号码\t选址结果ID\t装机地址名称\t标准地址id\t标准地址名称\t所属8级地址id\t所属8级地址名称\t")
                    .append("所属6级地址id\t所属6级地址名称\t地址关联的网格单元id\t地址关联的网格单元名称\t产品接入方式\t电路id\t设备id\t设备集团名称\t")
                    .append("设备类型\t电路类型\t设备关联的网格单元id\t设备关联的网格单元名称\t产品创建时间\t所属父产品id\t产品id\t营业部名称\t")
                    .append("设备所属子区域id\t设备所属子区域名称\t设备所属营业部名称\t设备类型名称\t是否一致\r\n");
        } catch (IOException e) {
            logger.error("file write error");
            try {
                fw.close();
            } catch (IOException exception) {
                exception.printStackTrace();
            }
            return;
        }

        jdbcTemplate.query("select t.region_name        区域名称," +
                "t.subarea_name       子区域名称," +
                "t.gwm_fid            产品关联的网格单元id," +
                "t.grid_name          产品关联网格单元名称," +
                "t.grid_info          产品关联网格单元属性," +
                "t.yyb                产品关联营业部," +
                "t.product_eid        产品id," +
                "t.product_status     产品状态," +
                "t.productcode        产品编码," +
                "t.productinstance    产品实例," +
                "t.servicecode        业务号码," +
                "(select cp.pstncode from c_project cp where cp.area_eid=t.telemt_code and cp.mme_eid=t.product_eid and cp.entitytype_eid=110001) 电话号码," +
                "t.addressresult_eid  选址结果ID," +
                "t.fullname           装机地址名称," +
                "t.address_eid        标准地址id," +
                "t.address_name       标准地址名称," +
                "t.address_eid8       所属8级地址id," +
                "t.address_name8      所属8级地址名称," +
                "t.address_eid6       所属6级地址id," +
                "t.address_name6      所属6级地址名称," +
                "t.grid_adfid         地址关联的网格单元id," +
                "t.grid_adname        地址关联的网格单元名称," +
                "t.accesstype         产品接入方式," +
                "t.circuit_eid        电路id," +
                "t.node_eid           设备id," +
                "t.node_gname         设备集团名称," +
                "t.node_type          设备类型," +
                "t.circuit_type       电路类型," +
                "t.grid_nofid         设备关联的网格单元id," +
                "t.grid_noname        设备关联的网格单元名称," +
                "t.create_date        产品创建时间," +
                "t.projectparent_eid  所属父产品id," +
                "t.project_eidzx      产品id," +
                "t.yyb_name           营业部名称," +
                "t.nodesubarea_eid    设备所属子区域id," +
                "t.nodesubarea_name   设备所属子区域名称," +
                "t.nodeyyb_name       设备所属营业部名称," +
                "t.nodetype_name      设备类型名称," +
                "t.is_consistent      是否一致 " +
                "from project_wangge t " +
                "where exists (select 1 from project_wangge_total13 p where p.node_wangge_cn = p.byzpnwanggecn and p.node_eid = t.node_eid) " +
                "and t.gwm_fid <> t.grid_nofid", new RowCallbackHandler() {
            @Override
            public void processRow(ResultSet rs) throws SQLException {
                try {
                    fw.append(rs.getString(1)).append("\t").append(rs.getString(2)).append("\t").append(rs.getString(3)).append("\t")
                            .append(rs.getString(4)).append("\t").append(rs.getString(5)).append("\t").append(rs.getString(6)).append("\t")
                            .append(rs.getString(7)).append("\t").append(rs.getString(8)).append("\t").append(rs.getString(9)).append("\t")
                            .append(rs.getString(10)).append("\t").append(rs.getString(11)).append("\t").append(rs.getString(12)).append("\t")
                            .append(rs.getString(13)).append("\t").append(rs.getString(14)).append("\t").append(rs.getString(15)).append("\t")
                            .append(rs.getString(16)).append("\t").append(rs.getString(17)).append("\t").append(rs.getString(18)).append("\t")
                            .append(rs.getString(19)).append("\t").append(rs.getString(20)).append("\t").append(rs.getString(21)).append("\t")
                            .append(rs.getString(22)).append("\t").append(rs.getString(23)).append("\t").append(rs.getString(24)).append("\t")
                            .append(rs.getString(25)).append("\t").append(rs.getString(26)).append("\t").append(rs.getString(27)).append("\t")
                            .append(rs.getString(28)).append("\t").append(rs.getString(29)).append("\t").append(rs.getString(30)).append("\t")
                            .append(rs.getString(31)).append("\t").append(rs.getString(32)).append("\t").append(rs.getString(33)).append("\t")
                            .append(rs.getString(34)).append("\t").append(rs.getString(35)).append("\t").append(rs.getString(36)).append("\t")
                            .append(rs.getString(37)).append("\t").append(rs.getString(38)).append("\t").append(rs.getString(39)).append("\r\n");

                    ++loop;
                    if (loop % 40000 == 0) {
                        fw.close();
                        fw = new FileWriter(fileFullName, true);
                        logger.info("  -- 写入数据量: " + loop);
                    }

                } catch (IOException e) {
                    logger.error("file write error");
                }
            }
        });

        logger.info("共写入数据量: " + loop);

        try {
            fw.close();
        } catch (IOException e) {
            logger.error("FileWrite close error!!!!!!!");
        }
    }

    public void writePartlyFile(String fileFullName) {
        loop = 0;
        try {
            fw = new FileWriter(fileFullName, true);
            fw.append("区域名称\t子区域名称\t产品关联的网格单元id\t产品关联网格单元名称\t产品关联网格单元属性\t产品关联营业部\t产品id\t产品状态\t")
                    .append("产品编码\t产品实例\t业务号码\t电话号码\t选址结果ID\t装机地址名称\t标准地址id\t标准地址名称\t所属8级地址id\t所属8级地址名称\t")
                    .append("所属6级地址id\t所属6级地址名称\t地址关联的网格单元id\t地址关联的网格单元名称\t产品接入方式\t电路id\t设备id\t设备集团名称\t")
                    .append("设备类型\t电路类型\t设备关联的网格单元id\t设备关联的网格单元名称\t产品创建时间\t所属父产品id\t产品id\t营业部名称\t")
                    .append("设备所属子区域id\t设备所属子区域名称\t设备所属营业部名称\t设备类型名称\t是否一致\r\n");
        } catch (IOException e) {
            logger.error("file write error");
            return;
        }

        jdbcTemplate.query("select t.region_name        区域名称," +
                "t.subarea_name       子区域名称," +
                "t.gwm_fid            产品关联的网格单元id," +
                "t.grid_name          产品关联网格单元名称," +
                "t.grid_info          产品关联网格单元属性," +
                "t.yyb                产品关联营业部," +
                "t.product_eid        产品id," +
                "t.product_status     产品状态," +
                "t.productcode        产品编码," +
                "t.productinstance    产品实例," +
                "t.servicecode        业务号码," +
                "(select cp.pstncode from c_project cp where cp.area_eid=t.telemt_code and cp.mme_eid=t.product_eid and cp.entitytype_eid=110001) 电话号码," +
                "t.addressresult_eid  选址结果ID," +
                "t.fullname           装机地址名称," +
                "t.address_eid        标准地址id," +
                "t.address_name       标准地址名称," +
                "t.address_eid8       所属8级地址id," +
                "t.address_name8      所属8级地址名称," +
                "t.address_eid6       所属6级地址id," +
                "t.address_name6      所属6级地址名称," +
                "t.grid_adfid         地址关联的网格单元id," +
                "t.grid_adname        地址关联的网格单元名称," +
                "t.accesstype         产品接入方式," +
                "t.circuit_eid        电路id," +
                "t.node_eid           设备id," +
                "t.node_gname         设备集团名称," +
                "t.node_type          设备类型," +
                "t.circuit_type       电路类型," +
                "t.grid_nofid         设备关联的网格单元id," +
                "t.grid_noname        设备关联的网格单元名称," +
                "t.create_date        产品创建时间," +
                "t.projectparent_eid  所属父产品id," +
                "t.project_eidzx      产品id," +
                "t.yyb_name           营业部名称," +
                "t.nodesubarea_eid    设备所属子区域id," +
                "t.nodesubarea_name   设备所属子区域名称," +
                "t.nodeyyb_name       设备所属营业部名称," +
                "t.nodetype_name      设备类型名称," +
                "t.is_consistent      是否一致 " +
                "from project_wangge t " +
                "where exists (select 1 from project_wangge_total13 p where p.node_wangge_cn <> p.yzpnwangecn and p.yzpnwangecn <> 0 and p.node_eid = t.node_eid) " +
                "and t.gwm_fid <> t.grid_nofid", new RowCallbackHandler() {
            @Override
            public void processRow(ResultSet rs) throws SQLException {
                try {
                    fw.append(rs.getString(1)).append("\t").append(rs.getString(2)).append("\t").append(rs.getString(3)).append("\t")
                            .append(rs.getString(4)).append("\t").append(rs.getString(5)).append("\t").append(rs.getString(6)).append("\t")
                            .append(rs.getString(7)).append("\t").append(rs.getString(8)).append("\t").append(rs.getString(9)).append("\t")
                            .append(rs.getString(10)).append("\t").append(rs.getString(11)).append("\t").append(rs.getString(12)).append("\t")
                            .append(rs.getString(13)).append("\t").append(rs.getString(14)).append("\t").append(rs.getString(15)).append("\t")
                            .append(rs.getString(16)).append("\t").append(rs.getString(17)).append("\t").append(rs.getString(18)).append("\t")
                            .append(rs.getString(19)).append("\t").append(rs.getString(20)).append("\t").append(rs.getString(21)).append("\t")
                            .append(rs.getString(22)).append("\t").append(rs.getString(23)).append("\t").append(rs.getString(24)).append("\t")
                            .append(rs.getString(25)).append("\t").append(rs.getString(26)).append("\t").append(rs.getString(27)).append("\t")
                            .append(rs.getString(28)).append("\t").append(rs.getString(29)).append("\t").append(rs.getString(30)).append("\t")
                            .append(rs.getString(31)).append("\t").append(rs.getString(32)).append("\t").append(rs.getString(33)).append("\t")
                            .append(rs.getString(34)).append("\t").append(rs.getString(35)).append("\t").append(rs.getString(36)).append("\t")
                            .append(rs.getString(37)).append("\t").append(rs.getString(38)).append("\t").append(rs.getString(39)).append("\r\n");

                    ++loop;
                    if (loop % 40000 == 0) {
                        fw.close();
                        fw = new FileWriter(fileFullName, true);
                        logger.info("  -- 写入数据量: " + loop);
                    }

                } catch (IOException e) {
                    logger.error("file write error");
                }
            }
        });

        logger.info("共写入数据量: " + loop);

        try {
            fw.close();
        } catch (IOException e) {
            logger.error("FileWrite close error!!!!!!!");
        }
    }

}