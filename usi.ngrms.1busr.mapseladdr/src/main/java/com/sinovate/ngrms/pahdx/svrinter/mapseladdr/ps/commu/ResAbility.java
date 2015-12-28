package com.sinovate.ngrms.pahdx.svrinter.mapseladdr.ps.commu;


import com.sinovate.ngrms.pahdx.svrinter.mapseladdr.abl.bo.ResAbilityBo;
import org.apache.commons.httpclient.HttpException;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

/**
 * 资源能力查询接口
 * @author zszhang
 * @version 1.0
 * @created 18-五月-2015 13:37:45
 */
@Service
public interface ResAbility {

	/**
	 * 根据区域编码、地址id（oldId）获取地址的接入能力，调用国朗注册在oip上的webService接口
	 * 
	 */
	public List<ResAbilityBo> getResAbility(String areaCode, String addrId,
									  String siteId, String siteName,
									  String oipUrl) throws HttpException, IOException;

}