package com.essence.business.xqh.web.floodScheduling;

import com.essence.business.xqh.api.floodScheduling.service.ObjResService;
import com.essence.business.xqh.common.returnFormat.SystemSecurityMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 水库信息表Controller
 * @company Essence
 * @author LiuGt
 * @version 1.0 2020/03/30
 */
@RestController
@RequestMapping("/objres")
public class ObjResController {

    @Autowired
    private ObjResService objResService;

    /**
     * 根据水库ID查询一个水库信息
     * {resCode} 水库ID
     *
     * @return
     */
    @RequestMapping(value = "/getObjRes/{resCode}", method = RequestMethod.GET)
    public @ResponseBody SystemSecurityMessage getObjRes(@PathVariable String resCode) {
        try {
            return SystemSecurityMessage.getSuccessMsg("查询水库数据成功！", objResService.queryByResCode(resCode));
        }catch (Exception e){
            e.printStackTrace();
            return SystemSecurityMessage.getFailMsg("查询水库数据失败！");

        }

    }

    /**
     * 查询可展示的水库列表
     * @return
     */
    @RequestMapping(value = "/queryShowObjRes", method = RequestMethod.POST)
    public @ResponseBody SystemSecurityMessage queryShowObjRes(){
        try {
            return new SystemSecurityMessage("ok", "查询成功！", objResService.queryByShow());
        }catch (Exception e){
            e.printStackTrace();
            return SystemSecurityMessage.getFailMsg("查询失败！");
        }

    }
}
