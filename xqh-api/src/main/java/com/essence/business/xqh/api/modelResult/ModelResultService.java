package com.essence.business.xqh.api.modelResult;

import com.essence.business.xqh.api.modelResult.dto.GridResultDto;

import java.util.List;

public interface ModelResultService {

    /**
     * 将模型结果生成图片
     * @Author huangxiaoli
     * @Description
     * @Date 15:53 2021/2/1
     * @Param [gridResultDtoList, modelName]
     * @return void
     **/
    public void getResultMaxDepthToPicture(List<GridResultDto> gridResultDtoList,String modelId,String planId,Object processNum);

    public void getCsnlResultMaxDepthToPicture(List<GridResultDto> gridResultDtoList,String planId,Object processNum);
}
