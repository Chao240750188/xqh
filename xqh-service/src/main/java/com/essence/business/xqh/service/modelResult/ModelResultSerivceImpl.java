package com.essence.business.xqh.service.modelResult;

import com.essence.business.xqh.api.modelResult.ModelResultService;
import com.essence.business.xqh.api.modelResult.dto.GridResultDto;
import com.essence.business.xqh.common.util.CopyFile;
import com.essence.business.xqh.common.util.ExportMethodResultDto;
import com.essence.business.xqh.common.util.GisPathConfigurationUtil;
import com.essence.business.xqh.common.util.gis.GisUtil;
import com.essence.framework.util.StrUtil;
import com.linuxense.javadbf.DBFDataType;
import com.linuxense.javadbf.DBFField;
import com.linuxense.javadbf.DBFUtils;
import com.linuxense.javadbf.DBFWriter;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileOutputStream;
import java.util.List;

@Service
public class ModelResultSerivceImpl implements ModelResultService {

    /**
     * 将模型结果生成图片
     * @Author huangxiaoli
     * @Description
     * @Date 15:53 2021/2/1
     * @Param [gridResultDtoList, modelName]
     * @return void
     **/
    @Override
    public void getResultMaxDepthToPicture(List<GridResultDto> gridResultDtoList, String modelId, String planId, Object processNum) {

        //dbf参数
        DBFField fields[] = new DBFField[2];
        fields[0] = new DBFField();
        fields[0].setName("gridId");
        fields[0].setType(DBFDataType.NUMERIC);
        fields[0].setLength(9);

        fields[1] = new DBFField();
        fields[1].setName("depth");
        fields[1].setType(DBFDataType.NUMERIC);
        fields[1].setLength(12);
        fields[1].setDecimalCount(5);

        try {
            System.out.println("当前过程数:" + processNum);
            String dbfFilePath ="";
            String fileName ="";
            if (null!=processNum && !StrUtil.isEmpty(processNum.toString())){//水深过程数据
                dbfFilePath = GisPathConfigurationUtil.getModelGridDbfFilesPath() + "/" + modelId +"/"+planId+ "/" + processNum;//生成shp后的存储路径
                fileName = "elements_depth_" + processNum;
            }else {//最大水深
                dbfFilePath = GisPathConfigurationUtil.getModelGridDbfFilesPath() + "/" + modelId +"/"+planId+ "/maxDepth";//生成shp后的存储路径
                fileName ="elements_maxDepth";
            }
            File filePathFile = new File(dbfFilePath);
            if (!filePathFile.exists()) {
                filePathFile.mkdirs();
            }

            //过程数据文件的名称
            File file = new File(dbfFilePath + "/" + fileName + ".dbf");
            file.createNewFile();

            FileOutputStream fos = new FileOutputStream(file);
            DBFWriter writer = new DBFWriter(fos);
            writer.setFields(fields);
            System.out.println("开始写入过程" + processNum + "的dbf文件");
            //水深数据
            for (int i=0;i<gridResultDtoList.size();i++){
                GridResultDto gridResultDto = gridResultDtoList.get(i);
                Object rowData[] = new Object[2];
                rowData[0] = gridResultDto.getGridId(); //网格
                rowData[1] = gridResultDto.getDepth();//水深
                writer.addRecord(rowData);
            }
            DBFUtils.close(writer);
            System.out.println("过程" + processNum + "的dbf文件生成成功");

            //调用GIS生成图片
            String mxdTemplateAbsolutePath= GisPathConfigurationUtil.getMxdTemplateAbsolutePath(); //mxd文件的文件夹路径
            String exportPictureFormate = GisPathConfigurationUtil.getExportPictureFormate(); //导出图片格式：png
            String maxDepthTemplate = ""; //水深 mxd的文件名
            //shp模板路径
            String shpTemplatePath="";
            switch (modelId){
                case "MODEL_DWX"://单位线模型
                    shpTemplatePath = GisPathConfigurationUtil.getDwxModelShpTempletePath();
                    break;
                case "MODEL_SCS"://SCS模型
                    shpTemplatePath = GisPathConfigurationUtil.getScsModelShpTempletePath();
                    break;
                case "MODEL_HSFX_01"://防洪保护区1
                    shpTemplatePath = GisPathConfigurationUtil.getHsfx01ModelShpTempletePath();
                    maxDepthTemplate=GisPathConfigurationUtil.getMxdTemplateHSFX01();
                    break;
                case "MODEL_HSFX_02"://防洪保护区2
                    shpTemplatePath = GisPathConfigurationUtil.getHsfx02ModelShpTempletePath();
                    maxDepthTemplate=GisPathConfigurationUtil.getMxdTemplateHSFX02();
                    break;
            }


            //图片导出路径
            String outputAbsolutePath = GisPathConfigurationUtil.getOutputPictureAbsolutePath()+"/"+modelId+"/"+planId;
            //动态生成保存图片的文件夹
            File file1 = new File(outputAbsolutePath);
            if (!file1.exists() && !file1.isDirectory()) {
                file1.mkdirs();
            }

            ExportMethodResultDto exportMethodResultDto=null;
            if (null!=processNum && !StrUtil.isEmpty(processNum.toString())){
                //复制shp模板文件
                CopyFile.copyShpFiles(shpTemplatePath, dbfFilePath, "depth_" + processNum); //重命名的文件格式：

                //图片导出路径
                String processOutputAbsolutePath = outputAbsolutePath+"/process" ;
                //动态生成保存图片的文件夹
                File processDirFile = new File(processOutputAbsolutePath);
                if (!processDirFile.exists() && !processDirFile.isDirectory()) {
                    processDirFile.mkdirs();
                }
                //参数1：dbf生成路径   参数2：dbf文件名称  参数3：随意  参数4：随意
                 exportMethodResultDto = GisUtil.exportToPicture(dbfFilePath, fileName, mxdTemplateAbsolutePath, maxDepthTemplate, exportPictureFormate, processOutputAbsolutePath, processNum + "");//水深过程数据
            }else {
                //复制shp模板文件
                CopyFile.copyShpFiles(shpTemplatePath, dbfFilePath, "maxDepth" ); //重命名的文件格式：
                //参数1：dbf生成路径   参数2：dbf文件名称  参数3：随意  参数4：随意
                exportMethodResultDto = GisUtil.exportToPicture(dbfFilePath, fileName, mxdTemplateAbsolutePath, maxDepthTemplate, exportPictureFormate, outputAbsolutePath, "maxDepth");//最大水深
            }
            //获取生成图片的状态
            if (null!=exportMethodResultDto){
                boolean flag=true;
                String status="";
                while (flag){
                    String exportToPictureStatus = GisUtil.getExportToPictureStatus(exportMethodResultDto);
                    status=exportToPictureStatus;
                    if ("true".equals(exportToPictureStatus)){
                        flag=false;
                    }
                    if ("failed".equals(exportToPictureStatus)){
                        flag=false;

                    }
                    Thread.sleep(2000);
                }

                if ("true".equals(status)){
                    System.out.println("图片导出成功！");
                }else if ("failed".equals(status)){
                    new RuntimeException("图片导出失败!");
                }

            }else{
                System.out.println("导出图片失败了");
                new RuntimeException("图片导出失败!");
            }

            //删除过程数据的excel文件
            //CopyFile.deleteSpecialFile(dbfFilePath,processNum); //删除过程数据的shp(包括dbf)文件

        } catch (Exception e) {
            System.out.println("导出图片报错了");
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
    }
}
