package com.essence.business.xqh.web.fhybdd.controller;

import com.essence.business.xqh.api.fhybdd.dto.ModelCallBySWDDVo;
import com.essence.business.xqh.common.returnFormat.SystemSecurityMessage;
import com.essence.business.xqh.common.util.DateUtil;
import com.essence.business.xqh.dao.dao.fhybdd.StPptnRDao;
import com.essence.business.xqh.dao.dao.fhybdd.StStbprpBDao;
import com.essence.business.xqh.dao.dao.fhybdd.WrpRsrBsinDao;
import com.essence.business.xqh.dao.dao.realtimemonitor.*;
import com.essence.business.xqh.dao.entity.fhybdd.StPptnR;
import com.essence.business.xqh.dao.entity.fhybdd.StStbprpB;
import com.essence.business.xqh.dao.entity.fhybdd.WrpRsrBsin;
import com.essence.business.xqh.dao.entity.realtimemonitor.TRsvrR;
import com.essence.framework.util.StrUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/createData")
public class CreateDataConrtroller {


    @Autowired
    StPptnRDao stPptnRDao;
    @Autowired
    StStbprpBDao stStbprpBDao;

    @Autowired
    private WrpRsrBsinDao wrpRsrBsinDao;

    @Autowired
    private TRsvrRDao tRsvrRDao;
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

    /**
     * 创建水库数据
     * @return
     */
    @RequestMapping(value = "/cjsk", method = RequestMethod.GET)
    public SystemSecurityMessage cjsk() {
        try {
            //测站编码表
            List<WrpRsrBsin> list = wrpRsrBsinDao.findAll();
            Date time = DateUtil.getDateByStringNormal("2017/01/01 00:00:00");
            List<TRsvrR> rlist = new ArrayList<>();
            for (int i = 0; i < 52; i++) {
                Date times = DateUtil.getNextMonth(time,i);
                for (WrpRsrBsin wrpRsrBsin:list) {
                    TRsvrR tRsvrR = new TRsvrR();
                    tRsvrR.setStcd(wrpRsrBsin.getRscd());
                    Double rz=(int)(Math.random()*100+20)*1.0;
                    tRsvrR.setRz(rz+"");
                    tRsvrR.setTm(times);
                    tRsvrR.setRwptn("1");
                    tRsvrR.setInqdr("1");
                    rlist.add(tRsvrR);

                    Double rz22=(int)(Math.random()*5+1)*1.0;

                    TRsvrR tRsvrR2 = new TRsvrR();
                    tRsvrR2.setStcd(wrpRsrBsin.getRscd());
                    Double rz2=rz+rz22;
                    tRsvrR2.setRz(rz2+"");
                    tRsvrR2.setTm(com.essence.framework.util.DateUtil.getNextDay(times,13));
                    tRsvrR2.setRwptn("1");
                    tRsvrR2.setInqdr("1");
                    rlist.add(tRsvrR2);

                    Double rz23=(int)(Math.random()*5+1)*1.0;

                    TRsvrR tRsvrR3 = new TRsvrR();
                    tRsvrR3.setStcd(wrpRsrBsin.getRscd());
                    Double rz3=rz+rz23;
                    tRsvrR3.setRz(rz3+"");
                    tRsvrR3.setTm(com.essence.framework.util.DateUtil.getNextDay(times,28));
                    tRsvrR3.setRwptn("1");
                    tRsvrR3.setInqdr("1");
                    rlist.add(tRsvrR3);
                }

            }
            System.out.println("入库中…………");
            if(rlist.size()>0)
                tRsvrRDao.saveAll(rlist);
            return SystemSecurityMessage.getSuccessMsg("入库成功！",rlist);
        }catch (Exception e){
            e.printStackTrace();
            return SystemSecurityMessage.getFailMsg("入库失败！");

        }
    }

}
