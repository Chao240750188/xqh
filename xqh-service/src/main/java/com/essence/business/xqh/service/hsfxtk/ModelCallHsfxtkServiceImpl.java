package com.essence.business.xqh.service.hsfxtk;

import com.essence.business.xqh.api.hsfxtk.ModelCallHsfxtkService;
import com.essence.business.xqh.api.hsfxtk.dto.PlanInfoHsfxtkVo;
import com.essence.business.xqh.common.util.DateUtil;
import com.essence.business.xqh.common.util.PropertiesUtil;
import com.essence.business.xqh.dao.dao.fhybdd.YwkPlaninfoDao;
import com.essence.business.xqh.dao.dao.hsfxtk.YwkPlanOutputGridMaxDao;
import com.essence.business.xqh.dao.dao.hsfxtk.YwkPlanOutputGridProcessDao;
import com.essence.business.xqh.dao.entity.fhybdd.YwkPlaninfo;
import com.essence.business.xqh.dao.entity.hsfxtk.YwkPlanOutputGridMax;
import com.essence.business.xqh.dao.entity.hsfxtk.YwkPlanOutputGridMaxPK;
import com.essence.business.xqh.dao.entity.hsfxtk.YwkPlanOutputGridProcess;
import com.essence.framework.util.StrUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.io.*;
import java.sql.Timestamp;
import java.util.*;

@Service
public class ModelCallHsfxtkServiceImpl implements ModelCallHsfxtkService {

    @Autowired
    private YwkPlaninfoDao ywkPlaninfoDao;
    @Autowired
    YwkPlanOutputGridProcessDao ywkPlanOutputGridProcessDao;
    @Autowired
    private YwkPlanOutputGridMaxDao ywkPlanOutputGridMaxDao;

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
        ywkPlaninfo.setdOpensourcestarttime(startTime);
        ywkPlaninfo.setdOpensourceendtime(endTIme);
        ywkPlaninfo.setnCreatetime(DateUtil.getCurrentTime());
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
        String grid_process_csv = hsfx_model_template_output+File.separator+"erwei"+File.separator+"process.csv";
        try {
            BufferedReader reader = new BufferedReader(new FileReader(grid_process_csv));//换成你的文件名
            reader.readLine();//第一行信息，为标题信息，不用，如果需要，注释掉
            String line = null;
            List<List<String>> datas = new ArrayList<>();
            while((line=reader.readLine())!=null){
                String item[] = line.split(",");//CSV格式文件为逗号分隔符文件，这里根据逗号切分
                datas.add(Arrays.asList(item));
            }
            for (List<String> data:datas){
                List<String> newList = data.subList(2,data.size());
                for(int i=0;i<newList.size();i++){
                    YwkPlanOutputGridProcess ywkPlanOutputGridProcess = new YwkPlanOutputGridProcess();
                    ywkPlanOutputGridProcess.getPk().setnPlanid(planinfo.getnPlanid());
                    ywkPlanOutputGridProcess.getPk().setGridId(Long.parseLong(data.get(0)));
                    String str = newList.get(i);
                    Long stepNew = step*i;
                    Date newDate = DateUtil.getNextMinute(startTime,stepNew.intValue());
                    ywkPlanOutputGridProcess.setAbsoluteTime(new Timestamp(newDate.getTime()));
                    ywkPlanOutputGridProcess.getPk().setRelativeTime(stepNew);
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

    /**
     * 解析最大淹没（二维模型淹没统计结果）数据入库
     * @param planId
     * @return
     */
    public List<YwkPlanOutputGridMax> saveGridMaxToDb(String planId) throws FileNotFoundException {
        List<YwkPlanOutputGridMax> list = new ArrayList<>();
        //查询方案基本信息
        YwkPlaninfo planInfo = ywkPlaninfoDao.findOneById(planId);
        Date startTime = planInfo.getdCaculatestarttm();
        Date endTime = planInfo.getdCaculateendtm();
        Long step = planInfo.getnOutputtm();
        //读取最大水深文件
        //输出参数文件
        String SWYB_PATH = PropertiesUtil.read("/filePath.properties").getProperty("HSFX_MODEL");
        //解析二维输出最大水深文件
        String MODEL_OUTPUT_MAX_FILE_PATH = SWYB_PATH + File.separator + PropertiesUtil.read("/filePath.properties").getProperty("MODEL_OUTPUT")
                + File.separator + planId+File.separator+"erwei/result.csv";//模型输出最大水深文件路径
        List<String> dataList = null;
        try{
            dataList= readCSV(MODEL_OUTPUT_MAX_FILE_PATH);
        }catch (Exception e){
            System.out.println("读取结果文件失败");
        }
        if(dataList!=null && dataList.size()>0){
            for (int i = 1; i < dataList.size(); i++) {
                //每行每列数据
                String[] dataArray = dataList.get(i).split(",");
                YwkPlanOutputGridMax gridMaxDto = new YwkPlanOutputGridMax();
                list.add(gridMaxDto);
                YwkPlanOutputGridMaxPK pk = new YwkPlanOutputGridMaxPK(planId,Long.parseLong(dataArray[0]));
                gridMaxDto.setIdCLass(pk);
                gridMaxDto.setGridSurfaceElevation(Double.parseDouble(dataArray[1]));//网格表面高程
                gridMaxDto.setMaxWaterDepth(Double.parseDouble(dataArray[2]));//最大水深
                gridMaxDto.setAbsoluteTime(Long.parseLong(dataArray[3]));//最大水深相对时间
                gridMaxDto.setRelativeTime(DateUtil.getNextMinute(startTime,Integer.parseInt(dataArray[3])));//最大水深绝对时间
            }
        }
        if(list.size()>0){
            List<YwkPlanOutputGridMax> returnList = ywkPlanOutputGridMaxDao.saveAll(list);
            return returnList;
        }
        return list;
    }

    /**
     * 读取csv文件内容
     * @param filePath
     * @return
     * @throws IOException
     */
    private List<String> readCSV(String filePath) throws IOException {
        List dataList = new ArrayList();
        BufferedReader br = null;
        InputStreamReader isr = null;
        try {
            File file = new File(filePath);
            if (file.isFile() && file.exists()) {
                isr = new InputStreamReader(new FileInputStream(file), "utf-8");
                br = new BufferedReader(isr);
                String lineTxt = null;
                while ((lineTxt = br.readLine()) != null) {
                    dataList.add(lineTxt);
                }
                return dataList;
            } else {
                System.out.println("文件不存在");
            }
        } catch (Exception e) {
            System.out.println("文件错误");
        } finally {
            if (br != null) {
                br.close();
            }
            if (isr != null) {
                isr.close();
            }
        }
        return dataList;
    }

    @Override
    public void test(String planId) throws Exception {
        saveGridProcessToDb(planId);
    }

}
