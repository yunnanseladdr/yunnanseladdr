package com.sinovate.ngrms.pahdx.svrinter.mapseladdr.das.dao.impl;

import org.springframework.stereotype.Component;

/**
 * @author tu.chenghong@ustcinfo.com
 * @单位名称：科大国创—电信资源事业部 USTC Sinovate Software Co.,Ltd.
 * Copyright (c) 2014 All Rights Reserved.
 * @系统名称：
 * @工程名称：usi.yxz.allparent
 * @文件名称:
 * @类路径: com.sinovate.ngrms.pahdx.svrinter.seladdr.ps.impl
 * @date 2015-05-19 16:13
 * @desc
 * @see
 */
@Component
public class SimpleSqlSvc {
        private String queryChangeAddrSQL;

        public String getQueryChangeAddrSQL() {
                return this.queryChangeAddrSQL;
        }

        public void setQueryChangeAddrSQL(String queryChangeAddrSQL) {
                this.queryChangeAddrSQL = queryChangeAddrSQL;
        }

        //查找网格表信息
        private String queryGridInfoSql;

        public String getQueryGridInfoSql() {
                return queryGridInfoSql;
        }
        public void setQueryGridInfoSql(String queryGridInfoSql) {
                this.queryGridInfoSql = queryGridInfoSql;
        }

        //查找临近号码信息
        private String queryNearGridSql;

        public String getQueryNearGridSql() {
                return queryNearGridSql;
        }

        public void setQueryNearGridSql(String queryNearGridSql) {
                this.queryNearGridSql = queryNearGridSql;
        }

        // 查找地址ID对应的网格ID
        private String queryGridIdByAddrIdsql;

        public String getQueryGridIdByAddrIdsql() {
                return queryGridIdByAddrIdsql;
        }

        public void setQueryGridIdByAddrIdsql(String queryGridIdByAddrIdsql) {
                this.queryGridIdByAddrIdsql = queryGridIdByAddrIdsql;
        }

        // 查找网格ID对应的工程信息
        private String queryProjInfoByGridIdSql;

        public String getQueryProjInfoByGridIdSql() {
                return queryProjInfoByGridIdSql;
        }

        public void setQueryProjInfoByGridIdSql(String queryProjInfoByGridIdSql) {
                this.queryProjInfoByGridIdSql = queryProjInfoByGridIdSql;
        }

        // 根据网格ID查找装维咨询电话
        private String queryTelByGridIdSql;

        public String getQueryTelByGridIdSql() {
                return queryTelByGridIdSql;
        }

        public void setQueryTelByGridIdSql(String queryTelByGridIdSql) {
                this.queryTelByGridIdSql = queryTelByGridIdSql;
        }

        //查找小前台返回参数信息
        private String queryXqtInfoSql;

        public String getQueryXqtInfoSql() {
                return queryXqtInfoSql;
        }

        public void setQueryXqtInfoSql(String queryXqtInfoSql) {
                this.queryXqtInfoSql = queryXqtInfoSql;
        }

        //查找区域信息
        private String QRY_AREA_INFO1 ;
        public String getQRY_AREA_INFO1() {
                return QRY_AREA_INFO1;
        }

        public void setQRY_AREA_INFO1(String QRY_AREA_INFO1) {
                this.QRY_AREA_INFO1 = QRY_AREA_INFO1;
        }
        //根据区域id查找子区域（县信息和中心市信息）
        private String QRY_SUBAREA_INFO1 ;

        public String getQRY_SUBAREA_INFO1() {
                return QRY_SUBAREA_INFO1;
        }

        public void setQRY_SUBAREA_INFO1(String QRY_SUBAREA_INFO1) {
                this.QRY_SUBAREA_INFO1 = QRY_SUBAREA_INFO1;
        }

        //查找中心市下面的区信息
        private String QRY_QU_INFO1 ;

        public String getQRY_QU_INFO1() {
                return QRY_QU_INFO1;
        }

