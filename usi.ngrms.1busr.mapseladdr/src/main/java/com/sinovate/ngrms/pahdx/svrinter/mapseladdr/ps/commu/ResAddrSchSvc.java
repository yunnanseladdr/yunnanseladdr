package com.sinovate.ngrms.pahdx.svrinter.mapseladdr.ps.commu;

import com.sinovate.ngrms.pahdx.svrinter.mapseladdr.aui.vo.UserAddrVo;
import org.apache.commons.httpclient.HttpException;

import java.io.IOException;
import java.util.List;

/**
 * Created by Administrator on 2015/5/27.
 */
public interface ResAddrSchSvc {

    /**
     * 根据区域编码、用户输入获取用户地址数据，调用国朗注册在oip上的webService接口
     *
     */
    public List<UserAddrVo> getResAddr(String areaCode, String searchText, int pageIndex, int resultSize, String oipUrl, String sysCode) throws HttpException, IOException;

}
