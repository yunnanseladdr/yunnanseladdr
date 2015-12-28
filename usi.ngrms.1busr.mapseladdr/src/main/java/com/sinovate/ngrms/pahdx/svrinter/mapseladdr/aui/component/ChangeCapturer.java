package com.sinovate.ngrms.pahdx.svrinter.mapseladdr.aui.component;



import com.sinovate.ngrms.pahdx.svrinter.mapseladdr.das.dao.CdcRmAddressStreamDao;
import com.sinovate.ngrms.pahdx.svrinter.mapseladdr.abl.bo.CdcRmAddrBo;
import com.sinovate.ngrms.pahdx.svrinter.mapseladdr.das.dao.StreamHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * 数据变化获取者，此类为定时任务类，由scheduler调用起来。关键放在在run方法中。
 * @author zszhang
 * @version 1.0
 * @created 18-五月-2015 13:39:06
 */
public class ChangeCapturer {

	private final static int STAT_RUNNING = 1;
	private final static int STAT_STOP = 0;
	private static final Logger logger = LoggerFactory.getLogger(ChangeCapturer.class);
	private static int stat = STAT_STOP;

	@Autowired
	private StreamHandler<CdcRmAddrBo> streamHandler;

	@Autowired
	private CdcRmAddressStreamDao cdcRmAddressStreamDao;

	public ChangeCapturer(){

	}

	public void finalize() throws Throwable {

	}

	/**
	 * 从runnable继承的方法，是此任务类运行的入口方法。
	 * 处理步骤是：
	 * 1. 调用CdcRmAddressStreamDao从CDC获取变化的T；
	 * 2. CdcRmAddressStreamDao会自动调用本类的handle和gameOver方法，对每条数据进行处理。
	 */
	public void run(){
		if(stat == STAT_RUNNING ) return;
		logger.info("增量开始........");
		stat = STAT_RUNNING;
		cdcRmAddressStreamDao.procAllChangeAddr(streamHandler);
		stat = STAT_STOP;
		logger.info("增量结束........");
	}

}