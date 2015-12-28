package com.sinovate.ngrms.pahdx.svrinter.mapseladdr.abl.impl;


import com.sinovate.ngrms.pahdx.svrinter.mapseladdr.abl.svc.AblSuggestAddrSvc;
import com.sinovate.ngrms.pahdx.svrinter.mapseladdr.ps.svc.PsSuggestAddrSvc;
import com.sinovate.ngrms.pahdx.svrinter.mapseladdr.aui.vo.SuggestiveAddrVo;
import com.sinovate.ngrms.pahdx.svrinter.mapseladdr.ps.support.GetMapInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author zszhang
 * @version 1.0
 * @created 18-五月-2015 13:38:34
 */
@Service
public class AblSuggestAddrSvcImpl implements AblSuggestAddrSvc {

	private static final Logger logger = LoggerFactory.getLogger(AblSuggestAddrSvcImpl.class);

	@Autowired
	@Qualifier("psSuggestAddrSvcImpl")
	private PsSuggestAddrSvc psSuggestAddrSvc;

	@Autowired
	private GetMapInfo getMapInfo;

	public AblSuggestAddrSvcImpl(){

	}

	public void finalize() throws Throwable {

	}

	/**
	 * 取提示地址信息
	 * 取街道(areaCode, subAreaName, streetName,,pageNo,pageSize)下面的提示地址信息
	 * 关键步骤：将subAreaName与streetName用空格隔开，放入searchText;
	 * 实现步骤：
	 * 1、封装查询参数，将subAreaName与streetName用空格隔开，赋值给searchText；
	 * 2、调用PsSuggestAddrSvc中的getSuggAddr(String,String,PageRequest)方法获得提示地址信息；
	 *
	 * @param areaCode
	 * @param subAreaName
	 * @param streetName
	 * @param pageNo
	 * @param pageSize
	 */
	public List<SuggestiveAddrVo> getSuggAddr(String areaCode, String subAreaName, String streetName, int pageNo, int pageSize){

		String searchText = null;
		List<SuggestiveAddrVo> list = null;

		if(areaCode == null || areaCode.isEmpty()) {
			logger.warn("区域代码areaCode为空，无法处理！");
			return null;
		}

		if(subAreaName == null || subAreaName.isEmpty()) {
			logger.warn("子区域名称subAreaName为空，无法处理！");
			return null;
		}

		if(streetName == null | streetName.isEmpty()) {
			searchText = subAreaName;
			list = psSuggestAddrSvc.getSuggAddr(areaCode, searchText, pageNo, pageSize);
		} else {
			searchText = (new StringBuilder(subAreaName).append(" ").append(streetName)).toString();
 		    list = psSuggestAddrSvc.getSuggAddr(areaCode, searchText, pageNo, pageSize);
		}
		return list;
	}

	/**
	 * 取提示地址信息
	 * 取街道(areaCode, subAreaName, streetName)下面的能匹配用户输入(input)信息的提示地址。
	 * subAreaName, streetName 可以同时为空
	 * streetName可以单独为空
	 * 实现步骤：
	 * 1、封装查询参数，将subAreaName、streetName、input三个参数用空格隔开，赋值给searchText；
	 * 2、调用PsSuggestAddrSvc中的getSuggAddr(String,String,PageRequest)方法获得提示地址信息；
	 *
	 * @param areaCode
	 * @param subAreaName
	 * @param streetName
	 * @param input
	 * @param pageNo
	 * @param pageSize
	 */
	public List<SuggestiveAddrVo> getSuggAddrWithInput(String areaCode, String subAreaName, String streetName,
													   String input, int pageNo, int pageSize){

		if(null == areaCode || areaCode.isEmpty()) {
			logger.warn("区域代码areaCode为空，无法处理！");
			return null;
		}

		if(input == null || input.isEmpty()) {
			logger.warn("input为空，无法处理");
			return null;
		}

		String searchText = null;
		List<SuggestiveAddrVo> list = new ArrayList<>();
		if(subAreaName == null || subAreaName.isEmpty()) {
			searchText = (new StringBuilder(input)).toString();
			logger.info("通过区域和输入关键词查找提示分词,封装后的参数为："+searchText);
			list = psSuggestAddrSvc.getSuggAddr(areaCode, searchText, pageNo, pageSize);

		}else {
			if(streetName == null || streetName.isEmpty()) {
				searchText = (new StringBuilder(subAreaName).append(" ").append(input)).toString();
				logger.info("通过区域、子区域和输入关键词查找提示分词,封装后的参数为："+searchText);
				list = psSuggestAddrSvc.getSuggAddr(areaCode, searchText, pageNo, pageSize);
			} else {
				searchText = (new StringBuilder(subAreaName).append(" ").append(streetName).append(" ").append(input)).toString();
				logger.info("通过区域、子区域、街道和输入关键词查找提示分词,封装后的参数为："+searchText);
				list = psSuggestAddrSvc.getSuggAddr(areaCode, searchText, pageNo, pageSize);
			}
		}
		return list;
	}

	public ArrayList<String> getMapAddrInfo(Double lng, Double lat){
		if (lng < 0 || lat < 0) {
			logger.warn("传入经纬度参数错误，无法处理！");
		}
		logger.info("+++调用百度api转换经纬度+++");
		Map<String, Double> lngAndLat = getMapInfo.getLngAndLat(lng, lat);
		logger.info("++根据经纬度查询网格名称及网格类型+++");
		ArrayList<String> gridNameInfo = getMapInfo.getGridName(lngAndLat);

		if (gridNameInfo == null) {
			return null;
		}

		return gridNameInfo;

	}
}