        public void setQRY_QU_INFO1(String QRY_QU_INFO1) {
                this.QRY_QU_INFO1 = QRY_QU_INFO1;
        }

        //根据中心市id和区id查找区下面的乡镇街道信息
        private String QRY_STRTOWM_INFO2 ;

        public String getQRY_STRTOWM_INFO2() {
                return QRY_STRTOWM_INFO2;
        }

        public void setQRY_STRTOWM_INFO2(String QRY_STRTOWM_INFO2) {
                this.QRY_STRTOWM_INFO2 = QRY_STRTOWM_INFO2;
        }

        //根据县id查找县下面的乡镇街道信息
        private String QRY_STRTOWN_INFO1 ;

        public String getQRY_STRTOWN_INFO1() {
                return QRY_STRTOWN_INFO1;
        }

        public void setQRY_STRTOWN_INFO1(String QRY_STRTOWN_INFO1) {
                this.QRY_STRTOWN_INFO1 = QRY_STRTOWN_INFO1;
        }

        //查询cdc变化表中最大的RSID$
        private String queryMaxIdCdcCommSql;

        public String getQueryMaxIdCdcCommSql() {
                return queryMaxIdCdcCommSql;
        }

        public void setQueryMaxIdCdcCommSql(String queryMaxIdCdcCommSql) {
                this.queryMaxIdCdcCommSql = queryMaxIdCdcCommSql;
        }

        //删除处理过的cdc变化表中的数据
        private String delCdcCommSql;

        public String getDelCdcCommSql() {
                return delCdcCommSql;
        }

        public void setDelCdcCommSql(String delCdcCommSql) {
                this.delCdcCommSql = delCdcCommSql;
        }

        //查询变化表中符合条件的user地址数据
        private String queryCommUserAddrSql;

        public String getQueryCommUserAddrSql() {
                return queryCommUserAddrSql;
        }

        public void setQueryCommUserAddrSql(String queryCommUserAddrSql) {
                this.queryCommUserAddrSql = queryCommUserAddrSql;
        }

        //查询变化表中符合条件的sugg地址数据
        private String queryCommSuggAddrsql;

        public String getQueryCommSuggAddrsql() {
                return queryCommSuggAddrsql;
        }

        public void setQueryCommSuggAddrsql(String queryCommSuggAddrsql) {
                this.queryCommSuggAddrsql = queryCommSuggAddrsql;
        }
        //查询cdc_rm_address_ct表中需要在es中做删除操作的数据
        private String queryDeleteCdcRmAddrSql;

        public String getQueryDeleteCdcRmAddrSql() {
                return queryDeleteCdcRmAddrSql;
        }

        public void setQueryDeleteCdcRmAddrSql(String queryDeleteCdcRmAddrSql) {
                this.queryDeleteCdcRmAddrSql = queryDeleteCdcRmAddrSql;
        }
        //查询cdc_re_address_ct表中需要在es中做删除操作的数据
        private String queryDeleteCdcReAddrSql;

        public String getQueryDeleteCdcReAddrSql() {
                return queryDeleteCdcReAddrSql;
        }

        public void setQueryDeleteCdcReAddrSql(String queryDeleteCdcReAddrSql) {
                this.queryDeleteCdcReAddrSql = queryDeleteCdcReAddrSql;
        }

        //9级地址关联的建议地址查询
        private String queryDeleteNexusSuggSql;

        public String getQueryDeleteNexusSuggSql() {
                return queryDeleteNexusSuggSql;
        }

        public void setQueryDeleteNexusSuggSql(String queryDeleteNexusSuggSql) {
                this.queryDeleteNexusSuggSql = queryDeleteNexusSuggSql;
        }

        //9级地址关联的建议地址处理
        private String queryNexusSuggAddrsql;

        public String getQueryNexusSuggAddrsql() {
                return queryNexusSuggAddrsql;
        }

        public void setQueryNexusSuggAddrsql(String queryNexusSuggAddrsql) {
                this.queryNexusSuggAddrsql = queryNexusSuggAddrsql;
        }

