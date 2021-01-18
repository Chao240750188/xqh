package com.essence.business.xqh.service.floodScheduling;

import com.essence.business.xqh.api.floodScheduling.dto.SkddStZvarlBDto;
import com.essence.business.xqh.api.floodScheduling.service.StZvarlBService;
import com.essence.business.xqh.dao.dao.floodScheduling.SkddStZvarlBDao;
import com.essence.business.xqh.dao.entity.floodScheduling.SkddStZvarlB;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * 库容曲线表服务实现
 * @company Essence
 * @author LiuGt
 * @version 1.0 2020/07/20
 */
@Transactional
@Service
public class StZvarlBServiceImpl implements StZvarlBService {

    @Autowired
    SkddStZvarlBDao stZvarlBDao;

    /**
     * 根据水库ID查询库容曲线
     * @param resCode
     * @return
     */
    @Override
    public List<SkddStZvarlBDto> queryListByResCode(String resCode){
        List<SkddStZvarlBDto> skddStZvarlBDtoList = new ArrayList<>();
        List<SkddStZvarlB> stZvarlBS = stZvarlBDao.queryListByResCode(resCode);
        if (stZvarlBS.size()>0){
            for (int i=0;i<stZvarlBS.size();i++){
                SkddStZvarlB stZvarlB = stZvarlBS.get(i);
                SkddStZvarlBDto skddStZvarlBDto = new SkddStZvarlBDto();
                BeanUtils.copyProperties(stZvarlB,skddStZvarlBDto);
                skddStZvarlBDtoList.add(skddStZvarlBDto);
            }
        }
        return skddStZvarlBDtoList;
    }
}
