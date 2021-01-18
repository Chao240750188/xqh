package com.essence.business.xqh.service.floodScheduling;

import com.essence.business.xqh.api.floodScheduling.dto.ResZvarlViewDto;
import com.essence.business.xqh.api.floodScheduling.dto.SkddHifZvarlBDto;
import com.essence.business.xqh.api.floodScheduling.service.HifZvarlBService;
import com.essence.business.xqh.dao.dao.floodScheduling.SkddHifZvarlBDao;
import com.essence.business.xqh.dao.entity.floodScheduling.SkddHifZvarlB;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * 库容曲线表服务实现
 * @company Essence
 * @author LiuGt
 * @version 1.0 2020/04/07
 */
@Transactional
@Service
public class HifZvarlBServiceImpl implements HifZvarlBService {

    @Autowired
    SkddHifZvarlBDao hifZvarlBDao;

    /**
     * 根据水库ID查询默认出库规则流量
     * @param resCode
     * @return
     */
    @Override
    public List<ResZvarlViewDto> queryListByResCode(String resCode){
        List<SkddHifZvarlB> hifZvarlBS = hifZvarlBDao.queryListByResCode(resCode);
        List<ResZvarlViewDto> dtos =new ArrayList<>();
        if (hifZvarlBS!=null && hifZvarlBS.size() > 0){
            hifZvarlBS.forEach(hifZvarlB -> {
                ResZvarlViewDto dto = new ResZvarlViewDto();
                dto.setPtNo(hifZvarlB.getPtNo());
                dto.setRz(hifZvarlB.getRz());
                BigDecimal otq = new BigDecimal(0);
                otq = otq.add(hifZvarlB.getSpdsq()).add(hifZvarlB.getShq());
                dto.setOtq(otq);
                dtos.add(dto);
            });
        }
        return dtos;
    }

    /**
     * 根据水库ID和水位查询最接近的一个库容
     * @param resCode
     * @param rz
     * @return
     */
    @Override
    public BigDecimal queryWByResCodeAndRz(String resCode, BigDecimal rz){
        return hifZvarlBDao.queryWByResCodeAndRz(resCode, rz.doubleValue());
    }

    /**
     * 根据水库ID和库容量查询最接近的一个库水位
     * @param resCode
     * @param w
     * @return
     */
    @Override
    public BigDecimal queryRzByResCodeAndW(String resCode, BigDecimal w){
        return hifZvarlBDao.queryRzByResCodeAndW(resCode, w.doubleValue());
    }

    /**
     * 查询指定水库点序号最小的一条数据
     * @return
     */
    @Override
    public SkddHifZvarlBDto queryOneOrderByMinPtNo( String resCode){
        SkddHifZvarlBDto skddHifZvarlBDto = null;
        SkddHifZvarlB hifZvarlB = hifZvarlBDao.queryOneOrderByMinPtNo(resCode);
        if (null!=hifZvarlB){
            skddHifZvarlBDto = new SkddHifZvarlBDto();
            BeanUtils.copyProperties(hifZvarlB,skddHifZvarlBDto);
        }
        return skddHifZvarlBDto;
    }
}
