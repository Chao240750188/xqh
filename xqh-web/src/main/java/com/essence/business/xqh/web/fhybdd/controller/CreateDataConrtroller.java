package com.essence.business.xqh.web.fhybdd.controller;

import com.essence.business.xqh.api.fhybdd.dto.ModelCallBySWDDVo;
import com.essence.business.xqh.common.returnFormat.SystemSecurityMessage;
import com.essence.business.xqh.common.util.DateUtil;
import com.essence.business.xqh.dao.dao.fhybdd.StPptnRDao;
import com.essence.business.xqh.dao.dao.fhybdd.StStbprpBDao;
import com.essence.business.xqh.dao.dao.fhybdd.StStbprpPartitionDao;
import com.essence.business.xqh.dao.dao.fhybdd.WrpRsrBsinDao;
import com.essence.business.xqh.dao.dao.realtimemonitor.TRiverRODao;
import com.essence.business.xqh.dao.dao.realtimemonitor.TRsvrRDao;
import com.essence.business.xqh.dao.entity.fhybdd.StPptnR;
import com.essence.business.xqh.dao.entity.fhybdd.StStbprpB;
import com.essence.business.xqh.dao.entity.fhybdd.StStbprpPartition;
import com.essence.business.xqh.dao.entity.fhybdd.WrpRsrBsin;
import com.essence.business.xqh.dao.entity.realtimemonitor.TRsvrR;
import com.essence.framework.util.StrUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
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
            Date time = DateUtil.getDateByStringNormal("2021/07/11 23:00:00");
            for (int i = 0; i < 1; i++) {
                Date times = DateUtil.getNextHour(time,i);
                for (StStbprpB stStbprpB:newLIst) {
                    StPptnR stPptnR = new StPptnR();
                    stPptnR.setStcd(stStbprpB.getStcd());

                    //取0-4一位随机数 保留一位小数
                    BigDecimal bigDecimal = makeRandom(4f, 0f, 1);
                    double a = bigDecimal.doubleValue()%1;
                    //小数位取0.5 或整数
                    Double drp = bigDecimal.intValue()+(a==0.0?0.0:(a>0.5?1.0:0.5));

//                  Double drp=(int)(Math.random()*6+1)*1.0;
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
            System.out.println("开始同步");
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

    @Autowired
    StStbprpPartitionDao stStbprpPartitionDao;
    @Autowired
    private TRiverRODao tRiverRDao; //河道水情


    @Transactional
    @RequestMapping(value = "/test1", method = RequestMethod.GET)
    public SystemSecurityMessage test1() {
        String areaId = "0";
        List<String> areaIds = new ArrayList<>();
        try {
            if ("0".equals(areaId)){
                areaIds.addAll(stStbprpPartitionDao.findAll().stream().map(StStbprpPartition::getId).collect(Collectors.toList()));
            }else {
                areaIds.add(areaId);
            }
            List<String> sttps = new ArrayList<>();
            sttps.add("ZQ");
            sttps.add("ZZ");
            sttps.add("RR");//水库
            //筛选后的河道站监测站
            List<StStbprpB> allSTBB = stStbprpBDao.findByAreaIdAndSttp(areaIds, sttps);//todo 后面再改 findByAreaIdAndSttp(areaIds, sttps);
            List<StStbprpB> collectHD = allSTBB.stream().filter(t -> "ZQ".equals(t.getSttp()) || "ZZ".equals(t.getSttp())).collect(Collectors.toList());

            List<StStbprpB> collectSK = allSTBB.stream().filter(t -> "RR".equals(t.getSttp())).collect(Collectors.toList());

            List<TRsvrR> tRsvrRS = new ArrayList<>();
            Date time = DateUtil.getDateByStringNormal("2021/02/01 00:00:00");
            int num = 2782637;
            for (int i = 0; i < 720; i++) {
                Date times = DateUtil.getNextHour(time,i);
                for (StStbprpB stStbprpB:collectSK) {//ST_RIVER_R

                    TRsvrR rsvrR = new TRsvrR();
                    rsvrR.setStcd(stStbprpB.getStcd());
                    rsvrR.setTm(times);
                    Double RZ =(int)(Math.random()*10+1)*1.0;
                    Double INQ =(int)(Math.random()*10+1)*1.0;
                    Double W =(int)(Math.random()*10+1)*1.0;
                    Double blrz =(int)(Math.random()*10+1)*1.0;
                    Double otq =(int)(Math.random()*10+1)*1.0;
                    rsvrR.setRz(RZ+"");
                    rsvrR.setInq(INQ+"");
                    rsvrR.setW(W+"");
                    rsvrR.setBlrz(blrz+"");
                    rsvrR.setOtq(otq+"");
                    rsvrR.setRwptn("1");
                    rsvrR.setInqdr("1");
                    tRsvrRS.add(rsvrR);
                    /*TRiverR tRiverR = new TRiverR();
                    tRiverR.setStcd(stStbprpB.getStcd());
                    tRiverR.setId(num+"");
                    num++;
                    Double Q=(int)(Math.random()*10+30)*1.5;
                    Double Z=(int)(Math.random()*10+1)*1.0;
                    tRiverR.setQ(Q+"");
                    tRiverR.setZ(Z+"");
                    tRiverR.setTm(times);
                    tRiverR.setType("2");
                    tRiverRS.add(tRiverR);*/
                }

            }
            System.out.println("入库中…………");
            if(tRsvrRS.size()>0)
                tRsvrRDao.saveAll(tRsvrRS);
            return SystemSecurityMessage.getSuccessMsg("数据处理成功！");
        }catch (Exception e){
            e.printStackTrace();
            return SystemSecurityMessage.getFailMsg("数据处理器失败！");

        }
    }

    /**
     * 生成指定范围，指定小数位数的随机数
     * @param max 最大值
     * @param min 最小值
     * @param scale 小数位数
     * @return
     */
    private static BigDecimal makeRandom(float max,float min,int scale){
        BigDecimal cha = new BigDecimal(Math.random() * (max-min) + min);
        return cha.setScale(scale,BigDecimal.ROUND_HALF_UP);//保留 scale 位小数，并四舍五入
    }

    public static void main(String[] args) {
        for (int i = 0; i < 10; i++) {
            BigDecimal bigDecimal = makeRandom(4f, 0f, 1);
            double a = bigDecimal.doubleValue()%1;
            Double b = bigDecimal.intValue()+(a==0.0?0.0:(a>0.5?1.0:0.5));
            System.out.println(b);
        }
    }
}
