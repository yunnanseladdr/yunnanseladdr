package com.sinovate.ngrms.pahdx.svrinter.mapseladdr.das.dao.impl;

import com.sinovate.ngrms.pahdx.svrinter.mapseladdr.das.dao.StreamReadAllBaseDao;
import com.sinovate.ngrms.pahdx.svrinter.mapseladdr.ps.svc.BuildFullTextSvc;
import com.sinovate.ngrms.pahdx.svrinter.mapseladdr.util.reflect.ClassUtil;
import com.sinovate.ngrms.pahdx.svrinter.mapseladdr.util.reflect.MethodUtil;
import com.sinovate.ngrms.pahdx.svrinter.mapseladdr.abl.bo.CdcBaseBo;
import com.sinovate.ngrms.pahdx.svrinter.mapseladdr.abl.bo.ResSiteBo;
import com.sinovate.ngrms.pahdx.svrinter.mapseladdr.das.dao.StreamHandler;
import com.sinovate.ngrms.pahdx.svrinter.mapseladdr.util.reflect.FieldUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.math.BigInteger;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by zszhang on 2015/5/19.
 */
@Component
public class StreamReadAllBaseDaoImpl<T extends CdcBaseBo> implements StreamReadAllBaseDao<T> {
    private final static Logger logger = LoggerFactory.getLogger(StreamReadAllBaseDaoImpl.class);
    /**
     * excludeFieldModifiers的内部整型表示，程序以此做判断。
     * 缺省设置为static transient
     */
    private static int _excludeMod = 0x00000088;

    @Autowired
    @Qualifier("jdbcTemplate_inv")
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private SimpleSqlSvc simpleSqlSvc;

    @Autowired
    private BuildFullTextSvc<T> buildFullTextSvc;

    /**
     * 根据SQL语句内容，将结果集中数据逐条封装为T类型，并交由hanler处理。
     * 本方法采用prepareStatement , args为要绑定进去的参数。
     *
     * @param sql
     * @param regionId
     * @param handler
     */
    @Override
    public void query(String sql, BigInteger regionId, final StreamHandler<T> handler, final Class<T> clazz) {
        if (null != regionId) {
            if (null == sql || sql.isEmpty()) {
                logger.warn("执行查询的SQL语句为空，donothing！");
                return;
            }
            String region_id = regionId.toString();
            try {
                jdbcTemplate.query(sql, new Object[]{region_id}, new RowCallbackHandler() {
                    @Override
                    public void processRow(ResultSet rs) throws SQLException {
                        procRow(rs, handler, clazz);
                    }
                });
            } catch (DataAccessException e) {
                e.printStackTrace();
            }
            handler.gameOver();
        } else {
//            query(sql, handler, clazz);
        }


    }

    /**
     * 根据SQL语句内容，将结果集中数据逐条封装为T类型，并交由hanler处理。要采用preparestatement，请使用另一个同名方法。
     *
     * @param sql
     * @param handler
     */
    @Override
    public void query(String sql, final StreamHandler<T> handler, final Class<T> clazz) {
        if (null == sql || sql.isEmpty()) {
            logger.warn("执行查询的SQL语句为空，donothing！");
            return;
        }
        try {
            jdbcTemplate.query(sql, new RowCallbackHandler() {
                @Override
                public void processRow(ResultSet rs) throws SQLException {
                    procRow(rs, handler, clazz);
                }
            });
        } catch (DataAccessException e) {
            e.printStackTrace();
        }
        handler.gameOver();
    }

    @Override
    public void query(String sql, String tableName, String maxRsId, final StreamHandler<T> handler, final Class<T> clazz) {
        if (null == sql || sql.isEmpty()) {
            logger.warn("执行查询的SQL语句为空，donothing！");
            return;
        }
        try {
            jdbcTemplate.query(sql, new Object[]{maxRsId}, new RowCallbackHandler() {
                @Override
                public void processRow(ResultSet rs) throws SQLException {
                    procRow(rs, handler, clazz);
                }
            });
        } catch (DataAccessException e) {
            e.printStackTrace();
        }
        handler.gameOver();
    }

    @Override
    public void query(String sql, String tableName, String maxRsId) {
        if (null == sql || sql.isEmpty()) {
            logger.warn("执行查询的SQL语句为空，donothing！");
            return;
        }
        try {
            jdbcTemplate.query(sql, new Object[]{tableName, maxRsId}, new RowCallbackHandler() {
                @Override
                public void processRow(ResultSet rs) throws SQLException {
                    deleteRow(rs);
                }
            });
        } catch (DataAccessException e) {
            e.printStackTrace();
        }
    }

