package com.essence.business.xqh.dao.dao.fhybdd;

import com.essence.business.xqh.dao.entity.fhybdd.StStbprpB;
import com.essence.business.xqh.dao.entity.fhybdd.StStsmtaskB;
import com.essence.business.xqh.dao.entity.rainfall.TStsmtaskBOld;
import com.essence.framework.jpa.EssenceJpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StStsmtaskBDao extends EssenceJpaRepository<StStsmtaskB,String > {
    List<StStsmtaskB> findByPfl(String pfl);

}
