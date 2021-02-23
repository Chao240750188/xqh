package com.essence.business.xqh.service.modelResult;

import com.essence.business.xqh.api.modelResult.ModelResultService;
import com.essence.business.xqh.api.modelResult.PlanProcessDataService;
import com.essence.business.xqh.api.modelResult.dto.GridResultDto;
import com.essence.business.xqh.common.thread.HandlerThreadFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.*;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@Service
public class PlanProcessDataServiceImpl implements PlanProcessDataService {

    @Autowired
    ModelResultService modelResultService;

    private Lock lock = new ReentrantLock(); //锁

    /**
     * 生成水深过程图片
     *
     * @param filePath
     */

    @Override
    public void readDepthCsvFile(String filePath, String dataType, String modelId, String planId) throws Exception {
        Integer corePoolSize = 4; //主线程数 ，设置为4
        Integer maxPoolSize = 5;   //线程池最大线程数量，设置为5
        Integer queueCapacity = 300; //队列中的线程数，设置为300
        Integer keepAliveTime = 60; //线程闲置后存活时间,单位：秒,设置为60
        //设置线程池
        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(corePoolSize, maxPoolSize, keepAliveTime, TimeUnit.SECONDS, new ArrayBlockingQueue<Runnable>(queueCapacity), new HandlerThreadFactory(), new ThreadPoolExecutor.AbortPolicy());
        //解析模型结果文件数据
        Map<String, List<GridResultDto>> dataListMap = null;
        //判断是过程数据还是结果数据
        if ("process".equals(dataType)) {
            dataListMap = readModelProcessCsvFileData(filePath);
        } else {
            dataListMap = readModelMaxDepthCsvFileData(filePath);
        }
        //如果有输出数据 - 过程数据  或 最大水深数据
        if (dataListMap.size() > 0) {
            for (Map.Entry<String, List<GridResultDto>> entry : dataListMap.entrySet()) {
                String processNum = entry.getKey();
                List<GridResultDto> list = entry.getValue();
                //调用Gis生成图片
                //启动线程
                threadPoolExecutor.execute(new Runnable() {
                    @Override
                    public void run() {
                        if ("process".equals(dataType)) {
                            System.out.println("处理过程数据：第" + processNum + "个过程开始");
                            modelResultService.getResultMaxDepthToPicture(list, modelId, planId, processNum);
                        } else {
                            System.out.println("处理最大水深数据,处理最大水深开始");
                            modelResultService.getResultMaxDepthToPicture(list, modelId, planId, null);
                        }
                    }
                });
            }
                threadPoolExecutor.shutdown(); //关闭线程池，此时不会再有任务加入到线程池中

                //判断线程池中的线程是否执行完毕
                Boolean flag = true;
                while (flag) {
                    if (threadPoolExecutor.isTerminated()) {
                        System.out.println("线程全部运行结束了");
                        flag = false;
                        //生成模型图片解析完成文件
                        try{
                            File file = new File(filePath+File.separator+"pic.txt");
                            file.createNewFile();
                        }catch (Exception e){
                        }
                        break;
                    }
                    System.out.println("线程池中线程数目：" + threadPoolExecutor.getPoolSize() + "，队列中等待执行的任务数目：" + threadPoolExecutor.getQueue().size() + "，已执行完的任务数目：" + threadPoolExecutor.getCompletedTaskCount());
                    try {
                        Thread.sleep(6000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
    }

    /**
     * 解析模型输出过程文件Grid process csv
     *
     * @param hsfx_model_template_output
     * @return
     */
    private Map<String, List<GridResultDto>> readModelProcessCsvFileData(String hsfx_model_template_output) {
        //封装每个过程数据
        Map<String, List<GridResultDto>> gridResultMap = new LinkedHashMap<>();
        String grid_process_csv = hsfx_model_template_output + File.separator + "erwei" + File.separator + "process.csv";
        try {
            FileReader fileReader = new FileReader(grid_process_csv);
            if (fileReader != null) {
                BufferedReader reader = new BufferedReader(new FileReader(grid_process_csv));//换成你的文件名
                //第一行信息，为标题信息，不用，如果需要，注释掉
                reader.readLine();
                String line = null;
                List<List<String>> datas = new ArrayList<>();
                while ((line = reader.readLine()) != null) {
                    String item[] = line.split(",");//CSV格式文件为逗号分隔符文件，这里根据逗号切分
                    datas.add(Arrays.asList(item));
                }
                for (int i = 0; i < datas.size(); i++) {
                    //将行列数据封装成过程
                    List<String> rowDataList = datas.get(i);
                    //网格id
                    String gridId = rowDataList.get(0);
                    for (int j = 2; j < rowDataList.size(); j++) {
                        List<GridResultDto> gridResultDtoList = gridResultMap.get((j - 1) + "");
                        if (gridResultDtoList == null) {
                            gridResultDtoList = new ArrayList<>();
                        }
                        String depth = rowDataList.get(j);
                        GridResultDto gridResultDto = new GridResultDto(Long.parseLong(gridId), Double.parseDouble(depth));
                        gridResultDtoList.add(gridResultDto);
                        gridResultMap.put((j - 1) + "", gridResultDtoList);
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("解析输出文件Grid process csv失败：" + e.getMessage());
            e.printStackTrace();
        }
        return gridResultMap;
    }

    /**
     * 解析模型输出过最大水深文件Grid maxDepth csv
     *
     * @param hsfx_model_template_output
     * @return
     */

    private Map<String, List<GridResultDto>> readModelMaxDepthCsvFileData(String hsfx_model_template_output) {
        //封装每个过程数据
        Map<String, List<GridResultDto>> gridResultMap = new LinkedHashMap<>();
        String grid_process_csv = hsfx_model_template_output + File.separator + "erwei" + File.separator + "result.csv";
        try {
            FileReader fileReader = new FileReader(grid_process_csv);
            if (fileReader != null) {
                BufferedReader reader = new BufferedReader(new FileReader(grid_process_csv));//换成你的文件名
                //第一行信息，为标题信息，不用，如果需要，注释掉
                reader.readLine();
                String line = null;
                List<List<String>> datas = new ArrayList<>();
                while ((line = reader.readLine()) != null) {
                    String item[] = line.split(",");//CSV格式文件为逗号分隔符文件，这里根据逗号切分
                    datas.add(Arrays.asList(item));
                }
                for (int i = 0; i < datas.size(); i++) {
                    //将行列数据封装成过程
                    List<String> rowDataList = datas.get(i);
                    List<GridResultDto> gridResultDtoList = gridResultMap.get("maxDepth");
                    if (gridResultDtoList == null) {
                        gridResultDtoList = new ArrayList<>();
                    }
                    //网格id
                    String gridId = rowDataList.get(0);
                    String depth = rowDataList.get(2);
                    GridResultDto gridResultDto = new GridResultDto(Long.parseLong(gridId), Double.parseDouble(depth));
                    gridResultDtoList.add(gridResultDto);
                    gridResultMap.put("maxDepth", gridResultDtoList);
                }
            }
        } catch (Exception e) {
            System.out.println("解析输出文件Grid process csv失败：" + e.getMessage());
            e.printStackTrace();
        }
        return gridResultMap;
    }
}

