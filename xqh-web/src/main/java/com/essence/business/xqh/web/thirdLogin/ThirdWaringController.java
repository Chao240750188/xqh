package com.essence.business.xqh.web.thirdLogin;

import com.essence.business.xqh.api.Third.ThirdWaringService;
import com.essence.business.xqh.api.rainfall.vo.QueryParamDto;
import com.essence.business.xqh.common.returnFormat.SystemSecurityMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/warning")
public class ThirdWaringController {


    @Autowired
    ThirdWaringService thirdWaringService;
    /**
     * 降雨预警信息接口
     *
     * @param
     * @return
     */
    @GetMapping(value = "/rainWarning")
    public SystemSecurityMessage getRainWarning() {
        try {
            return new SystemSecurityMessage("ok", "查询降雨预警信息接口成功", thirdWaringService.getRainWarning());
        } catch (Exception e) {
            e.printStackTrace();
            return new SystemSecurityMessage("error", "查询降雨预警信息接口失败");
        }
    }

    /**
     *河道洪水告警信息接口
     * @return
     */
    @GetMapping(value = "/waterWarning")
    public SystemSecurityMessage getWaterWarning() {
        try {
            return new SystemSecurityMessage("ok", "查询河道洪水告警信息接口成功", thirdWaringService.getWaterWarning());
        } catch (Exception e) {
            e.printStackTrace();
            return new SystemSecurityMessage("error", "查询河道洪水告警信息接口失败");
        }
    }


}
