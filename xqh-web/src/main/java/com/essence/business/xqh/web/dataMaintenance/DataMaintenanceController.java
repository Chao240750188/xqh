package com.essence.business.xqh.web.dataMaintenance;

import com.essence.business.xqh.api.dataMaintenance.DataMaintenanceService;
import com.essence.business.xqh.api.dataMaintenance.dto.StSkkrDto;
import com.essence.business.xqh.api.dataMaintenance.dto.WrpRsrBsinTzDto;
import com.essence.business.xqh.common.returnFormat.SystemSecurityMessage;
import com.essence.business.xqh.dao.dao.fhybdd.StStbprpBDao;
import com.essence.business.xqh.dao.entity.fhybdd.StStbprpB;
import com.essence.business.xqh.dao.entity.fhybdd.StZvarlB;
import com.essence.business.xqh.dao.entity.fhybdd.WrpRsrBsin;
import com.essence.business.xqh.dao.entity.fhybdd.YwkRainLevel;
import com.essence.framework.jpa.Paginator;
import com.essence.framework.jpa.PaginatorParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 数据维护-系统控制层
 */
@RestController
@RequestMapping("/dataMaintenance")
public class DataMaintenanceController {

    @Autowired
    DataMaintenanceService dataMaintenanceService;

    /**
     * 分页获取雨量站 或 水位站监测站列表
     *
     * @return
     */
    @RequestMapping(value = "/getStbprpbList", method = RequestMethod.POST)
    public SystemSecurityMessage getPlanList(@RequestBody PaginatorParam paginatorParam) {
        try {
            Paginator planList = dataMaintenanceService.getStbprpbList(paginatorParam);
            return SystemSecurityMessage.getSuccessMsg("获取测站列表成功！", planList);
        } catch (Exception e) {
            e.printStackTrace();
            return SystemSecurityMessage.getFailMsg("获取测站列表失败！");
        }
    }

    /**
     * 新增或修改测站信息
     *
     * @return
     */
    @RequestMapping(value = "/saveOrUpdataStbprpb", method = RequestMethod.POST)
    public SystemSecurityMessage saveOrUpdataStbprpb(@RequestBody StStbprpB stStbprpB) {
        try {
            StStbprpB stStbprpBs = dataMaintenanceService.saveOrUpdataStbprpb(stStbprpB);
            return SystemSecurityMessage.getSuccessMsg("编辑测站成功！", stStbprpBs);
        } catch (Exception e) {
            e.printStackTrace();
            return SystemSecurityMessage.getFailMsg("编辑测站失败！");
        }
    }

    /**
     * 删除测站对象信息
     *
     * @return
     */
    @RequestMapping(value = "/deleteStbprpb/{stcd}", method = RequestMethod.GET)
    public SystemSecurityMessage deleteStbprpb(@PathVariable String stcd) {
        try {
            dataMaintenanceService.deleteStbprpb(stcd);
            return SystemSecurityMessage.getSuccessMsg("删除测站对象信息成功！", stcd);
        } catch (Exception e) {
            e.printStackTrace();
            return SystemSecurityMessage.getFailMsg("删除测站对象信息失败！");
        }
    }

    /**
     * 分页获取水库列表
     *
     * @return
     */
    @RequestMapping(value = "/getWrpRsrBsinList", method = RequestMethod.POST)
    public SystemSecurityMessage getWrpRsrBsinList(@RequestBody PaginatorParam paginatorParam) {
        try {
            Paginator planList = dataMaintenanceService.getWrpRsrBsinList(paginatorParam);
            return SystemSecurityMessage.getSuccessMsg("获取水库列表成功！", planList);
        } catch (Exception e) {
            e.printStackTrace();
            return SystemSecurityMessage.getFailMsg("获取水库列表失败！");
        }
    }

    /**
     * 新增或修改水库信息
     *
     * @return
     */
    @RequestMapping(value = "/saveOrWrpRsrBsin", method = RequestMethod.POST)
    public SystemSecurityMessage saveOrWrpRsrBsin(@RequestBody WrpRsrBsin wrpRsrBsin) {
        try {
            WrpRsrBsin wrpRsrBsins = dataMaintenanceService.saveOrWrpRsrBsin(wrpRsrBsin);
            return SystemSecurityMessage.getSuccessMsg("编辑水库成功！", wrpRsrBsins);
        } catch (Exception e) {
            e.printStackTrace();
            return SystemSecurityMessage.getFailMsg("编辑水库失败！");
        }
    }

