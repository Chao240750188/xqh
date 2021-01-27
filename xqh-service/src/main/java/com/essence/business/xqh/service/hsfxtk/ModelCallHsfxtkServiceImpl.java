package com.essence.business.xqh.service.hsfxtk;

import com.essence.business.xqh.api.hsfxtk.ModelCallHsfxtkService;
import com.essence.business.xqh.api.hsfxtk.dto.PlanInfoHsfxtkVo;
import com.essence.business.xqh.dao.dao.fhybdd.YwkPlaninfoDao;
import com.essence.business.xqh.dao.entity.fhybdd.YwkPlaninfo;
import com.essence.framework.util.StrUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class ModelCallHsfxtkServiceImpl implements ModelCallHsfxtkService {

    @Autowired
    private YwkPlaninfoDao ywkPlaninfoDao;

    @Override
    public String savePlanToDb(PlanInfoHsfxtkVo vo) {
        if(StrUtil.isEmpty(vo.getnPlanid())){
            vo.setnPlanid(StrUtil.getUUID());
        }
        Date startTime = vo.getStartTime(); //开始时间
        Date endTIme = vo.getEndTime();  //结束时间
        Date periodEndTime = vo.getPeriodEndTime();//预见结束时间
        if (periodEndTime != null) {
            endTIme = periodEndTime;
        }
        Long step = vo.getStep();//以分钟为单位

        //方案基本信息入库
        YwkPlaninfo ywkPlaninfo = new YwkPlaninfo();
        ywkPlaninfo.setnPlanid(vo.getnPlanid());
        ywkPlaninfo.setcPlanname(vo.getcPlanname());
        ywkPlaninfo.setnCreateuser("user");
        ywkPlaninfo.setnPlancurrenttime(new Date());
        ywkPlaninfo.setdCaculatestarttm(startTime);//方案计算开始时间
        ywkPlaninfo.setdCaculateendtm(endTIme);//方案计算结束时间
        ywkPlaninfo.setnPlanstatus(0l);//方案状态
        ywkPlaninfo.setnOutputtm(step);//设置间隔分钟
        ywkPlaninfo.setnModelid(vo.getModelId());
        ywkPlaninfo.setdRainstarttime(startTime);
        ywkPlaninfo.setdRainendtime(endTIme);
        YwkPlaninfo saveDbo = ywkPlaninfoDao.save(ywkPlaninfo);
        return saveDbo.getnPlanid();
    }
}
