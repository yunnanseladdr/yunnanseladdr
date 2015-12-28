package com.sinovate.ngrms.pahdx.svrinter.mapseladdr.das.dao.impl;

import com.sinovate.ngrms.pahdx.svrinter.addrsearch.das.model.ResAddrBo;
import com.sinovate.ngrms.pahdx.svrinter.mapseladdr.das.dao.RedisDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by Administrator on 2015/5/26.
 */
@Service
public class RedisDaoImpl implements RedisDao {

    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    public List<ResAddrBo> getResAddsByIds(String areaCode, List<String> oldIds){
        List<ResAddrBo> addrBos = redisTemplate.boundHashOps(areaCode).multiGet(oldIds);
        return addrBos;
    }
}