    private void deleteRow(ResultSet rs) throws SQLException {
        try {
            buildFullTextSvc.delete(rs.getString("areaCode"), rs.getString("tableName"), rs.getString("id"));
            /*logger.info("删除区域：" + rs.getString("areaCode") + ";" + "类型为：" +
                    rs.getString("tableName") + ";" + "id为：" + rs.getString("id") + "对应的Es中的数据！");*/
        } catch (Exception e) {
            logger.warn("逐条删除数据时出现异常！");
            logger.info("区域：" + rs.getString("areaCode") + ";" + "类型：" +
                    rs.getString("tableName") + ";" + "id：" + rs.getString("id"));
            e.printStackTrace();
        }
    }

    void procRow(ResultSet rs, StreamHandler<T> handler, Class<T> clazz) throws SQLException {
        T obj = ClassUtil.createBean(clazz);
        List<Field> fields = FieldUtil.getAllFields(clazz);
        Object value = null;
        if ("SuggAddr".equals(rs.getString("tableName"))) {
            for (Field field : fields) {
                if (isExcludeFieldModifier(field.getModifiers())) continue;
                try {
                    if (field.getName().equals("name")) {
                        value = converselyStr(rs.getString(field.getName()));
                    } else {
                        value = rs.getString(field.getName());
                    }
                } catch (SQLException e) {
                    logger.debug("请求的属性名在查询列中没有，跳过该属性的处理，属性名为：" + field.getName());
                    continue;
                }
                MethodUtil.setFieldValue(obj, field.getName(), field.getType(), value);
                //FieldUtil.setFieldValue(obj, field, value);
            }
        } else {
            for (Field field : fields) {
                if (isExcludeFieldModifier(field.getModifiers())) continue;
                try {
                    value = rs.getString(field.getName());
                } catch (SQLException e) {
                    logger.debug("请求的属性名在查询列中没有，跳过该属性的处理，属性名为：" + field.getName());
                    continue;
                }
                //封装地址的局向信息
                if (field.getName().equals("sites")) {
                    String address_id = rs.getString("id");
                    if (null != address_id && !address_id.isEmpty()) {
                        if (!"D".equals(rs.getString("actionFlag"))) {
                            String site = getAddrSites(address_id);
                            MethodUtil.setFieldValue(obj, field.getName(), field.getType(), site);
                        }
                    }

                } else {
                    MethodUtil.setFieldValue(obj, field.getName(), field.getType(), value);
                    //FieldUtil.setFieldValue(obj, field, value);
                }

            }
        }
        handler.handle(obj);
    }

    private boolean isExcludeFieldModifier(int mod) {
        return (_excludeMod & mod) > 0;
    }

    /**
     * 改变数据库返回名称字段的顺序
     *
     * @param name
     * @return
     */
    private String converselyStr(String name) {
        if (null == name) {
            logger.debug("推荐地址名称为空");
            return "";
        }
        String[] names = name.split(" ");
        StringBuilder bf = new StringBuilder();
        for (int i = names.length - 1; i >= 0; i--) {
            bf.append(names[i]);
        }
        return bf.toString();
    }

    /**
     * 根据地址id获得相应的局向信息
     *
     * @param address_id
     * @return
     */
    private String getAddrSites(String address_id) {
        String sitesInfo = "";
        List<ResSiteBo> siteBoList = new ArrayList<>();
        try {
            siteBoList = jdbcTemplate.query(simpleSqlSvc.getQRY_ADDR_SITE(), new Object[]{address_id}, new RowMapper<ResSiteBo>() {
                @Override
                public ResSiteBo mapRow(ResultSet resultSet, int i) throws SQLException {
                    ResSiteBo resSiteBo = new ResSiteBo();
                    resSiteBo.setSiteId(resultSet.getString("id"));
                    resSiteBo.setSiteName(resultSet.getString("name"));
                    resSiteBo.setSiteCode(resultSet.getString("code"));
                    return resSiteBo;
                }
            });
        } catch (DataAccessException e) {
            e.printStackTrace();
        }
        if (null != siteBoList && siteBoList.size() > 0) {
            sitesInfo = siteBoList.toString();
            sitesInfo = sitesInfo.substring(1, sitesInfo.length() - 1);
        }
        return sitesInfo;
    }
}

