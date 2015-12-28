package com.sinovate.ngrms.pahdx.svrinter.mapseladdr.ps.svc;

import com.sinovate.ngrms.pahdx.svrinter.mapseladdr.aui.vo.UserAddrVo;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 用户地址服务
 * @author zszhang
 * @version 1.0
 * @created 18-五月-2015 13:37:44
 */
@Service
public interface PsUserAddrSvc {

	/**
	 * 取用户地址信息（全地址查询）
	 * 根据areaCode不同，调用不同的索引查询
	 * 实现步骤：
	 * 1、调用DatabaseNameConverter中的getDbName(String)方法获得dbname;
	 * 2、获得tablename；
	 * 3、调用TableSearcher中的search(String,String,String,
	 * PageRequest)方法获得用户地址的List<Map<String,Object>>集合
	 * 4、封装上一步返回的集合，获得List<UserAddrVo>集合并返回
	 * 
	 * @param areaCode
	 * @param searchText
	 * @param pageNo
	 * @param pageSize
	 */
	public List<UserAddrVo> getUserAddr(String areaCode, String searchText, int pageNo,int pageSize);

}