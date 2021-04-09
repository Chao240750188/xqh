package com.essence.business.xqh.service.dataMaintenance;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.essence.business.xqh.api.dataMaintenance.DataMaintenanceService;
import com.essence.business.xqh.api.dataMaintenance.dto.StSkkrDto;
import com.essence.business.xqh.api.dataMaintenance.dto.WrpRsrBsinTzDto;
import com.essence.business.xqh.dao.dao.fhybdd.*;
import com.essence.business.xqh.dao.entity.fhybdd.*;
import com.essence.framework.jpa.Criterion;
import com.essence.framework.jpa.Paginator;
import com.essence.framework.jpa.PaginatorParam;
import com.essence.framework.util.DateUtil;
import com.essence.framework.util.StrUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 数据维护业务控制层
 */
@Service
public class DataMaintenanceServiceImpl implements DataMaintenanceService {
    //检测站
    @Autowired
    private StStbprpBDao stStbprpBDao;
    //水库
    @Autowired
    private WrpRsrBsinDao wrpRsrBsinDao;
    //库（湖）站防洪指标表
    @Autowired
    private StRsvrfcchBDao stRsvrfcchBDao;
    //库（湖）汛限特征
    @Autowired
    private StRsvrfsrBDao stRsvrfsrBDao;
  //库（湖）汛限特征
    @Autowired
    private StZvarlBDao stZvarlBDao;

    /**
     * 降雨等级
     */
    @Autowired
    private YwkRainLevelDao ywkRainLevelDao;


    /**
     * 数据维护系统-接口层
     */
    @Override
    public Paginator getStbprpbList(PaginatorParam param) {
        List<Criterion> orders = param.getOrders();
        if(orders == null){
            orders = new ArrayList<>();
            param.setOrders(orders);
        }
        Criterion criterion = new Criterion();
        criterion.setFieldName("moditime");
        criterion.setOperator(Criterion.DESC);
        orders.add(criterion);
        return stStbprpBDao.findAll(param);
    }

    /**
     * 新增或编辑测站
     * @param stStbprpB
     * @return
     */
    @Override
    public StStbprpB saveOrUpdataStbprpb(StStbprpB stStbprpB) {
        if(StrUtil.isEmpty(stStbprpB.getStcd())){
            stStbprpB.setStcd(StrUtil.getUUID());
        }
        stStbprpB.setModitime(DateUtil.getCurrentTime());
        return stStbprpBDao.save(stStbprpB);
    }

    /**
     * 删除测站对象信息
     * @param stcd
     */
    @Override
    @Transactional
    public void deleteStbprpb(String stcd) {
        stStbprpBDao.deleteByStcd(stcd);
    }

    /**
     * 分页获取水库列表
     * @param param
     * @return
     */
    @Override
    public Paginator getWrpRsrBsinList(PaginatorParam param) {
        List<Criterion> orders = param.getOrders();
        if(orders == null){
            orders = new ArrayList<>();
            param.setOrders(orders);
        }
        Criterion criterion = new Criterion();
        criterion.setFieldName("dtupdt");
        criterion.setOperator(Criterion.DESC);
        orders.add(criterion);
        return wrpRsrBsinDao.findAll(param);
    }

    /**
     * 新增或编辑水库信息
     * @param wrpRsrBsin
     * @return
     */
    @Override
    public WrpRsrBsin saveOrWrpRsrBsin(WrpRsrBsin wrpRsrBsin) {
        if(StrUtil.isEmpty(wrpRsrBsin.getRscd())){
            wrpRsrBsin.setRscd(StrUtil.getUUID());
        }
        wrpRsrBsin.setDtupdt(DateUtil.getCurrentTime());
        return wrpRsrBsinDao.save(wrpRsrBsin);
    }

    @Override
    @Transactional
    public void deleteWrpRsrBsin(String stcd) {
        wrpRsrBsinDao.deleteByRscd(stcd);
    }

    /**
     * 根据水库编码查询水库特征值
     * @param stcd
     */
    @Override
    public WrpRsrBsinTzDto getWrpRsrBsinTz(String stcd) {
        WrpRsrBsinTzDto wrpRsrBsinTzDto = new WrpRsrBsinTzDto();
        //查询特征值
        StRsvrfcchB stRsvrfcchB = stRsvrfcchBDao.findByStcd(stcd);
        if(stRsvrfcchB==null){
            stRsvrfcchB = new StRsvrfcchB();
            stRsvrfcchB.setStcd(stcd);
        }
        //查询汛限特征
        StRsvrfsrB stRsvrfsrB = stRsvrfsrBDao.findByStcd(stcd);
        if(stRsvrfsrB==null){
            stRsvrfsrB = new StRsvrfsrB();
            stRsvrfsrB.setStcd(stcd);
        }
        wrpRsrBsinTzDto.setStRsvrfcchB(stRsvrfcchB);
        wrpRsrBsinTzDto.setStRsvrfsrB(stRsvrfsrB);
        return wrpRsrBsinTzDto;
    }

    /**
     * 编辑水库特征值
     * @param wrpRsrBsinTzDto
     * @return
     */
    @Override
    @Transactional
    public WrpRsrBsinTzDto saveWrpRsrBsinTz(WrpRsrBsinTzDto wrpRsrBsinTzDto) {
        StRsvrfsrB stRsvrfsrB = wrpRsrBsinTzDto.getStRsvrfsrB();
        StRsvrfcchB stRsvrfcchB = wrpRsrBsinTzDto.getStRsvrfcchB();
        if(stRsvrfcchB!=null){
            stRsvrfcchB.setModiTime(DateUtil.getCurrentTime());
            stRsvrfcchBDao.save(stRsvrfcchB);
        }
        if(stRsvrfsrB!=null){
            stRsvrfsrB.setModiTime(DateUtil.getCurrentTime());
            stRsvrfsrBDao.save(stRsvrfsrB);
        }
        return wrpRsrBsinTzDto;
    }

    @Override
    public Object getWrpRsrKr() {
        JSONArray jsonArray = new JSONArray();
        //查询水库
        List<WrpRsrBsin> rsrBsinList = wrpRsrBsinDao.findAll();
        //查询库容曲线值
        List<StZvarlB> krList = stZvarlBDao.findAllByPtno();
        Map<String, List<StZvarlB>> krMap = krList.stream().collect(Collectors.groupingBy(StZvarlB::getStcd));
        //封装数据
        for(WrpRsrBsin rsrBsin:rsrBsinList){
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("rscd",rsrBsin.getRscd());
            jsonObject.put("rsnm",rsrBsin.getRsnm());
            if(krMap!=null)
                jsonObject.put("dataList",krMap.get(rsrBsin.getRscd()));
            jsonArray.add(jsonObject);
        }
        return jsonArray;
    }

    @Override
    @Transactional
    public Object updateWrpRsrKrList(StSkkrDto stSkkrDto) {
        //删除原来的库容曲线
        stZvarlBDao.deleteByStcd(stSkkrDto.getRscd());
        //存入新的库容曲线
        stZvarlBDao.saveAll(stSkkrDto.getDataList());
        return stSkkrDto;
    }

    @Override
    public Object getRainLevelList() {
       //查询雨情等级
        List<YwkRainLevel> allByLevel = ywkRainLevelDao.findAllByLevel();
        return allByLevel;
    }

    @Override
    @Transactional
    public Object updateRainLevelList(List<YwkRainLevel> rainLevelList) {
        if(rainLevelList!=null && rainLevelList.size()>0){
            return ywkRainLevelDao.saveAll(rainLevelList);
        }
        return rainLevelList;
    }
}