        //找地址信息
        public String QRY_ADDR = "select a.mme_eid itspId, a.chinese_character_match fullName, a.mme_eid oldId\n" +
                "          from c_address a\n" +
                "         where a.area_eid = ?\n" +
                "           and a.subarea_eid = ?\n" +
                "           and a.addresstype = 1\n" +
                "           and a.entitytype_eid = 100001\n" +
                "           and a.chinese_character_match<>'0' order by a.mme_eid desc";
        //取一个区域下面的用户地址
    /*    public String QRY_ADDR_NOSUB = "select substr(a.mme_eid,0) id, a.chinese_character_match name, a.mme_eid oldId," +
                "'551' areaCode,to_number('12') as hierarchy , '' as  subnames from c_address a where a.mme_eid =13618176";*/
    public String QRY_ADDR_NOSUB ;

        public String getQRY_ADDR_NOSUB() {
                return QRY_ADDR_NOSUB;
        }

        public void setQRY_ADDR_NOSUB(String QRY_ADDR_NOSUB) {
                this.QRY_ADDR_NOSUB = QRY_ADDR_NOSUB;
        }

        //按区域-子区域 查地址总数
        public String QRY_ADDR_TOTAL = "select count(a.mme_eid)\n" +
                "          from c_address a\n" +
                "         where a.area_eid = ?\n" +
                "           and ah.subarea_eid = ?\n" +
                "           and ah.addresstype = 1\n" +
                "           and a.entitytype_eid = 100001\n" +
                "           and (a.chinese_character_match is not null and a.chinese_character_match<>'0')";

        //按区域-子区域 查地址总数
        public String QRY_ADDR_TOTAL_NOSUB = "select count(a.mme_eid)\n" +
                "          from c_address a\n" +
                "         where a.area_eid = ?\n" +
                "           and ah.addresstype = 1\n" +
                "           and a.entitytype_eid = 100001\n" +
                "           and (a.chinese_character_match is not null and a.chinese_character_match<>'0')";

        //找区域信息
        public String QRY_INFO = "select t3.id id,\n" +
                "t3.name name,\n" +
                "t3.code code,\n" +
                "t3.oldId oldId,\n" +
                "t3.subId subId,\n" +
                "t3.subName subName,\n" +
                "t3.subCode subCode,\n" +
                "t3.subOldId subOldId,\n" +
                "t3.count count \n" +
                "from rm_info t3 \n";
        public String QRY_AREA = "select t3.id id,\n" +
                "t3.name name,\n" +
                "t3.code code,\n" +
                "t3.oldId oldId,\n" +
                "t3.subId subId,\n" +
                "t3.subName subName,\n" +
                "t3.subCode subCode,\n" +
                "t3.subOldId subOldId,\n" +
                "(select count(1) \n" +
                "from c_address ra \n" +
                "where ra.area_eid = t3.id \n" +
                "and re.subarea_eid = t3.subId \n" +
                "and ra.entitytype_eid = 100001\n" +
                "and re.addresstype = 1\n" +
                "and ra.chinese_character_match <> '0' \n" +
                "and ra.chinese_character_match is not null) count \n" +
                "from (select t1.area_id id, \n" +
                "t1.area_name name, \n" +
                "t1.area_code code, \n" +
                "ltrim(substr(t1.area_old_id, 12), 0) oldId, \n" +
                "t2.sub_area_id subId, \n" +
                "t2.sub_area_name subName, \n" +
                "t2.sub_area_code subCode, \n" +
                "ltrim(substr(t2.sub_area_old_id, 12), 0) subOldId \n" +
                "from (select t.mme_eid     area_id, \n" +
                "t.groupname   area_name, \n" +
                "t.groupcode   area_code, \n" +
                "t.mme_eid area_old_id \n" +
                "from c_location t \n" +
                "where t.entitytype_eid in (107001,107002)\n" +
                "and exists \n" +
                "(select 1 from c_address where area_eid = t.id)) t1 \n" +
                "left join (select t.parentlocation_eid, \n" +
                "t.mme_eid        sub_area_id, \n" +
                "t.groupname      sub_area_name, \n" +
                "t.groupcode      sub_area_code, \n" +
                "t.mme_eid    sub_area_old_id \n" +
                "from c_location t \n" +
                "where  t.entitytype_eid in (107001,107002)\n" +
                "and exists (select 1 \n" +
                "from c_address \n" +
                "where subarea_eid = t.id)) t2 \n" +
                "on t1.area_id = t2.parentlocation_eid \n" +
                "order by code) t3 \n" +
                "order by code \n" ;

