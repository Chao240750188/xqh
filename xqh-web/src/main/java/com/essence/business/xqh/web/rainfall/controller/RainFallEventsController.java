package com.essence.business.xqh.web.rainfall.controller;

import com.essence.business.xqh.api.rainfall.dto.YTRainTimeDto;
import com.essence.business.xqh.api.rainfall.service.RainFallEventsService;
import com.essence.business.xqh.common.returnFormat.SystemSecurityMessage;
import com.essence.business.xqh.dao.entity.rainfall.YwkTypicalRainTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/rainFallEvents")
public class RainFallEventsController {

    @Autowired
    RainFallEventsService rainFallEventsService;

    /**
     * 根据场次名称查询降雨场次是否已存在
     * @param cRainName
     * @return
     */
    @GetMapping(value = "/searchRainFallEventsIsExits/{cRainName}")
    public SystemSecurityMessage searchRainFallEventsIsExits(@PathVariable String cRainName) {
        try {
            Boolean isExits = rainFallEventsService.searchRainFallEventsIsExits(cRainName);

            if (isExits){
                return SystemSecurityMessage.getFailMsg("降雨场次名称重复！",0); //存在返回0
            }

            return SystemSecurityMessage.getSuccessMsg("降雨场次名称可使用", 1); //不存在返回1
        }catch (Exception e){
            e.printStackTrace();
            return SystemSecurityMessage.getFailMsg("降雨场次名称失败！");
        }
    }

    /**
     * 增加降雨场次
     * @param ywkTypicalRainTime
     * @return
     */
    @PostMapping(value = "/addRainFallEvents")
    public SystemSecurityMessage addRainFallEvents(@RequestBody YwkTypicalRainTime ywkTypicalRainTime) {
        try {
            String results = rainFallEventsService.addRainFallEvents(ywkTypicalRainTime);

            if ("RainNameIsExist".equals(results)){
                return SystemSecurityMessage.getFailMsg("降雨场次名称已存在，请重新输入！");
            }

            if(results == null){
                return SystemSecurityMessage.getSuccessMsg("增加降雨场次失败！");
            }
            return SystemSecurityMessage.getSuccessMsg("增加降雨场次成功！", results);
        }catch (Exception e){
            e.printStackTrace();
            return SystemSecurityMessage.getFailMsg("增加降雨场次失败！");
        }
    }

    /**
     * 查询降雨场次
     * @param cRainName
     * @return
     */
    @GetMapping(value = "/getRainFallEventsByName/{cRainName}")
    public SystemSecurityMessage getRainFallEventsByName(@PathVariable String cRainName) {
        try {
            List<YwkTypicalRainTime> allYwkTypicalRainTime = rainFallEventsService.getRainFallEventsByName(cRainName);
            return SystemSecurityMessage.getSuccessMsg("查询降雨场次成功！", allYwkTypicalRainTime);
        }catch (Exception e){
            e.printStackTrace();
            return SystemSecurityMessage.getFailMsg("查询降雨场次失败！");
        }
    }

    /**
     * 查询所有降雨场次
     * @return
     */
    @GetMapping(value = "/getAllRainFallEvents")
    public SystemSecurityMessage getAllRainFallEvents() {
        try {
            List<YTRainTimeDto> all = rainFallEventsService.getAllRainFallEvents();
            return SystemSecurityMessage.getSuccessMsg("查询降雨场次成功！", all);
        }catch (Exception e){
            e.printStackTrace();
            return SystemSecurityMessage.getFailMsg("查询降雨场次失败！");
        }

    }

    /**
     * 删除降雨场次
     * @param cId
     * @return
     */
    @GetMapping(value = "/deleteRainFallEvents/{cId}")
    public SystemSecurityMessage deleteRainFallEvents(@PathVariable String cId) {
        try {
            rainFallEventsService.deleteRainFallEvents(cId);
            return SystemSecurityMessage.getSuccessMsg("删除降雨场次成功！");
        }catch (Exception e){
            e.printStackTrace();
            return SystemSecurityMessage.getFailMsg("删除降雨场次失败！");
        }
    }

    /**
     * 修改降雨场次信息
     * @param ywkTypicalRainTime
     * @return
     */
    @PostMapping(value = "/updateRainFallEvents/{cId}")
    public SystemSecurityMessage updateRainFallEvents(@PathVariable String cId, @RequestBody YwkTypicalRainTime ywkTypicalRainTime) {
        try {
            rainFallEventsService.updateRainFallEvents(cId, ywkTypicalRainTime);
            return SystemSecurityMessage.getSuccessMsg("修改降雨场次成功！");
        }catch (Exception e){
            e.printStackTrace();
            return SystemSecurityMessage.getFailMsg("修改降雨场次失败！");
        }
    }

}
