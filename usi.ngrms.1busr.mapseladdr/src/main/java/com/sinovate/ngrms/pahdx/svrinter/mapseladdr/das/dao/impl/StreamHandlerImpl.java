package com.sinovate.ngrms.pahdx.svrinter.mapseladdr.das.dao.impl;

import com.sinovate.ngrms.pahdx.svrinter.mapseladdr.ps.task.MaintainIndexTaskFactory;
import com.sinovate.ngrms.pahdx.svrinter.mapseladdr.abl.bo.CdcBaseBo;
import com.sinovate.ngrms.pahdx.svrinter.mapseladdr.das.dao.StreamHandler;
import com.sinovate.ngrms.pahdx.svrinter.mapseladdr.ps.task.MaintainIndexTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zszhang on 2015/5/21.
 */
@Component
public class StreamHandlerImpl<T extends CdcBaseBo> implements StreamHandler<T> {
    private final static Logger logger = LoggerFactory.getLogger(StreamHandlerImpl.class);

    @Autowired
    private TaskExecutor worker;

    private final static int MINI_AREA_CODE = 549;
    private final static int PAGE_SIZE = 100;
    private final static int AREA_SIZE = 20;
    private final static int ACTION_SIZE = 3;
    private List<T>[][] data = new List[ACTION_SIZE][AREA_SIZE];

    private long totaldata = 0;

    @Autowired
    MaintainIndexTaskFactory maintainIndexTaskFactory;
    /**
     * 调用此方法，通知StreamHandler所有数据已经发送完。
     */
    @Override
    public void gameOver() {
        logger.debug("ready gameover");
        for(int i=0; i< ACTION_SIZE; i++) {
            for(int j=0; j< AREA_SIZE; j++) {
                if(null !=data[i][j] && !data[i][j].isEmpty()){
                    execTask(copyData(data[i][j]),i,this.getAreaCode(j));
                    data[i][j]= new ArrayList<T>(PAGE_SIZE);
                }

            }
        }
        totaldata = 0;
    }

    /**
     * 向流中增加一条数据
     *
     * @param obj
     */
    @Override
    public void handle(T obj) {
        if( null == obj )   return;
        int action = getAction(obj.getActionFlag());
        String areaCode = obj.getAreaCode();
        int areaIndex = getAreaIndex(areaCode);
        if( null == data[action][areaIndex] )
            data[action][areaIndex] = new ArrayList<T>(PAGE_SIZE);
        else if(data[action][areaIndex].size() == PAGE_SIZE) {
            execTask(data[action][areaIndex], action, areaCode);
            data[action][areaIndex] = new ArrayList<T>(PAGE_SIZE);
        }
        data[action][areaIndex].add(obj);
    }

    private int getAreaIndex(String areaCode) {
        int ac = Integer.parseInt(areaCode);
        return ac - MINI_AREA_CODE;
    }

    private String getAreaCode(int areaIndex) {
        if( areaIndex < 0 || areaIndex >= AREA_SIZE)
            return null;
        else
            return String.valueOf(areaIndex+MINI_AREA_CODE);
    }

    private int getAction(char actionFlag) {
        if(actionFlag == 'I')
            return MaintainIndexTask.ACTION_INSERT;
        if(actionFlag == 'U')
            return MaintainIndexTask.ACTION_UPDATE;
        if(actionFlag == 'D')
            return MaintainIndexTask.ACTION_DELETE;
        return -1;
    }

    private void execTask(List<T> list, int action, String areaCode) {
        if( null == list || list.isEmpty() )
            return;
        totaldata += list.size();
        logger.debug("exec task action = " + action +", areaCode = " + areaCode + ",data size = " + list.size() + ", total = " + totaldata);
        MaintainIndexTask task = maintainIndexTaskFactory.getTaskBean(list,action, areaCode);
        worker.execute(task);
    }

    private List<T> copyData(List<T> list) {
        List<T> newList = new ArrayList<T>();
        for (T t : list) {
            newList.add(t);
        }
        return newList;
    }
}