        public String getQRY_ADDR_SITE() {
                return QRY_ADDR_SITE;
        }

        public void setQRY_ADDR_SITE(String QRY_ADDR_SITE) {
                this.QRY_ADDR_SITE = QRY_ADDR_SITE;
        }

        //地址对应局站信息
        public String QRY_ADDR_SITE;

        //查询所有的区域信息
        public String Qry_AREA_INFO =  " select t.mme_eid     area_id,\n" +
                " t.groupname   area_name,\n" +
                " t.groupcode   area_code,\n" +
                " t.mme_eid oldId\n" +
                " from c_location t\n" +
                " where t.entitytype_eid in (107001,107002)\n" +
                "and exists\n" +
                " (select 1 from c_address where area_eid = t.mme_eid) order by oldId";

        //找子区域信息
        public String QRY_SUBAREA = "select /*addrsearch*/ t1.area_id id,\n" +
                "       t1.area_name name,\n" +
                "       t1.area_code code,\n" +
                "       ltrim(substr(t1.area_old_id, 12), 0) oldId,\n" +
                "       t2.sub_area_id subId,\n" +
                "       t2.sub_area_name subName,\n" +
                "       t2.sub_area_code subCode,\n" +
                "       ltrim(substr(t2.sub_area_old_id, 12), 0) subOldId\n" +
                "  from (select t.mme_eid     area_id,\n" +
                "               t.groupname   area_name,\n" +
                "               t.groupcode   area_code,\n" +
                "               t.mme_eid area_old_id\n" +
                "          from c_location t\n" +
                "         where t.entitytype_eid in (107001,107002)\n" +
                "           and t.id =mme_eid ?\n" +
                "           and exists\n" +
                "         (select 1 from c_address where area_eid = t.mme_eid)) t1\n" +
                "  left join (select t.parentlocation_eid,\n" +
                "                    t.mme_eid        sub_area_id,\n" +
                "                    t.groupname      sub_area_name,\n" +
                "                    t.groupcode      sub_area_code,\n" +
                "                    t.mme_eid    sub_area_old_id\n" +
                "               from c_location t\n" +
                "              where t.entitytype_eid in (107001,107002)\n" +
                "                and t.parentlocation_eid = ?\n" +
                "                and exists\n" +
                "              (select 1 from c_address where subarea_eid = t.mme_eid)) t2\n" +
                "    on t1.area_id = t2.parentlocation_eid";

        //查找某个子区域下面的街道信息
        public String QRY_STREETORTOWNBYSUBAREA = "select /*addrsearch*/ a.mme_eid ID ,a.name NAMEBAK, a.subarea_eid SUB_AREA_ID, c.groupname SUB_AREA_NAME,\n" +
                "replace((SELECT LTRIM(MAX(SYS_CONNECT_BY_PATH(T.NAME, '$$#$$$##$@@#')), '$$#$$$##$@@#') PARENTNAME FROM C_ADDRESS T START WITH T.MME_EID = a.id CONNECT BY NOCYCLE PRIOR T.PARENTADDRESS_EID = T.MME_EID),'$$#$$$##$@@#',',')  STREET_UP_NAME\n" +
                "from c_address a, c_location c \n" +
                "and c.mme_eid = a.subarea_eid \n" +
                "and a.entitytype_eid = 100001\n" +
                "and a.hierarchy = 5 \n" +
                "and a.address_type = 1 \n" +
                "and a.subarea_eid = ?";

