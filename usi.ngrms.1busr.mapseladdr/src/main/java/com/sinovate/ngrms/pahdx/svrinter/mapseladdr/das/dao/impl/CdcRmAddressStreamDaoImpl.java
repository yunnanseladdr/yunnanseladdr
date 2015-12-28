package com.sinovate.ngrms.pahdx.svrinter.mapseladdr.das.dao.impl;

import com.sinovate.ngrms.pahdx.svrinter.mapseladdr.abl.bo.CdcRmAddrBo;
import com.sinovate.ngrms.pahdx.svrinter.mapseladdr.das.dao.CdcRmAddressStreamDao;
import com.sinovate.ngrms.pahdx.svrinter.mapseladdr.das.dao.StreamHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;

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
 * @date 2015-05-20 20:20
 * @desc cdc中增量数据获取dao
 * @see
 */
@Component
public class CdcRmAddressStreamDaoImpl implements CdcRmAddressStreamDao{

    private static final Logger logger = LoggerFactory.getLogger(CdcRmAddressStreamDaoImpl.class);

    private static final String SUGG_TABLE_NAME = "SuggAddr";

    private static final String USER_TABLE_NAME = "UserAddr";

    private static final String COMM_TABLE_NAME = "$cdc_change_table_name";

    private static final String CDC_RM_ADDRESS_CT = "cdc_rm_address_ct";

    //private static final String CDC_RE_ADDRESS_AHW_CT = "cdc_re_address_ahw_ct";

    private static final String CDC_RR_AREA_ADDRESS_CT = "cdc_rr_area_address_ct";

    private static final String SPECIAL_CONDITION = "and cdc.$specfield is not null";

    private static final String SPECIAL_CONDITION_2 = "and cdc.$spefield2 is not null";

    private static final String SPECIAL_FIELD = "$specfield";

    private static final String SPECIAL_FIELD_2 = "$spefield2";

    private static final String REGION_ID = "area_eid";

    private static final String SUB_AREA_ID = "subarea_eid";

    private static final String NOT_DELETE_CONDITION =  "and cdc.operation$ != 'D'";

    private static final String DECODE_CONDITION = "decode(cdc.operation$,'I','I','D','D','UN','U','I')";

    private static final String ALLWAYS_UPDATE = "'U'";

    private static final String ALLWAYS_INSERT = "'I'";

    @Autowired
    private StreamReadAllBaseDaoImpl streamReadAllBaseDao;

    @Autowired
    SimpleSqlSvc simpleSqlSvc;

    @Autowired
    @Qualifier("jdbcTemplate_cdc")
    private JdbcTemplate jdbcTemplate;

