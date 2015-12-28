package com.sinovate.ngrms.pahdx.svrinter.mapseladdr.util.aws;

import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * 地址分词器，实现地址的拆分和合并
 * Created by zszhang on 2015/5/20.
 */
@Component
public interface AddressSegmenter {
    /**
     * 对输入地址进行分词
     * @param addr 输入地址
     * @return 分词后的结果
     */
    public String splitAddr(String addr);

    /**
     * 对分词结果进行反向恢复
     * @param splitAddr 分词的结果
     * @return 恢复后的地址
     */
    public String recoverAddr(String splitAddr);

    /**
     * 将准备写入ES中的数据进行分词
     *
     * @param data 准备写入ES中的数据
     * @return
     */
    public void splitAddrData(Map<String, Object> data);

    /**
     * 将从ES中读出的数据进行恢复
     *
     * @param data 从ES中读出的数据
     * @return
     */
    public void recoverAddrData(Map<String, Object> data);

}