        //根据条件模糊查询地址数量

        public String QRY_ADDRCOUNT_FUZZ = "select count(1)\n" +
                "          from c_address a\n" +
                "         where a.chinese_character_match like ?\n" +
                "           and a.area_eid = ?\n" +
                "           and a.subarea_eid = ?\n" +
                "           and a.address_type = 1\n" +
                "           and a.entitytype_eid = 100001 \n" +
                "           and (a.chinese_character_match is not null and a.chinese_character_match<>'0') ";
        //按照区域id模糊查询该区域下的用户地址的总数量
        public String QRY_ADDRCOUNT_NOSUB_FUZZ = "select count(1)\n" +
                "          from c_address a\n" +
                "         where a.chinese_character_match like ?\n" +
                "           and a.area_eid = ?\n" +
                "           and a.address_type = 1\n" +
                "           and a.entitytype_eid = 100001\n" +
                "           and (a.chinese_character_match is not null and a.chinese_character_match<>'0')";

        //模糊查询分页查找地址信息
        public String QRY_ADDR_FUZZ = "select a.mme_eid itspId, a.chinese_character_match fullName, a.mme_eid oldId\n" +
                "          from c_address a\n" +
                "         where a.chinese_character_match like ?\n" +
                "           and a.area_eid = ?\n" +
                "           and a.subarea_eid = ?\n" +
                "           and a.address_type = 1\n" +
                "           and a.entitytype_eid = 100001\n" +
                "           and (a.chinese_character_match is not null and a.chinese_character_match<>'0') order by a.id desc";

        //按照区域id模糊查询该区域下的用户地址
        public String QRY_ADDR_FUZZ_NOSUB = "select a.mme_eid itspId, a.chinese_character_match fullName, a.mme_eid oldId\n" +
                "          from c_address a\n" +
                "         where a.chinese_character_match like ?\n" +
                "           and a.area_eid = ?\n" +
                "           and a.address_type = 1\n" +
                "           and a.entitytype_eid = 100001\n" +
                "           and (a.chinese_character_match is not null and a.chinese_character_match<>'0') order by a.id desc";

        //查找9级地址以内地址name信息
        public String QRY_ADDR_NAME= "select a.name name,a.MME_EID id,a.PARENTADDRESS_EID parentid, ah.hierarchy hierarchy,\n " +
                "           replace((SELECT LTRIM(MAX(SYS_CONNECT_BY_PATH(T.NAME, '$$#$$$##$@@#')), '$$#$$$##$@@#') PARENTNAME FROM C_ADDRESS T START WITH T.mme_eid = a.id CONNECT BY NOCYCLE PRIOR T.PARENTADDRESS_EID = T.MME_EID),'$$#$$$##$@@#',',')  nameUpName\n" +
                "          from c_address a\n"+
                "           where a.area_eid = ?\n" +
                "           and ah.subarea_eid = ?\n" +
                "           and a.entitytype_eid = 100001\n"+
                "           and a.hierarchy in (8,9)\n"+
                "           and a.address_type = 1\n" +
                "           and a.name<>'-' \n"+
                "           and a.name<>'*' \n"+
                "           and a.name<>'\\'" ;
        //查找某个区域下面的8、9级地址
        public String QRY_89ADDR_NAME;

        public String getQRY_89ADDR_NAME() {
                return QRY_89ADDR_NAME;
        }

        public void setQRY_89ADDR_NAME(String QRY_89ADDR_NAME) {
                this.QRY_89ADDR_NAME = QRY_89ADDR_NAME;
        }

