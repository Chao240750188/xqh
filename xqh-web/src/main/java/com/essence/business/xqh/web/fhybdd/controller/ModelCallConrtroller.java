package com.essence.business.xqh.web.fhybdd.controller;

import com.essence.business.xqh.api.fhybdd.dto.ModelCallBySWDDVo;
import com.essence.business.xqh.api.fhybdd.service.ModelCallService;
import com.essence.business.xqh.common.returnFormat.SystemSecurityMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/modelCall")
public class ModelCallConrtroller {


    @Autowired
    ModelCallService modelCallService;
    /**
     * 水文调度模型
     * @return
     */
    @RequestMapping(value = "/modelCallBySWDD", method = RequestMethod.POST)
    public SystemSecurityMessage modelCallBySWDD(ModelCallBySWDDVo vo) {
        try {
            //modelCallService.callMode(vo
            return SystemSecurityMessage.getSuccessMsg("调用防洪与报警水文调度模型成功！");
        }catch (Exception e){
            e.printStackTrace();
            return SystemSecurityMessage.getFailMsg("调用防洪与报警水文调度模型失败！");

        }
    }

}