    /**
     * 获取不同地市的变化表中的变化数据，并交由handler处理
     *  1、分别获取cdc_rm_address_ct、cdc_rr_area_address_ct两张表中最大的RSID$
     *  2、分别处理这三张表的变化数据
     *  3、删除这三张表中的RSID$小于最大值的所有变化数据
     * @param handler
     */
    @Override
    public void procAllChangeAddr(StreamHandler<CdcRmAddrBo> handler) {
        String maxRmId = null;
        String maxReId = null;
        int delRowsCount[] = null;
        String maxRrId = null;
        SqlRowSet set = null;
        set = jdbcTemplate.queryForRowSet(simpleSqlSvc.getQueryMaxIdCdcCommSql().replace(COMM_TABLE_NAME,CDC_RM_ADDRESS_CT));
        if(set.first()) {
            maxRmId = set.getString("maxId");
        }
        if (null != maxRmId) {
            logger.info("##CDC_____处理cdc_rm_address_ct表");
            logger.info("##CDC__删除UserAddr的ES数据");
            streamReadAllBaseDao.query(simpleSqlSvc.getQueryDeleteCdcRmAddrSql(), USER_TABLE_NAME, maxRmId);
            logger.info("##CDC__删除SuggAddr的ES数据");
            streamReadAllBaseDao.query(simpleSqlSvc.getQueryDeleteCdcRmAddrSql(), SUGG_TABLE_NAME, maxRmId);
            logger.info("##CDC__重建UserAddr的ES数据");
            streamReadAllBaseDao.query(simpleSqlSvc.getQueryCommUserAddrSql().replace(DECODE_CONDITION, ALLWAYS_INSERT)
                            .replace(COMM_TABLE_NAME, CDC_RM_ADDRESS_CT).replace(SPECIAL_FIELD, REGION_ID).replace(SPECIAL_FIELD_2, SUB_AREA_ID),
                    USER_TABLE_NAME, maxRmId, handler, CdcRmAddrBo.class);
            logger.info("##CDC__重建SuggAddr的ES数据");
            streamReadAllBaseDao.query(simpleSqlSvc.getQueryCommSuggAddrsql().replace(DECODE_CONDITION, ALLWAYS_INSERT)
                            .replace(COMM_TABLE_NAME, CDC_RM_ADDRESS_CT).replace(SPECIAL_FIELD, REGION_ID).replace(SPECIAL_FIELD_2, SUB_AREA_ID),
                    SUGG_TABLE_NAME, maxRmId, handler, CdcRmAddrBo.class);

            logger.info("##CDC__删除9级地址关联SuggAddr的ES数据");
            streamReadAllBaseDao.query(simpleSqlSvc.getQueryDeleteNexusSuggSql(), SUGG_TABLE_NAME, maxRmId);
            logger.info("##CDC__重建9级地址关联SuggAddr的ES数据");
            streamReadAllBaseDao.query(simpleSqlSvc.getQueryNexusSuggAddrsql().replace(DECODE_CONDITION, ALLWAYS_INSERT)
                            .replace(COMM_TABLE_NAME, CDC_RM_ADDRESS_CT).replace(SPECIAL_FIELD, REGION_ID).replace(SPECIAL_FIELD_2, SUB_AREA_ID),
                    SUGG_TABLE_NAME, maxRmId, handler, CdcRmAddrBo.class);

            logger.info("##CDC__删除变化表数据");
            String RSID$ = maxRmId;
            List<Object[]> list = new ArrayList<>();
            list.add(new Object[]{RSID$});
            delRowsCount  = jdbcTemplate.batchUpdate(simpleSqlSvc.getDelCdcCommSql().replace(COMM_TABLE_NAME, CDC_RM_ADDRESS_CT), list);
            if (null != delRowsCount){
                logger.info("##CDC__删除变化表cdc_rm_address_ct的数据量为：" + delRowsCount[0] + "条！");
            }
        }
        /*set = jdbcTemplate.queryForRowSet(simpleSqlSvc.getQueryMaxIdCdcCommSql().replace(COMM_TABLE_NAME,CDC_RM_ADDRESS_CT));
        if (set.first()) {
            maxReId = set.getString("maxId");
        }
        if (null != maxReId) {
            streamReadAllBaseDao.query(simpleSqlSvc.getQueryDeleteCdcReAddrSql(),USER_TABLE_NAME,maxRmId);
            streamReadAllBaseDao.query(simpleSqlSvc.getQueryDeleteCdcReAddrSql(),SUGG_TABLE_NAME,maxRmId);
            logger.info("删除CDC_RM_ADDRESS_CT表中对应的Es数据");
            streamReadAllBaseDao.query(simpleSqlSvc.getQueryCommUserAddrSql().replace(DECODE_CONDITION, ALLWAYS_INSERT)
                            .replace(COMM_TABLE_NAME, CDC_RM_ADDRESS_CT).replace(SPECIAL_FIELD,SUB_AREA_ID),
                    USER_TABLE_NAME, maxReId, handler, CdcRmAddrBo.class);
            streamReadAllBaseDao.query(simpleSqlSvc.getQueryCommSuggAddrsql().replace(DECODE_CONDITION,ALLWAYS_INSERT)
                            .replace(COMM_TABLE_NAME, CDC_RM_ADDRESS_CT).replace(SPECIAL_FIELD,SUB_AREA_ID),
                    SUGG_TABLE_NAME, maxReId, handler, CdcRmAddrBo.class);
            List<Object[]> list1 = new ArrayList<>();
            String RSID$ = maxReId;
            list1.add( new Object[]{RSID$});
            delRowsCount  = jdbcTemplate.batchUpdate(simpleSqlSvc.getDelCdcCommSql().replace(COMM_TABLE_NAME, CDC_RM_ADDRESS_CT),list1);
            if (null != delRowsCount){
                logger.info("删除变化表CDC_RM_ADDRESS_CT的数据量为："+delRowsCount[0]+"条！");
            }
        }*/
        set = jdbcTemplate.queryForRowSet(simpleSqlSvc.getQueryMaxIdCdcCommSql().replace(COMM_TABLE_NAME,CDC_RR_AREA_ADDRESS_CT));
        if (set.first()) {
            maxRrId = set.getString("maxId");
        }
        if (null != maxRrId) {
            logger.info("##CDC__处理cdc_rr_area_address_ct表");
            streamReadAllBaseDao.query(simpleSqlSvc.getQueryCommUserAddrSql().replace(DECODE_CONDITION, ALLWAYS_UPDATE).
                            replace(COMM_TABLE_NAME, CDC_RR_AREA_ADDRESS_CT).replace(SPECIAL_CONDITION, "").replace(SPECIAL_CONDITION_2, "")
                            .replace(NOT_DELETE_CONDITION,"").replace("cdc.mme_eid", "cdc.address_eid"),
                    USER_TABLE_NAME, maxRrId, handler, CdcRmAddrBo.class);
            streamReadAllBaseDao.query(simpleSqlSvc.getQueryCommSuggAddrsql().replace(DECODE_CONDITION, ALLWAYS_UPDATE).
                            replace(COMM_TABLE_NAME, CDC_RR_AREA_ADDRESS_CT).replace(SPECIAL_CONDITION, "").replace(SPECIAL_CONDITION_2, "")
                            .replace(NOT_DELETE_CONDITION,"").replace("cdc.mme_eid", "cdc.address_eid").replace("cdc1.mme_eid", "cdc1.address_eid"),
                    SUGG_TABLE_NAME, maxRrId, handler, CdcRmAddrBo.class);
            logger.info("##CDC__删除表数据");
            List<Object[]> list2 = new ArrayList<>();
            String RSID$ = maxRrId;
            list2.add( new Object[]{RSID$});
            delRowsCount  = jdbcTemplate.batchUpdate(simpleSqlSvc.getDelCdcCommSql().replace(COMM_TABLE_NAME, CDC_RR_AREA_ADDRESS_CT),list2);
            if (null != delRowsCount){
                logger.info("##CDC__删除变化表cdc_rr_area_address_ct的数据量为："+delRowsCount[0]+"条！");
            }
        }
    }
}
