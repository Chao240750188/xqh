package com.essence.business.xqh.service.hsfxtk;

import com.essence.business.xqh.api.hsfxtk.ModelCallHsfxtkService;
import com.essence.business.xqh.api.hsfxtk.dto.PlanInfoHsfxtkVo;
import com.essence.business.xqh.common.util.DateUtil;
import com.essence.business.xqh.common.util.PropertiesUtil;
import com.essence.business.xqh.dao.dao.fhybdd.YwkPlaninfoDao;
import com.essence.business.xqh.dao.dao.hsfxtk.YwkPlanOutputGridProcessDao;
import com.essence.business.xqh.dao.entity.fhybdd.YwkPlaninfo;
import com.essence.business.xqh.dao.entity.hsfxtk.YwkPlanOutputGridProcess;
import com.essence.framework.util.StrUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.sql.Timestamp;
import java.util.*;

@Service
public class ModelCallHsfxtkServiceImpl implements ModelCallHsfxtkService {

    @Autowired
    private YwkPlaninfoDao ywkPlaninfoDao;
    @Autowired
    YwkPlanOutputGridProcessDao ywkPlanOutputGridProcessDao;


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


    @Override
    public Integer saveGridProcessToDb(String planId) {

        YwkPlaninfo planinfo = ywkPlaninfoDao.findOne(planId);
        if (planinfo == null){
            System.out.println("计划planid没有找到记录");
            return -1;
        }
        String hsfx_path = PropertiesUtil.read("/filePath.properties").getProperty("HSFX_MODEL");

        String hsfx_model_template_output = hsfx_path +
                File.separator + PropertiesUtil.read("/filePath.properties").getProperty("MODEL_OUTPUT")
                + File.separator + planId; //输入的地址
        List<YwkPlanOutputGridProcess> results = analysisOfGridProcessCSV(hsfx_model_template_output,planinfo);
        if (CollectionUtils.isEmpty(results)){
            System.out.println("解析输出文件Grid process csv 没有数据或者失败");
            return -1;
        }
        List<YwkPlanOutputGridProcess> ywkPlanOutputGridProcesses = ywkPlanOutputGridProcessDao.saveAll(results);
        if (results.size() == ywkPlanOutputGridProcesses.size()){
            return 1;
        }else {
            return -1;
        }
    }

    /**
     * 解析输出文件Grid process csv
     * @param hsfx_model_template_output
     * @return
     */
    private List<YwkPlanOutputGridProcess> analysisOfGridProcessCSV(String hsfx_model_template_output,YwkPlaninfo planinfo) {
        List<YwkPlanOutputGridProcess> results = new ArrayList<>();
        Date startTime = planinfo.getdCaculatestarttm();//计算开始时间
        Long step = planinfo.getnOutputtm();
        String grid_process_csv = hsfx_model_template_output+File.separator+"process.csv";
        try {
            BufferedReader reader = new BufferedReader(new FileReader(grid_process_csv));//换成你的文件名
            reader.readLine();//第一行信息，为标题信息，不用，如果需要，注释掉
            String line = null;
            List<List<String>> datas = new ArrayList<>();
            while((line=reader.readLine())!=null){
                String item[] = line.split("，");//CSV格式文件为逗号分隔符文件，这里根据逗号切分
                datas.add(Arrays.asList(item));
            }
            for (List<String> data:datas){
                List<String> newList = data.subList(2,data.size());
                for(int i=0;i<newList.size();i++){
                    YwkPlanOutputGridProcess ywkPlanOutputGridProcess = new YwkPlanOutputGridProcess();
                    ywkPlanOutputGridProcess.setnPlanid(planinfo.getnPlanid());
                    ywkPlanOutputGridProcess.setGridId(Long.parseLong(data.get(0)));
                    String str = newList.get(i);
                    Long stepNew = step*i;
                    Date newDate = DateUtil.getNextMinute(startTime,stepNew.intValue());
                    ywkPlanOutputGridProcess.setAbsoluteTime(new Timestamp(newDate.getTime()));
                    ywkPlanOutputGridProcess.setRelativeTime(stepNew);
                    ywkPlanOutputGridProcess.setGridDepth(Double.parseDouble(str));
                    results.add(ywkPlanOutputGridProcess);
                }
            }
        } catch (Exception e) {
            System.out.println("解析输出文件Grid process csv失败："+e.getMessage());
            e.printStackTrace();
        }
        return results;

    }

}
