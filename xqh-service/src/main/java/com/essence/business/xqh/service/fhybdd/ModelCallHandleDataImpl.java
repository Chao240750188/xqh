package com.essence.business.xqh.service.fhybdd;

import com.essence.business.xqh.api.fhybdd.service.ModelCallHandleDataService;
import com.essence.business.xqh.common.util.FileUtil;
import com.essence.business.xqh.common.util.PropertiesUtil;
import com.essence.business.xqh.dao.entity.fhybdd.YwkPlanOutputQ;
import com.essence.business.xqh.dao.entity.fhybdd.YwkPlaninRainfall;
import com.essence.business.xqh.dao.entity.fhybdd.YwkPlaninfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.io.File;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
public class ModelCallHandleDataImpl implements ModelCallHandleDataService {

    @Autowired
    EntityManager entityManager;

    @Transactional(propagation = Propagation.REQUIRED)
    @Async
    @Override
    public CompletableFuture<Integer> saveRainToDb(List<YwkPlaninRainfall> result) {
        String sql = "INSERT ALL  ";
        String insertSql="";

        for (YwkPlaninRainfall ywkPlaninRainfall : result){
            String id = ywkPlaninRainfall.getcId();
            String stcd = ywkPlaninRainfall.getcStcd();
            Double aDouble = ywkPlaninRainfall.getnDrp();
            //Date date = ywkPlaninRainfall.getdTime();
            String planid = ywkPlaninRainfall.getnPlanid();
            insertSql = insertSql+"INTO YWK_PLANIN_RAINFALL VALUES"+"( '"+id+"','"+stcd+"',"+aDouble+",? ,'"+planid+"') ";

        }
        //insertSql = insertSql.substring(0,insertSql.length()-1);
        insertSql = insertSql +" SELECT 1 FROM DUAL ";
        Query nativeQuery = entityManager.createNativeQuery(sql + insertSql);//.executeUpdate();
        int z = 1;
        for (YwkPlaninRainfall ywkPlaninRainfall : result){
            Date date = ywkPlaninRainfall.getdTime();
            nativeQuery.setParameter(z,date);
            z++;
        }
        nativeQuery.executeUpdate();
       return CompletableFuture.completedFuture(1);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    @Async
    @Override
    public CompletableFuture<Integer> savePlanOut(List<YwkPlanOutputQ> result) {
        String sql = "INSERT ALL  ";
        String insertSql="";

        for (YwkPlanOutputQ ywkPlanOutputQ : result){
            String idcid = ywkPlanOutputQ.getIdcId();
            String planId = ywkPlanOutputQ.getnPlanid();
            //Date date = ywkPlanOutputQ.getdTime();
            Double q = ywkPlanOutputQ.getnQ();
            String rvcrcrsccd = ywkPlanOutputQ.getRvcrcrsccd();
            insertSql = insertSql+"INTO YWK_PLAN_OUTPUT_Q VALUES"+"( '"+idcid+"','"+planId+"',?,"+q+" ,'"+rvcrcrsccd+"') ";

        }
        //insertSql = insertSql.substring(0,insertSql.length()-1);
        insertSql = insertSql +" SELECT 1 FROM DUAL ";
        Query nativeQuery = entityManager.createNativeQuery(sql + insertSql);//.executeUpdate();
        int z = 1;
        for (YwkPlanOutputQ outputQ : result){
            Date date = outputQ.getdTime();
            nativeQuery.setParameter(z,date);
            z++;
        }
        nativeQuery.executeUpdate();
        return CompletableFuture.completedFuture(1);
    }


    @Async
    @Override
    public void handleCsvAndResult(Integer tag ,YwkPlaninfo planInfo) {




    }
}
