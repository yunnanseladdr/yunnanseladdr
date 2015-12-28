package com.sinovate.ngrms.pahdx.svrinter.mapseladdr.ps.task;


import com.sinovate.ngrms.pahdx.svrinter.mapseladdr.abl.bo.CdcBaseBo;
import com.sinovate.ngrms.pahdx.svrinter.mapseladdr.ps.svc.BuildFullTextSvc;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import java.util.List;

/**
 * 索引数据更新任务，实现runnable接口，使用线程池调用。
 * @author zszhang
 * @version 1.0
 * @created 18-五月-2015 13:37:44
 */
public class MaintainIndexTask<T extends CdcBaseBo> implements Runnable{

	private static final Logger logger = LoggerFactory.getLogger(MaintainIndexTask.class);

	/**
	 * 说明本任务的类型，可取值为ACTION_DELETE, ACTION_INSERT, ACTION_UPDATE
	 */
	private int action;
	/**
	 * 删除索引
	 */
	public static final int ACTION_DELETE = 0;
	/**
	 * 对索引应该采取的操作
	 */
	public static final int ACTION_INSERT = 1;
	/**
	 * 更新索引
	 */
	public static final int ACTION_UPDATE = 2;


	private final List<T> data;
	private final String areaCode;
	private final BuildFullTextSvc<T> buildFullTextSvc;



	/**
	 * 构造函数
	 * 
	 * @param data
	 * @param action
	 */
	public MaintainIndexTask(final BuildFullTextSvc<T> buildFullTextSvc, final List<T> data, int action, String areaCode){
		this.data = data;
		this.action = action;
		this.areaCode = areaCode;
		this.buildFullTextSvc = buildFullTextSvc;
	}

	/**
	 * 从runnable继承来的接口。主要处理方法；
	 * 处理步骤是，调用BuildFullTextSvc的相关方法对数据进行刷新。
	 */
	public void run(){

		if(action == ACTION_UPDATE) {
			buildFullTextSvc.update(areaCode, data);
		} else if(action == ACTION_DELETE) {
			String addrType = data.get(0).getTableName();
			for(int i = 0; i < data.size(); i++) {
				String id = data.get(i).getId();
				buildFullTextSvc.delete(areaCode, addrType, id);
			}
		} else if(action == ACTION_INSERT) {
			buildFullTextSvc.insert(areaCode, data);
		} else {
			return;
		}
	}

}