        public String QRY_ADDR_NAMEBYID = "Select  rm.name NAME,rm.PARENTADDRESS_EID PARENT_ID,rm.mme_eid ID, \n" +
                "rm.area_eid OLD_ID, rm.chinese_character_match FULL_NAME,\n" +
                "rm.area_eid REGION_ID,rm.entitytype_eid SPEC_ID, \n" +
                "rm.subarea_eid SUB_AREA_ID, rm.hierarchy HIERARCHY ,\n" +
                "replace((SELECT LTRIM(MAX(SYS_CONNECT_BY_PATH(T.NAME, '$$#$$$##$@@#')), '$$#$$$##$@@#') PARENTNAME FROM C_ADDRESS T START WITH T.MME_EID = rm.mme_eid CONNECT BY NOCYCLE PRIOR T.PARENTADDRESS_EID = T.MME_EID),'$$#$$$##$@@#',',')  NAME_UP_NAME\n" +
                "from c_address rm, \n" +
                "where rm.address_type = 1\n" +
                "and rm.hierarchy in(8,9)\n"+
                "and rm.mme_eid = ?";

        public String QRY_STREET_NAMEBYID = "select a.mme_eid ID ,a.name NAMEBAK, a.subarea_eid , c.groupname SUB_AREA_NAME,\n" +
                "replace((SELECT LTRIM(MAX(SYS_CONNECT_BY_PATH(T.NAME, '$$#$$$##$@@#')), '$$#$$$##$@@#')  FROM C_ADDRESS T START WITH T.MME_EID = a.mme_eid CONNECT BY NOCYCLE PRIOR T.PARENTADDRESS_EID = T.MME_EID),'$$#$$$##$@@#',','   )  STREET_UP_NAME\n" +
                "from c_address a, c_location c\n" +
                "and c.mme_eid = a.subarea_eid\n" +
                "and a.entitytype_eid = 100001\n" +
                "and a.hierarchy =  5 \n" +
                "and a.address_type = 1\n" +
                "and a.mme_eid = ?" ;
        public String QRY_ADDR_NAME_NOSUB = "select a.name name,a.mme_eid oldId,a.parentaddress_eid parentid\n " +
                "          from c_address a\n"+
                "           where a.area_eid = ?\n" +
                "           and a.entitytype_eid = 100001\n"+
                "           and a.hierarchy in (8,9)\n"+
                "           and a.address_type = 1\n" +
//            "           and a.name<>'/' \n"+
                "           and a.name<>'-' \n"+
                "           and a.name<>'*' \n"+
                "           and a.name<>'\\' \n"+
                "          order by a.name desc";

        public String QRY_ADDR_NAME_TOTAL = "select /*addrsearch*/ count(1)\n " +
                "          from c_address a\n"+
                "           where a.area_eid = ?\n" +
                "           and ah.sunarea_eid = ?\n" +
                "           and a.entitytype_eid = 100001\n"+
                "           and ah.hierarchy in (8,9)\n"+
                "           and ah.address_type = 1\n" +
//            "           and a.name<>'/' \n"+
                "           and a.name<>'-' \n"+
                "           and a.name<>'*' \n"+
                "           and a.name<>'\\' \n";

