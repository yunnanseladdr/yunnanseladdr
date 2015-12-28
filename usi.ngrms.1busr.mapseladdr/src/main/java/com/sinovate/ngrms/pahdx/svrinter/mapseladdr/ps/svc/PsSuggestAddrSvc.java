package com.sinovate.ngrms.pahdx.svrinter.mapseladdr.ps.svc;


import com.sinovate.ngrms.pahdx.svrinter.mapseladdr.aui.vo.SuggestiveAddrVo;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 提示地址服务
 * @author zszhang
 * @version 1.0
 * @created 18-五月-2015 13:37:44
 */
@Service
public interface PsSuggestAddrSvc {

	/**
	 * 取提示地址信息
	 * 实现步骤：
	 * 1、调用DatabaseNameConverter中的getDbName(String)方法获得dbname;
	 * 2、获得tablename；
	 * 3、调用TableSearcher中的search(String,String,String,
	 * PageRequest)方法获得提示地址信息的List<Map<String,Object>>集合
	 * 4、封装上一步返回的集合，获得List<SuggestiveAddrVo>集合并返回
	 * 
	 * @param areaCode
	 * @param searchText
	 * @param pageNo
	 * @param pageSize
	 */
	public List<SuggestiveAddrVo> getSuggAddr(String areaCode, String searchText, int pageNo,int pageSize);

}