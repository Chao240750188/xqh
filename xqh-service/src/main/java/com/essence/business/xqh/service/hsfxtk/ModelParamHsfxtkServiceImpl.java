package com.essence.business.xqh.service.hsfxtk;

import com.essence.business.xqh.api.fhybdd.dto.YwkModelDto;
import com.essence.business.xqh.api.hsfxtk.ModelParamHsfxtkService;
import com.essence.business.xqh.dao.dao.fhybdd.YwkModelDao;
import com.essence.business.xqh.dao.entity.fhybdd.YwkModel;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

public class ModelParamHsfxtkServiceImpl implements ModelParamHsfxtkService {


    @Autowired
    YwkModelDao ywkModelDao;

    @Override
    public List<YwkModelDto> getModelList() {
        List<YwkModel> hsfx = ywkModelDao.getYwkModelByModelType("HSFX");
        List<YwkModelDto> results = new ArrayList<>();
        BeanUtils.copyProperties(hsfx,results);
        return results;
    }
}
