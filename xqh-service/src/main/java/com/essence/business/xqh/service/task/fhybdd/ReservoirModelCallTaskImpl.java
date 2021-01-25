package com.essence.business.xqh.service.task.fhybdd;

import com.essence.business.xqh.api.task.fhybdd.ReservoirModelCallTask;
import com.essence.business.xqh.common.util.FileUtil;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
public class ReservoirModelCallTaskImpl implements ReservoirModelCallTask {

    @Async
    @Override
    public CompletableFuture<String> text(String name,int i) {
        System.out.println(Thread.currentThread());
        System.out.println(2/0);
        return CompletableFuture.completedFuture("true"+name);

    }


    //    @Async
//    @Override
//    public CompletableFuture<String> reservoirModelCall(String name) throws Exception {
//
//        System.out.println("水库调用模型开始"+name);
//        System.out.println(Thread.currentThread());
//
//
//        return CompletableFuture.completedFuture("true"+name);
//    }


    @Async
    @Override
    public CompletableFuture<Integer> reservoirModelCall(String skdd_run,String skdd_model_template, String skdd_model_template_input, String skdd_model_template_output,Long step, List<String> rkll,String swyb_model_template) {

        //初始输入
        writeDataToInputCSSR(skdd_model_template,skdd_model_template_input,step,rkll);
        //编写水库调度，config文件
        writeDataToSKDDConfig(skdd_run,skdd_model_template_input,skdd_model_template_output);
        //调用水库调度文件
        runModelExe(skdd_run + File.separator + "startUp.bat");

        //文件copy到水文模型到入参里
        FileUtil.copyFile(skdd_model_template_output+File.separator + "result.txt",swyb_model_template,true);
        return CompletableFuture.completedFuture(1);
    }


    /**
     * 调用模型运行模型文件
     */
    private void runModelExe(String modelRunPath) {
        BufferedReader br = null;
        BufferedReader brError = null;
        try {
            // 执行exe cmd可以为字符串(exe存放路径)也可为数组，调用exe时需要传入参数时，可以传数组调用(参数有顺序要求)
            Process p = Runtime.getRuntime().exec(modelRunPath);
            String line = null;
            br = new BufferedReader(new InputStreamReader(p.getInputStream()));
            brError = new BufferedReader(new InputStreamReader(p.getErrorStream()));
            System.out.println("======水库调度路径"+modelRunPath);
            // while ((line = br.readLine()) != null || (line = brError.readLine()) != null)
            // {
            while ((line = brError.readLine()) != null) {
                // 输出exe输出的信息以及错误信息
                System.out.println("==="+line);
            }
            System.out.println("水库模型调用成功！"+modelRunPath);

        } catch (Exception e) {
            System.out.println("水库模型调用失败！"+modelRunPath);
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 写水库调度的config文件
     * @param skdd_run
     * @param skdd_model_template_input
     * @param skdd_model_template_output
     */
    private void writeDataToSKDDConfig(String skdd_run, String skdd_model_template_input, String skdd_model_template_output) {
        String configUrl = skdd_run + File.separator + "config.txt";
        String shuiwei = "shuiwei&&"+skdd_model_template_input+File.separator+"shuiwei.txt";
        String kurong = "kurong&&"+skdd_model_template_input+File.separator+"kurong.txt";
        String inputCSSRUrl = "chushishuju&&" + skdd_model_template_input + File.separator + "chushishuju.txt";
        String outputUrl = "result&&" + skdd_model_template_output + File.separator + "result.txt";

        List<String> result = new ArrayList<String>();
        result.add(shuiwei);
        result.add(kurong);
        result.add(inputCSSRUrl);
        result.add(outputUrl);
//        try {
//            BufferedReader br = new BufferedReader(new FileReader(configUrl));//构造一个BufferedReader类来读取文件
//            String s = null;
//            while ((s = br.readLine()) != null) {//使用readLine方法，一次读一行
//                result.add(s);
//            }
//            br.close();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        result.set(2, inputCSSRUrl);
//        result.set(3, outputUrl);
        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(configUrl, false)); // 附加
            // 添加新的数据行
//            for (String s : result) {
//                bw.write(s);
//                bw.newLine();
//            }
            for (int i=0;i<result.size();i++){
                String s = result.get(i);
                if (i==result.size()-1){
                    bw.write(s);
                }else {
                    bw.write(s);
                    bw.newLine();
                }
            }
            bw.close();
        } catch (FileNotFoundException e) {
            // File对象的创建过程中的异常捕获
            e.printStackTrace();
        } catch (IOException e) {
            // BufferedWriter在关闭对象捕捉异常
            e.printStackTrace();
        }

    }

    /**
     * 水库调度的入参
     * @param skdd_model_template_input
     * @param step
     * @param rkll
     */
    private void writeDataToInputCSSR(String skdd_model_template,String skdd_model_template_input, Long step , List<String> rkll) {
        String chushishuju = skdd_model_template_input+ File.separator+"chushishuju.txt";
        String chushishujuTemplate = skdd_model_template+File.separator+"chushishuju.txt";

        List<String> result = new ArrayList<>();
        try {
            BufferedReader br = new BufferedReader(new FileReader(chushishujuTemplate));//构造一个BufferedReader类来读取文件
            String s = null;
            while ((s = br.readLine()) != null) {//使用readLine方法，一次读一行
                result.add(s);
            }
            br.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        BufferedWriter bw = null;
        try {
            bw = new BufferedWriter(new FileWriter(new File(chushishuju)));
            bw.write(step+"");//步数
            bw.newLine();
            bw.write(result.get(1));//来流初始水位  TODO 写死
            bw.newLine();
            bw.write(result.get(2));//初始下泄流量 TODO 写死
            bw.newLine();
//            for (String s : rkll) {
//                bw.write(s);
//                bw.newLine();
//            }
            for (int i=0;i<rkll.size();i++){
                String s = rkll.get(i);
                if (i==rkll.size()-1){
                    bw.write(s);
                }else {
                    bw.write(s);
                    bw.newLine();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("read errors :" +e.getMessage() );
        } finally {
            try {
                bw.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }
}