    /**
     * 删除水库对象信息
     *
     * @return
     */
    @RequestMapping(value = "/deleteWrpRsrBsin/{stcd}", method = RequestMethod.GET)
    public SystemSecurityMessage deleteWrpRsrBsin(@PathVariable String stcd) {
        try {
            dataMaintenanceService.deleteWrpRsrBsin(stcd);
            return SystemSecurityMessage.getSuccessMsg("删除水库对象信息成功！", stcd);
        } catch (Exception e) {
            e.printStackTrace();
            return SystemSecurityMessage.getFailMsg("删除水库对象信息失败！");
        }
    }

    /**
     * 根据水库编码查询水库特征值
     *
     * @return
     */
    @RequestMapping(value = "/getWrpRsrBsinTz/{stcd}", method = RequestMethod.GET)
    public SystemSecurityMessage getWrpRsrBsinTz(@PathVariable String stcd) {
        try {
            WrpRsrBsinTzDto wrpRsrBsinTz = dataMaintenanceService.getWrpRsrBsinTz(stcd);
            return SystemSecurityMessage.getSuccessMsg("查询水库特征值成功！", wrpRsrBsinTz);
        } catch (Exception e) {
            e.printStackTrace();
            return SystemSecurityMessage.getFailMsg("查询水库特征值失败！",null);
        }
    }

    /**
     * 编辑水库特征值
     *
     * @return
     */
    @RequestMapping(value = "/saveWrpRsrBsinTz", method = RequestMethod.POST)
    public SystemSecurityMessage saveWrpRsrBsinTz(@RequestBody WrpRsrBsinTzDto wrpRsrBsinTzDto) {
        try {
            WrpRsrBsinTzDto wrpRsrBsinTz = dataMaintenanceService.saveWrpRsrBsinTz(wrpRsrBsinTzDto);
            return SystemSecurityMessage.getSuccessMsg("编辑水库特征值成功！", wrpRsrBsinTz);
        } catch (Exception e) {
            e.printStackTrace();
            return SystemSecurityMessage.getFailMsg("编辑水库特征值失败！",null);
        }
    }

    /**
     * 获取调度目标维护-水库库容曲线
     *
     * @return
     */
    @RequestMapping(value = "/getWrpRsrKr", method = RequestMethod.GET)
    public SystemSecurityMessage getWrpRsrKr() {
        try {
            return SystemSecurityMessage.getSuccessMsg("获取库容曲线成功！", dataMaintenanceService.getWrpRsrKr());
        } catch (Exception e) {
            e.printStackTrace();
            return SystemSecurityMessage.getFailMsg("获取库容曲线失败！",null);
        }
    }

    /**
     * 批量修改调度目标维护-水库库容曲线
     *
     * @return
     */
    @RequestMapping(value = "/updateWrpRsrKrList", method = RequestMethod.POST)
    public SystemSecurityMessage updateWrpRsrKrList (@RequestBody StSkkrDto stSkkrDto) {
        try {
            return SystemSecurityMessage.getSuccessMsg("批量修改调度目标维护-水库库容曲线成功！", dataMaintenanceService.updateWrpRsrKrList(stSkkrDto));
        } catch (Exception e) {
            e.printStackTrace();
            return SystemSecurityMessage.getFailMsg("批量修改调度目标维护-水库库容曲线失败！",null);
        }
    }


    /**
     * 其它信息-雨情量级查询
     *
     * @return
     */
    @RequestMapping(value = "/getRainLevelList", method = RequestMethod.GET)
    public SystemSecurityMessage getRainLevelList () {
        try {
            return SystemSecurityMessage.getSuccessMsg("查询其它信息-雨情量级查询成功！", dataMaintenanceService.getRainLevelList());
        } catch (Exception e) {
            e.printStackTrace();

            return SystemSecurityMessage.getFailMsg("查询其它信息-雨情量级查询失败！",null);
        }
    }

    /**
     * 批量修改其它信息-雨情量级
     *
     * @return
     */
    @RequestMapping(value = "/updateRainLevelList", method = RequestMethod.POST)
    public SystemSecurityMessage updateRainLevelList (@RequestBody List<YwkRainLevel> rainLevelList) {
        try {
            return SystemSecurityMessage.getSuccessMsg("批量修改其它信息-雨情量级成功！", dataMaintenanceService.updateRainLevelList(rainLevelList));
        } catch (Exception e) {
            e.printStackTrace();
            return SystemSecurityMessage.getFailMsg("批量修改其它信息-雨情量级失败！",null);
        }
    }
}

