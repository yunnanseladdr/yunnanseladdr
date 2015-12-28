package com.sinovate.ngrms.pahdx.svrinter.mapseladdr.ps.impl;

import com.sinovate.ngrms.pahdx.svrinter.mapseladdr.das.fullsearch.TableSearcher;
import com.sinovate.ngrms.pahdx.svrinter.mapseladdr.ps.support.DatabaseNameConverter;
import com.sinovate.ngrms.pahdx.svrinter.mapseladdr.ps.svc.PsSuggestAddrSvc;
import com.sinovate.ngrms.pahdx.svrinter.mapseladdr.aui.vo.SuggestiveAddrVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * @author zszhang
 * @version 1.0
 * @created 18-五月-2015 13:37:44
 */
@Service
public class PsSuggestAddrSvcImpl implements PsSuggestAddrSvc {
	private static final Logger logger = LoggerFactory.getLogger(PsSuggestAddrSvcImpl.class);

	@Autowired
	@Qualifier("databaseNameConverter")
	private DatabaseNameConverter databaseNameConverter;

	@Autowired
	@Qualifier("fullTextSearcherImplByElastic")
	private TableSearcher tableSearcher;


	public PsSuggestAddrSvcImpl(){

	}

	public void finalize() throws Throwable {

	}
	private String suggtablename;
	private String suggname;
	private String sugghighlighttext;
	private String suggid;
	private String suggnamebak;
	private String suggsubnames;

	public void setSuggtablename(String suggtablename) {
		this.suggtablename = suggtablename;
	}

	public void setSuggname(String suggname) {
		this.suggname = suggname;
	}

	public void setSugghighlighttext(String sugghighlighttext) {
		this.sugghighlighttext = sugghighlighttext;
	}

	public void setSuggid(String suggid) {
		this.suggid = suggid;
	}

	public void setSuggnamebak(String suggnamebak) {
		this.suggnamebak = suggnamebak;
	}

	public void setSuggsubnames(String suggsubnames) {
		this.suggsubnames = suggsubnames;
	}

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
	public List<SuggestiveAddrVo> getSuggAddr(String areaCode, String searchText, int pageNo,int pageSize){
		if(null == areaCode && areaCode.equals("")){
			logger.warn("区域编码为空，无法获取提示地址信息，直接返回!");
			return null;
		}
		String dbName = databaseNameConverter.getDbName(areaCode);
		if(null == dbName && dbName.equals("")){
			logger.warn("通过区域编码："+areaCode+"获取dbname失败，直接返回!");
			return null;
		}
		String tableName = this.suggtablename;
		List<Map<String,Object>>  maps = tableSearcher.search(dbName,tableName,searchText,pageNo,pageSize);
		if (null != maps) {
			logger.info("查找到的提示分词的个数："+maps.size());
		}else {
			logger.info("查找到的提示分词的个数："+ 0);
		}
		if(null != maps && maps.size() > 0){
			List<SuggestiveAddrVo> suggestiveAddrVoList = new ArrayList<SuggestiveAddrVo>();
			for(Map<String,Object> m :maps){
				//封装
				SuggestiveAddrVo suggestiveAddrVo = new SuggestiveAddrVo();
				suggestiveAddrVo.setName(null != m.get(this.suggname) ? m.get(this.suggname).toString():"");
				suggestiveAddrVo.setHighlightText(null != m.get(this.sugghighlighttext) ? m.get(this.sugghighlighttext).toString() : "");
				suggestiveAddrVo.setId(null != m.get(this.suggid) ? m.get(this.suggid).toString() : "");
//				suggestiveAddrVo.setNameBak(null != m.get(this.suggnamebak) ? m.get(this.suggnamebak).toString():"");
				if (null != m.get(this.sugghighlighttext) && null != m.get(this.suggnamebak)) {
					suggestiveAddrVo.setNameBak(addHighLightType(m.get(this.sugghighlighttext).toString(),m.get(this.suggnamebak).toString()));
				}
				suggestiveAddrVo.setSubNames(null != m.get(this.suggsubnames) ? m.get(this.suggsubnames).toString():null);
				suggestiveAddrVoList.add(suggestiveAddrVo);
			}
			return suggestiveAddrVoList;

		}
		return null;
	}

	private String addHighLightType(String highLightText, String nameBak) {
		highLightText = highLightText.replace(" ", "");
		String[] spiltStr = highLightText.split("</em>");
		String[] newSpiltStr = null;
		ArrayList<String> listStr = new ArrayList<String>();
		if (spiltStr[spiltStr.length-1].indexOf("<em>") == -1) {
			newSpiltStr = new String[spiltStr.length-1];
			for (int i = 0; i < newSpiltStr.length; i++) {
				newSpiltStr[i] = spiltStr[i];
			}
			spiltStr = newSpiltStr;
		}
		Set<String> oldStr = new HashSet<String>();
		if (spiltStr.length > 0) {
			for (int i = 0 ; i < spiltStr.length ; i ++) {
				if (spiltStr[i].indexOf("<em>") != -1) {
					spiltStr[i] = spiltStr[i].substring(spiltStr[i].indexOf("<em>") + 4, spiltStr[i].length());
				}
			}
		}
		for (String str : spiltStr) {
			if (nameBak.indexOf(str) > -1) {
				if (!oldStr.contains(str)) {
					nameBak = nameBak.replace(str, "<em>" + str + "</em>");
					oldStr.add(str);
				}
			}
		}
		return nameBak;
	}

}