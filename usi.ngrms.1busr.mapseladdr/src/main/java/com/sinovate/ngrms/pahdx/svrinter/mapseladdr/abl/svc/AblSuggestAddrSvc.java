package com.sinovate.ngrms.pahdx.svrinter.mapseladdr.abl.svc;


import com.sinovate.ngrms.pahdx.svrinter.mapseladdr.aui.vo.SuggestiveAddrVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * 提示地址服务
 * @author zszhang
 * @version 1.0
 * @created 18-五月-2015 13:38:34
 */
public interface AblSuggestAddrSvc {

	static final Logger logger = LoggerFactory.getLogger(AblSuggestAddrSvc.class);

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
	public List<SuggestiveAddrVo> getSuggAddr(String areaCode, String subAreaName, String streetName, int pageNo, int pageSize);

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
	public List<SuggestiveAddrVo> getSuggAddrWithInput(String areaCode, String subAreaName, String streetName, String input, int pageNo, int pageSize);

	public /*List<SuggestiveAddrVo>*/ ArrayList<String> getMapAddrInfo(Double lng, Double lat);
}