        public String QRY_ADDR_NAME_NOSUB_TOTAL = "select count(1)\n " +
                "          from c_address a\n"+
                "           where a.area_eid = ?\n" +
                "           and a.entitytype_eid = 100001\n"+
                "           and ah.hierarchy in (8,9)\n"+
                "           and ah.address_type = 1\n" +
//            "           and a.name<>'/' \n"+
                "           and a.name<>'-' \n"+
                "           and a.name<>'*' \n"+
                "           and a.name<>'\\' \n";
        public String QRY_SIX_RM_ADDRESS =
                "select c.name name from c_address a, c_address b , c_address c\n" +
                        "where a.area_eid = ?\n" +
                        "and a.area_eid = b.area_eid\n" +
                        "and b.area_eid = c.area_eid\n" +
                        "and a.parentlocation_eid = b.mme_eid\n" +
                        "and b.parentlocation_eid = c.mme_eid\n" +
                        "and c.subarea_eid = ?\n" +
                        "and a.mme_eid = ? \n" +
                        "and c.hierarchy = 6\n" +
                        "and c.address_type = 1\n" +
                        "and a.entitytype_eid = 100001";
        //
        public String QRY_PARENT_RM_ADDRESS = "select /*addrsearch*/ rm7.name SEVEN_NAME,\n" +
                "rm6.name SIX_NAME, rm5.name FIVE_NAME \n" +
                "from c_address rm7 ,\n" +
                "rm_address rm6 ,\n" +
                " rm_address rm5,\n" +
                "where rm7.parentaddress_eid = rm6.mme_eid\n" +
                "and rm6.parentaddress_eid = rm5.mme_eid\n" +
                "and rm6.area_eid = rm7.area_eid\n" +
                "and rm5.area_eid = rm7.area_eid\n" +
                "and rm6.address_type = rm7.address_type\n" +
                "and rm5.address_type = rm7.address_type\n" +
                "and rm6.sub_area_id = rm7.sub_area_id\n" +
                "and rm5.sub_area_id = rm7.sub_area_id\n" +
                "and rm7.mme_eid = ?\n" +
                "and rm7.area_eid = ?\n" +
                "and re7.subarea_eid = ?\n" +
                "and rm7.entitytype_eid = 100001\n" +
                "and rm7.address_type = 1" ;

        //    public String QRY_ADDR_PARENTNAME = "select /*addrsearch*/ to_char(wm_concat(t.name)) parentName from c_address t \n" +
//    		"start with t.mme_eid = ? \n" +
//    		"connect by nocycle prior t.parentaddress_eid = t.mme_eid";

        public String QRY_ADDR_PARENTNAME = "SELECT /*addrsearch*/  replace(LTRIM(MAX(SYS_CONNECT_BY_PATH(T.NAME, '$$#$$$##$@@#')), '$$#$$$##$@@#'),'$$#$$$##$@@#',',') PARENTNAME \n" +
                "FROM C_ADDRESS T \n" +
                "START WITH T.MME_EID = ? \n" +
                "CONNECT BY NOCYCLE PRIOR T.PARENTADDRESS_EID = T.MME_EID";
        //查找所有子区域下面的街道信息
        public String QRY_ADDR_STREETORTOWN = "select /*addrsearch*/ a.mme_eid ID ,a.name NAMEBAK, a.subarea_eid SUB_AREA_ID, c.name SUB_AREA_NAME,\n" +
                "replace((SELECT LTRIM(MAX(SYS_CONNECT_BY_PATH(T.NAME, '$$#$$$##$@@#')), '$$#$$$##$@@#') PARENTNAME FROM C_ADDRESS T START WITH T.MME_EID = a.mme_eid CONNECT BY NOCYCLE PRIOR T.PARENTADDRESS_EID = T.MME_EID),'$$#$$$##$@@#',',')  STREET_UP_NAME\n" +
                "from c_address a, c_location c \n" +
                "where c.mme_eid = a.subarea_eid \n" +
                "and a.entitytype_eid = 100001\n" +
                "and b.hierarchy = "+ 5 +"\n" +
                "and b.address_type = 1";
        public String QRY_ADDR_STREETORTOWN_COUNT = "select /*addrsearch*/ count(a.id) COUNT \n" +
                "from c_address a, c_location c \n" +
                "where c.mme_eid = a.subarea_eid \n" +
                "and a.entitytype_eid = 100001\n" +
                "and b.hierarchy = "+ 5 +"\n" +
                "and b.address_type =1";

        public String BATCHINSERT_SQL = "INSERT INTO ES_ERROR_DATA (id,old_id,region_id,sub_area_id,full_name,split_name,sub_name,error_type,es_full_name,area_name,create_date)\n" +
                " VALUES (?,?,?,?,?,?,?,?,?,?,sysdate)";

        public String QRY_HIERARCHY_SQL="select b.hierarchy from c_address a \n" +
                " where  a.address_type = 1 \n" +
                "and a.entitytype_eid = 100001\n" +
                "and a.mme_eid = ?";

}
