package com.essence.business.xqh.web.fhybdd.controller;

import com.essence.business.xqh.api.fhybdd.dto.ModelCallBySWDDVo;
import com.essence.business.xqh.common.returnFormat.SystemSecurityMessage;
import com.essence.business.xqh.common.util.DateUtil;
import com.essence.business.xqh.dao.dao.fhybdd.StPptnRDao;
import com.essence.business.xqh.dao.dao.fhybdd.StStbprpBDao;
import com.essence.business.xqh.dao.entity.fhybdd.StPptnR;
import com.essence.business.xqh.dao.entity.fhybdd.StStbprpB;
import com.essence.framework.util.StrUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/createData")
public class CreateDataConrtroller {


    @Autowired
    StPptnRDao stPptnRDao;
    @Autowired
    StStbprpBDao stStbprpBDao;

    /**
     * 水文调度模型
     * @return
     */
    @RequestMapping(value = "/test", method = RequestMethod.GET)
    public SystemSecurityMessage modelCallBySWDD(ModelCallBySWDDVo vo) {
        try {
            List<StStbprpB> list = stStbprpBDao.findAll();
            List<StStbprpB> newLIst = list.stream().filter(StStbprpB->"1".equals(StStbprpB.getAddvcd())).collect(Collectors.toList());
            List<StPptnR> stPptnRList = new ArrayList<>();
            Date time = DateUtil.getDateByStringNormal("2021/03/01 00:00:00");
            for (int i = 0; i < 500; i++) {
                Date times = DateUtil.getNextHour(time,i);
                for (StStbprpB stStbprpB:newLIst) {
                    StPptnR stPptnR = new StPptnR();
                    stPptnR.setStcd(stStbprpB.getStcd());
                    Double drp=(int)(Math.random()*10+1)*1.0;
                    stPptnR.setDrp(drp);
                    stPptnR.setTm(times);
                    stPptnR.setId(StrUtil.getUUID());
                    stPptnRList.add(stPptnR);
                }

            }
            System.out.println("入库中…………");
            if(stPptnRList.size()>0)
                stPptnRDao.saveAll(stPptnRList);
            return SystemSecurityMessage.getSuccessMsg("调用防洪与报警水文调度模型成功！");
        }catch (Exception e){
            e.printStackTrace();
            return SystemSecurityMessage.getFailMsg("调用防洪与报警水文调度模型失败！");

        }
    }

}
