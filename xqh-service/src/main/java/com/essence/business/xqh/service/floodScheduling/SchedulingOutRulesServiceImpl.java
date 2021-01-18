package com.essence.business.xqh.service.floodScheduling;

import com.essence.business.xqh.api.floodScheduling.dto.SkddSchedulingOutRulesDto;
import com.essence.business.xqh.api.floodScheduling.service.SchedulingOutRulesService;
import com.essence.business.xqh.dao.dao.floodScheduling.SkddSchedulingOutRulesDao;
import com.essence.business.xqh.dao.entity.floodScheduling.SkddSchedulingOutRules;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * 调度方案出库规则表服务实现
 * @company Essence
 * @author LiuGt
 * @version 1.0 2020/04/09
 */
@Transactional
@Service
public class SchedulingOutRulesServiceImpl implements SchedulingOutRulesService {

    @Autowired
    SkddSchedulingOutRulesDao schedulingOutRulesDao;

    /**
     * 根据方案ID查询调度规则数据
     * @param planId
     * @return
     */
    @Override
    public List<SkddSchedulingOutRulesDto> queryRulesByPlanId(String planId){
        List<SkddSchedulingOutRulesDto> skddSchedulingOutRulesDtoList = new ArrayList<>();
        List<SkddSchedulingOutRules> schedulingOutRulesList = schedulingOutRulesDao.queryRulesByPlanId(planId);
        if (schedulingOutRulesList.size()>0){
            for (int i=0;i<schedulingOutRulesList.size();i++){
                SkddSchedulingOutRules schedulingOutRules = schedulingOutRulesList.get(i);
                SkddSchedulingOutRulesDto skddSchedulingOutRulesDto = new SkddSchedulingOutRulesDto();
                BeanUtils.copyProperties(schedulingOutRules,skddSchedulingOutRulesDto);
                skddSchedulingOutRulesDtoList.add(skddSchedulingOutRulesDto);
            }
        }
        return skddSchedulingOutRulesDtoList;
    }

    /**
     * 根据方案ID和库水位，查询最接近的出库流量
     * @param planId
     * @param rz
     * @return
     */
    @Override
    public BigDecimal queryOtqByPlanIdAndRz(String planId, BigDecimal rz){
        return schedulingOutRulesDao.queryOtqByPlanIdAndRz(planId, rz.doubleValue());
    }
}
