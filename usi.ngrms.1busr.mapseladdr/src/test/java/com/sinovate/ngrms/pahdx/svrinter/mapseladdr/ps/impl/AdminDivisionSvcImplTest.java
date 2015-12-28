package com.sinovate.ngrms.pahdx.svrinter.mapseladdr.ps.impl;

import com.sinovate.ngrms.pahdx.svrinter.mapseladdr.abl.bo.AreaBo;
import com.sinovate.ngrms.pahdx.svrinter.mapseladdr.abl.bo.StreetBo;
import com.sinovate.ngrms.pahdx.svrinter.mapseladdr.abl.bo.SubAreaBo;
import com.sinovate.ngrms.pahdx.svrinter.mapseladdr.ps.svc.AdminDivisionSvc;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.Test;

import java.util.List;

/**
 * Created by Administrator on 2015/5/22.
 */
@ContextConfiguration(locations = {"classpath:app-test-context.xml"})
public class AdminDivisionSvcImplTest extends AbstractTestNGSpringContextTests {

    @Autowired
    private AdminDivisionSvc adminDivisionSvc;



    @Test(enabled = false)
    public void testAll() {
        adminDivisionSvc.init();
        List<AreaBo> areaBoList = adminDivisionSvc.getAreas();
        if(null != areaBoList && areaBoList.size()>0){
            for(AreaBo a:areaBoList){
                logger.info("区域名：" + a.getName());
                logger.info("\t该子区域下有子区域：");
                List<SubAreaBo> subAreaBoList = adminDivisionSvc.getSubAreas(a.getCode());
                if(null != subAreaBoList && subAreaBoList.size()>0){
                    for(SubAreaBo s:subAreaBoList){
                        logger.info("\t\t子区域名：" + s.getName());
                        logger.info("\t\t\t该子区域下有街道：");
                        List<StreetBo> streetBoList = adminDivisionSvc.getStreetNames(a.getCode(),s.getName());
                        if (null != streetBoList && streetBoList.size() > 0) {
                            for(StreetBo streetBo: streetBoList) {
                                logger.info("\t\t\t\t" + streetBo.getName());
                            }
                        }

                    }
                }
            }
        }
    }

    @Test(enabled = false)
    public void initTest() {
        adminDivisionSvc.init();
    }

    @Test(enabled = false)
    public void testGetAreas() throws Exception {
        adminDivisionSvc.init();
        List<AreaBo> acturalAreas = adminDivisionSvc.getAreas();
        for (AreaBo areaBo: acturalAreas) {
            logger.info("查询到areaBo的id信息：" + areaBo.getId());
            logger.info("查询到areaBo的name信息：" + areaBo.getName());
            logger.info("查询到areaBo的code信息：" + areaBo.getCode());
            logger.info("查询到areaBo的oldId信息：" + areaBo.getOldId());
            if (areaBo.getM_SubAreaBo() != null)
                logger.info("查询到areaBo的subareaBo列表长度信息：" + areaBo.getM_SubAreaBo().size());
        }
    }

    @Test(enabled = false)
    public void testGetSubAreas(/*String areaCode*/) throws Exception {
        adminDivisionSvc.init();
        List<SubAreaBo> acturalSubAreas = adminDivisionSvc.getSubAreas("551");
        if (acturalSubAreas != null) {
            for(SubAreaBo subAreaBo: acturalSubAreas) {
                logger.info("查询到subAreaBo的name信息：" + subAreaBo.getName());
                if(subAreaBo.getM_StreetBo() != null)
                    logger.info("查询到subAreaBo下的街道列表长度为" + subAreaBo.getM_StreetBo().size());
            }
        }

    }

    @Test(enabled = false)
    public void testGetStreetNames() throws Exception {
        adminDivisionSvc.init();
        List<StreetBo> acturalStreetNames = adminDivisionSvc.getStreetNames("551", "蜀山区");
        logger.debug("测试" + (acturalStreetNames == null));
        if (acturalStreetNames != null) {
            for (StreetBo streetBo: acturalStreetNames) {
                logger.info("查询到streetBo的name信息为：" + streetBo.getName());
                logger.info("查询到streetBo的id信息为：" + streetBo.getId());
            }
        }

    }
}
