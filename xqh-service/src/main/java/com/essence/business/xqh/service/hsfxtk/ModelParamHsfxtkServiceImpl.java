package com.essence.business.xqh.service.hsfxtk;

import com.essence.business.xqh.api.fhybdd.dto.YwkModelDto;
import com.essence.business.xqh.api.hsfxtk.ModelParamHsfxtkService;
import com.essence.business.xqh.api.hsfxtk.dto.*;
import com.essence.business.xqh.dao.dao.fhybdd.YwkModelDao;
import com.essence.business.xqh.dao.dao.hsfxtk.*;
import com.essence.business.xqh.dao.entity.fhybdd.YwkModel;
import com.essence.business.xqh.dao.entity.hsfxtk.*;
import com.essence.framework.util.StrUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.sql.Timestamp;
import java.util.*;
import java.util.stream.Collectors;
@Service
public class ModelParamHsfxtkServiceImpl implements ModelParamHsfxtkService {


    @Autowired
    YwkModelDao ywkModelDao;

    @Autowired
    YwkModelRoughnessParamDao ywkModelRoughnessParamDao;//模型参数，河道保护区操率


    @Autowired
    YwkRiverRoughnessParamDao ywkRiverRoughnessParamDao;//河道模型操率参数

    @Autowired
    YwkModelRiverRoughnessRlDao ywkModelRiverRoughnessRlDao;//河道模型操率关联表，中间表

    @Autowired
    YwkRiverRoughnessBasicDao ywkRiverRoughnessBasicDao;//河道操率基本信息表

    @Autowired
    YwkBreakBasicDao ywkBreakBasicDao;//溃口基本信息表

    @Autowired
    YwkPlaninFloodBreakDao ywkPlaninFloodBreakDao;//溃口方案表

    @Override
    public List<YwkModelDto> getModelList() {
        List<YwkModel> hsfx = ywkModelDao.getYwkModelByModelType("HSFX");
        List<YwkModelDto> results = new ArrayList<>();
        for (YwkModel model : hsfx){
            YwkModelDto dto = new YwkModelDto();
            BeanUtils.copyProperties(model,dto);
            results.add(dto);
        }
        return results;
    }


    @Override
    public List<YwkModelRoughnessParamDto> getModelParamList(String modelId) {

        List<YwkModelRoughnessParam> ywkModelRoughnessParamByModelId = ywkModelRoughnessParamDao.findYwkModelRoughnessParamByModelId(modelId);
        List<YwkModelRoughnessParamDto> result = new ArrayList<>(); //参数表，
        for (YwkModelRoughnessParam param : ywkModelRoughnessParamByModelId){
            YwkModelRoughnessParamDto dto = new YwkModelRoughnessParamDto();
            BeanUtils.copyProperties(param,dto);
            result.add(dto);
        }
        return result;
    }


    @Override
    public List<YwkRiverRoughnessParamDto> getModelRoughParamList(String paramId) {
        List<YwkRiverRoughnessParam> ywkRiverRoughnessParams = ywkRiverRoughnessParamDao.findsByRoughnessParamId(paramId);
        List<YwkRiverRoughnessParamDto> results = new ArrayList<>();
        for (YwkRiverRoughnessParam source:ywkRiverRoughnessParams){
            YwkRiverRoughnessParamDto target = new YwkRiverRoughnessParamDto();
            BeanUtils.copyProperties(source,target);
            results.add(target);
        }
        return results;
    }


    @Override
    public List<Map<String,Object>> beforeSaveRoughness(String modelId) {

        List<YwkModelRiverRoughnessRl> byModelId = ywkModelRiverRoughnessRlDao.findByModelId(modelId);
        List<String> collect = byModelId.stream().map(YwkModelRiverRoughnessRl::getRiverRoughnessid).collect(Collectors.toList());
        //获取操率基本信息
        List<YwkRiverRoughnessBasic> allById = ywkRiverRoughnessBasicDao.findAllByIdsOrderByMileage(collect);
        List<Map<String,Object>> result = new ArrayList<>();
        for (YwkRiverRoughnessBasic m:allById){
            Map<String,Object> map = new HashMap<>();
            map.put("mileage",m.getMileage());
            map.put("roughness",m.getRoughness());
            map.put("isFix",1);//1不可修改，不可新增
            result.add(map);
        }
        return result;
    }


