package com.sinovate.ngrms.pahdx.svrinter.mapseladdr.ps.impl;

import com.sinovate.ngrms.pahdx.svrinter.addrsearch.das.model.ResAddrBo;
import com.sinovate.ngrms.pahdx.svrinter.mapseladdr.abl.bo.ResSiteBo;
import com.sinovate.ngrms.pahdx.svrinter.mapseladdr.aui.vo.UserAddrVo;
import com.sinovate.ngrms.pahdx.svrinter.mapseladdr.das.dao.RedisDao;
import com.sinovate.ngrms.pahdx.svrinter.mapseladdr.das.fullsearch.TableSearcher;
import com.sinovate.ngrms.pahdx.svrinter.mapseladdr.ps.support.DatabaseNameConverter;
import com.sinovate.ngrms.pahdx.svrinter.mapseladdr.ps.svc.PsUserAddrSvc;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.*;

/**
 * @author zszhang
 * @version 1.0
 * @created 18-五月-2015 13:37:45
 */
public class PsUserAddrSvcImpl implements PsUserAddrSvc {

	private static final Logger logger = LoggerFactory.getLogger(PsUserAddrSvcImpl.class);

	@Autowired
//	@Qualifier("databaseNameConverter")
	private DatabaseNameConverter databaseNameConverter;

	@Autowired
	private RedisDao redisDao;

	@Autowired
	@Qualifier("fullTextSearcherImplByElastic")
	private TableSearcher tableSearcher;

	public PsUserAddrSvcImpl(){

	}

	public void finalize() throws Throwable {

	}

	private String usertablename;
	private String useroldid;
	private String username;
	private String userhighlighttext;
	private String useroldsubareaid;
	private String usersubareaname;
	private String usersites;

	public void setUsertablename(String usertablename) {
		this.usertablename = usertablename;
	}

	public void setUseroldid(String useroldid) {
		this.useroldid = useroldid;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public void setUserhighlighttext(String userhighlighttext) {
		this.userhighlighttext = userhighlighttext;
	}

	public void setUseroldsubareaid(String useroldsubareaid) {
		this.useroldsubareaid = useroldsubareaid;
	}

	public void setUsersubareaname(String usersubareaname) {
		this.usersubareaname = usersubareaname;
	}

	public void setUsersites(String usersites) {
		this.usersites = usersites;
	}

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
	public List<UserAddrVo> getUserAddr(String areaCode, String searchText, int pageNo,int pageSize){
		if(null == areaCode || areaCode.equals("")){
			logger.warn("区域编码为空，无法获取提示地址信息，直接返回!");
			return null;
		}
		String dbName = databaseNameConverter.getDbName(areaCode);
		if(null == dbName || dbName.equals("")){
			logger.warn("通过区域编码："+areaCode+"获取dbname失败，直接返回!");
			return null;
		}
		boolean getValueFromRedis = true;
		String tableName = this.usertablename;
		List<Map<String,Object>>  maps = tableSearcher.search(dbName,tableName,searchText,pageNo,pageSize);
		if(null != maps && maps.size() > 0){
			List<UserAddrVo> userAddrVoList = new ArrayList<UserAddrVo>();
			for(Map<String,Object> m:maps){
				UserAddrVo userAddrVo = new UserAddrVo();
				userAddrVo.setId(null != m.get(this.useroldid) ? m.get(this.useroldid).toString():"");
				userAddrVo.setName(null != m.get(this.username) ? m.get(this.username).toString() : "");
				userAddrVo.setHighlightText(null != m.get(this.userhighlighttext) ? m.get(this.userhighlighttext).toString() : "");
				userAddrVo.setOldSubAreaId(null != m.get(this.useroldsubareaid) ? m.get(this.useroldsubareaid).toString() : "");
				userAddrVo.setSubAreaName(null != m.get(this.usersubareaname) ? m.get(this.usersubareaname).toString() : "");
				Object sites =  m.get(this.usersites);
				Set<ResSiteBo> siteBoSet = null;
				if (null != sites ) {
					getValueFromRedis = false;
					siteBoSet = getAddrSites((String)sites);
				}
				if(null !=siteBoSet && !siteBoSet.isEmpty()){
					userAddrVo.setSites(siteBoSet);
				}
				userAddrVoList.add(userAddrVo);
			}
			if(getValueFromRedis){
				getSitesFromRedis(userAddrVoList,areaCode);
			}
			return userAddrVoList;
			//封装
		}
		return null;
	}


	//获得局向信息
	private Set<ResSiteBo> getAddrSites(String sites){
		Set<ResSiteBo> resSiteBos = new HashSet<ResSiteBo>();
		ResSiteBo r = null;
		String[] arr = sites.split(", ");
		for (String s : arr) {
			String[] s1 = s.split(",");
			if (s1.length == 3) {
				r = new ResSiteBo();
				r.setSiteId(s1[0].trim());
				r.setSiteName(s1[1].trim());
				r.setSiteCode(s1[2].trim());
				resSiteBos.add(r);
			}
		}
		return resSiteBos;
	}

	/**
	 * 为用户地址设置局向信息
	 * @param list
	 */
	private void getSitesFromRedis(List<UserAddrVo> list,String areaCode){
		if(null != list && list.size()>0){
			List<String> oldids = new ArrayList<>();
			for(UserAddrVo u:list){
				String id = u.getId();
				if(null !=id && !id.isEmpty()){
					oldids.add(id);
				}
			}
			if(null != oldids && oldids.size()>0){
				List<ResAddrBo> resAddrBos = redisDao.getResAddsByIds(areaCode,oldids);
				if(null !=resAddrBos && resAddrBos.size()>0){
					for(UserAddrVo userAddrVo:list){
						String id = userAddrVo.getId();
						for(ResAddrBo resAddrBo:resAddrBos){
							if(null == resAddrBo){
								continue;
							}
							String oldid = resAddrBo.getAddressId();
							if(id.equals(oldid)){
								Set<ResSiteBo> resSiteBos = new HashSet<>();
								Set<com.sinovate.ngrms.pahdx.svrinter.addrsearch.das.model.ResSiteBo> resSiteBo = resAddrBo.getSites();
								for(com.sinovate.ngrms.pahdx.svrinter.addrsearch.das.model.ResSiteBo r:resSiteBo){
									ResSiteBo resSiteBo1 = new ResSiteBo();
									resSiteBo1.setSiteId(r.getSiteId());
									resSiteBo1.setSiteName(r.getSiteName());
									resSiteBo1.setSiteCode(r.getSiteCode());
									resSiteBos.add(resSiteBo1);
								}
								userAddrVo.setSites(resSiteBos);
							}
						}
					}
				}
			}
		}

	}

}