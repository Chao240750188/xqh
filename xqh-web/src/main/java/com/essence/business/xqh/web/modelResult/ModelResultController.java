package com.essence.business.xqh.web.modelResult;

import com.essence.business.xqh.api.modelResult.ModelResultService;
import com.essence.business.xqh.api.modelResult.dto.GridResultDto;
import com.essence.business.xqh.common.returnFormat.SystemSecurityMessage;
import com.essence.business.xqh.web.modelResult.dto.ModelResultParamDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/modelResult")
public class ModelResultController {
    @Autowired
    private ModelResultService modelResultService;

    @RequestMapping(value = "getResultMaxDepthToPicture", method = RequestMethod.POST)
    public SystemSecurityMessage getResultMaxDepthToPicture(@RequestBody ModelResultParamDto paramDto) {

        try {
            String csvFilePath = paramDto.getCsvFilePath();
            BufferedReader br = new BufferedReader(new FileReader(csvFilePath));
            //水深数据
            String line = "";
            Integer count = 0;
            List<GridResultDto> gridResultDtoArrayList = new ArrayList<GridResultDto>();
            while ((line = br.readLine()) != null) { // 逐行读入除表头的数据
                if (count == 0) {

                } else{
                    String[] values = line.split(",");//水深数据
                    GridResultDto gridResultDto = new GridResultDto();
                    if (values.length>=4){
                        long gridId = Long.parseLong(values[0]);
                        double depth = Double.parseDouble(values[2]);
                        gridResultDto.setGridId(gridId);
                        gridResultDto.setDepth(depth);
                        gridResultDtoArrayList.add(gridResultDto);
                    }
                }
                count++;
            }
            modelResultService.getResultMaxDepthToPicture(gridResultDtoArrayList,"MODEL_HSFX_01","Xqh1_Guojia_50",1);
            return SystemSecurityMessage.getSuccessMsg("生成成功");

        }catch (Exception e){
            e.printStackTrace();
            return SystemSecurityMessage.getFailMsg("生成失败！");

        }
    }
}