    @Transactional
    @Override
    public void saveRoughness(YwkParamVo ywkParamVo) {

        YwkModelRoughnessParam ywkModelRoughnessParam = new YwkModelRoughnessParam();
        if (ywkParamVo.getId()!=null){
            ywkModelRoughnessParam.setRoughnessParamid(ywkParamVo.getId());
        }else {
            ywkModelRoughnessParam.setRoughnessParamid(StrUtil.getUUID());
        }
        ywkModelRoughnessParam.setIdmodelId(ywkParamVo.getModelId());
        ywkModelRoughnessParam.setGridSynthesizeRoughness(ywkParamVo.getGridSynthesizeRoughness());
        ywkModelRoughnessParam.setRoughnessParamnm(ywkParamVo.getRoughnessParamnm());
        ywkModelRoughnessParam.setModitime(new Timestamp(new Date().getTime()));

        ywkModelRoughnessParamDao.save(ywkModelRoughnessParam);
        ywkModelRoughnessParamDao.flush();
        List<YwkRoughnessVo> ywkRoughnessVos = ywkParamVo.getYwkRoughnessVos();

        List<YwkRiverRoughnessParam> list = new ArrayList<>();
        if (!CollectionUtils.isEmpty(ywkRoughnessVos)){
            for (YwkRoughnessVo vo : ywkRoughnessVos){
                YwkRiverRoughnessParam ywkRiverRoughnessParam = new YwkRiverRoughnessParam();
                ywkRiverRoughnessParam.setId(StrUtil.getUUID());
                ywkRiverRoughnessParam.setIsFix(vo.getIsFix());
                ywkRiverRoughnessParam.setRoughnessParamid(ywkModelRoughnessParam.getRoughnessParamid());
                ywkRiverRoughnessParam.setRoughness(vo.getRoughness());
                ywkRiverRoughnessParam.setMileage(vo.getMileage());
                list.add(ywkRiverRoughnessParam);
            }
        }
        ywkRiverRoughnessParamDao.deleteByRoughnessParamid(ywkModelRoughnessParam.getRoughnessParamid());
        /**
         * delete之后，delete语句并没有执行，调用flush让他先把更改作用到数据库，再去执行save插入操作，
         * 虽然此刻事务依旧没有提交，但是delete语句已经执行
         */
        ywkRiverRoughnessParamDao.flush();
        ywkRiverRoughnessParamDao.saveAll(list);

    }


    @Transactional
    @Override
    public void deleteRoughness(String roughness) {

        ywkModelRoughnessParamDao.deleteById(roughness);
        ywkRiverRoughnessParamDao.deleteByRoughnessParamid(roughness);
    }


    @Override
    public List<YwkBreakBasicDto> getBreakList(String modelId) {

        List<YwkBreakBasic> ywkBreakBasics = ywkBreakBasicDao.findsByModelId(modelId);
        List<YwkBreakBasicDto> results = new ArrayList<>();
        for (YwkBreakBasic source : ywkBreakBasics){
            YwkBreakBasicDto target = new YwkBreakBasicDto();
            BeanUtils.copyProperties(source,target);
            results.add(target);
        }
        return results;
    }


    @Override
    public void saveBreak(BreakVo breakDto) {

        YwkPlaninFloodBreak target = new YwkPlaninFloodBreak();
        BeanUtils.copyProperties(breakDto,target);
        target.setId(StrUtil.getUUID());
        ywkPlaninFloodBreakDao.save(target);
    }
}
