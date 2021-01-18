package com.essence.business.xqh.service.floodScheduling;


import com.essence.business.xqh.api.floodScheduling.service.StRsvrRService;
import com.essence.business.xqh.api.tuoying.dto.TuoyingStRsvrRDto;
import com.essence.business.xqh.dao.dao.tuoying.TuoyingStRsvrRDao;
import com.essence.business.xqh.dao.entity.tuoying.TuoyingStRsvrR;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 水库水情表服务实现
 * @company Essence
 * @author LiuGt
 * @version 1.0 2020/07/22
 */
@Transactional
@Service
public class StRsvrRServiceImpl implements StRsvrRService {

    @Autowired
    TuoyingStRsvrRDao rsvrRDao;

    /**
     * 根据测站ID查询最近一次实测数据
     * @param stcd 测站ID
     * @return
     */
    @Override
    public TuoyingStRsvrRDto queryLastOneByStcd(String stcd){
        TuoyingStRsvrRDto tuoyingStRsvrRDto = null;
        List<TuoyingStRsvrR> tuoyingStRsvrRList = rsvrRDao.queryLastOneByStcd(stcd);
        if (tuoyingStRsvrRList != null && tuoyingStRsvrRList.size() > 0){
            tuoyingStRsvrRDto = new TuoyingStRsvrRDto();
            TuoyingStRsvrR tuoyingStRsvrR = tuoyingStRsvrRList.get(0);
            BeanUtils.copyProperties(tuoyingStRsvrR,tuoyingStRsvrRDto);
            return tuoyingStRsvrRDto;
        }
        else{
            return null;
        }
    }

}
