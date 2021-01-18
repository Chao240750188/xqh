package com.essence.business.xqh.service.floodScheduling;

import com.essence.business.xqh.api.floodScheduling.dto.SaveSchedulingPlanParamDto;
import com.essence.business.xqh.api.floodScheduling.dto.SchedulingPlanPageListParamDto;
import com.essence.business.xqh.api.floodScheduling.dto.SkddSchedulingPlanInfoDto;
import com.essence.business.xqh.api.floodScheduling.service.SchedulingPlanInfoService;
import com.essence.business.xqh.dao.dao.floodScheduling.SkddObjResDao;
import com.essence.business.xqh.dao.dao.floodScheduling.SkddSchedulingOutRulesDao;
import com.essence.business.xqh.dao.dao.floodScheduling.SkddSchedulingPlanInfoDao;
import com.essence.business.xqh.dao.entity.floodScheduling.SkddObjRes;
import com.essence.business.xqh.dao.entity.floodScheduling.SkddSchedulingOutRules;
import com.essence.business.xqh.dao.entity.floodScheduling.SkddSchedulingPlanInfo;
import com.essence.euauth.common.SysConstant;
import com.essence.framework.jpa.Criterion;
import com.essence.framework.jpa.Paginator;
import com.essence.framework.jpa.PaginatorParam;
import com.essence.framework.util.StrUtil;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpSession;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 调度方案信息表服务实现
 * @company Essence
 * @author LiuGt
 * @version 1.0 2020/03/31
 */
@Transactional
@Service
public class SchedulingPlanInfoServiceImpl implements SchedulingPlanInfoService {

    @Autowired
    SkddSchedulingPlanInfoDao schedulingPlanInfoDao;
    @Autowired
    SkddObjResDao objResDao; //水库数据访问
    @Autowired
    SkddSchedulingOutRulesDao schedulingOutRulesDao; //调度规则数据访问

    /**
     * 分页查询调度方案列表
     * @param param
     * @return
     */
    @Override
    public Paginator<SkddSchedulingPlanInfoDto> getSchedulingPlanListPage(SchedulingPlanPageListParamDto param){
        Paginator<SkddSchedulingPlanInfoDto> paginator = new Paginator<>(param.getCurrentPage(), param.getPageSize());
        List<SkddSchedulingPlanInfoDto> skddSchedulingPlanInfoDtoList = new ArrayList<>();

        //排序
        List<Criterion> orders = new ArrayList<>();
        Criterion criterion = new Criterion();
        criterion.setFieldName("modiTime");
        criterion.setOperator(Criterion.DESC);
        orders.add(criterion);
        //分页参数实体实例
        PaginatorParam paginatorParam = new PaginatorParam();
        paginatorParam.setCurrentPage(param.getCurrentPage());
        paginatorParam.setPageSize(param.getPageSize());
        paginatorParam.setOrders(orders);

        //region 查询条件

        //水库ID
        List<Criterion> conditions = new ArrayList<>();
        if (param.getResCode() != null && !param.getResCode().equals("")){
            Criterion criterion1 = new Criterion();
            criterion1.setFieldName("resCode");
            criterion1.setOperator(Criterion.EQ);
            criterion1.setValue(param.getResCode());
            conditions.add(criterion1);
        }
        //历史状态
        Criterion criterionHistory = new Criterion();
        criterionHistory.setFieldName("historyStatus");
        criterionHistory.setOperator(Criterion.EQ);
        criterionHistory.setValue(param.getHistoryStatus());
        conditions.add(criterionHistory);
        //开始时间和结束时间条件
        if (param.getStartTime() != null && !param.getStartTime().equals("") && param.getEndTime() != null && !param.getEndTime().equals("")){
            LocalDateTime startTime = LocalDateTime.parse(param.getStartTime(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            LocalDateTime endTime = LocalDateTime.parse(param.getEndTime(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            Criterion criterionStartTime = new Criterion();
            criterionStartTime.setFieldName("startTime");
            criterionStartTime.setOperator(Criterion.GTE);
            criterionStartTime.setValue(startTime);
            conditions.add(criterionStartTime);
            Criterion criterionEndTime = new Criterion();
            criterionEndTime.setFieldName("startTime");
            criterionEndTime.setOperator(Criterion.LTE);
            criterionEndTime.setValue(endTime);
            conditions.add(criterionEndTime);
        }
        if (conditions.size() > 0){
            paginatorParam.setConditions(conditions);
        }
        //endregion

        //查询并返回数据
        Paginator<SkddSchedulingPlanInfo> schedulingPlanInfoPaginator = schedulingPlanInfoDao.findAll(paginatorParam);
        BeanUtils.copyProperties(schedulingPlanInfoPaginator,paginator);
        List<SkddSchedulingPlanInfo> items = schedulingPlanInfoPaginator.getItems();
        if (items.size() > 0){
            List<SkddObjRes> objResList = objResDao.findAll();
            Map<String, String> collect = objResList.stream().collect(Collectors.toMap(SkddObjRes::getResCode, SkddObjRes::getResName));
            for (int i=0;i<items.size();i++){
                SkddSchedulingPlanInfo schedulingPlanInfo = items.get(i);
                SkddSchedulingPlanInfoDto skddSchedulingPlanInfoDto = new SkddSchedulingPlanInfoDto();
                BeanUtils.copyProperties(schedulingPlanInfo,skddSchedulingPlanInfoDto);
                String resName = collect.get(skddSchedulingPlanInfoDto.getResCode());
                skddSchedulingPlanInfoDto.setResName(resName);
                skddSchedulingPlanInfoDtoList.add(skddSchedulingPlanInfoDto);
            }
        }
        paginator.setItems(skddSchedulingPlanInfoDtoList);

        return paginator;
    }

    /**
     * 保存调度方案结果
     * @param planId
     * @return
     */
    @Override
    public int saveSchedulingResult(String planId){
        try{
            int i = schedulingPlanInfoDao.saveSchedulingResult(planId);
            return i;
        }
        catch (Exception e){
            e.printStackTrace();
            return -1;
        }
    }

    /**
     * 保存水库调度信息
     * @param saveSchedulingPlanParamDto
     * @return
     */
    @Override
    public SkddSchedulingPlanInfoDto saveSchedulingPlan(SaveSchedulingPlanParamDto saveSchedulingPlanParamDto){
        //水库调度ID
        String planId = StrUtil.getUUID();
        //调度规则
        List<SkddSchedulingOutRules> schedulingOutRulesList = new ArrayList<>();
        if (saveSchedulingPlanParamDto.getResZvarlList() != null && saveSchedulingPlanParamDto.getResZvarlList().size() > 0){
            saveSchedulingPlanParamDto.getResZvarlList().forEach(resZvarlViewDto -> {
                SkddSchedulingOutRules schedulingOutRules = new SkddSchedulingOutRules();
                schedulingOutRules.setId(StrUtil.getUUID());
                schedulingOutRules.setPlanId(planId);
                schedulingOutRules.setRz(resZvarlViewDto.getRz());
                schedulingOutRules.setPtNo(resZvarlViewDto.getPtNo());
                schedulingOutRules.setOtq(resZvarlViewDto.getOtq());
                schedulingOutRulesList.add(schedulingOutRules);
            });
        }
        //水库调度基本信息
        SkddSchedulingPlanInfo schedulingPlanInfo = new SkddSchedulingPlanInfo();
        schedulingPlanInfo.setPlanId(planId);
        schedulingPlanInfo.setResCode(saveSchedulingPlanParamDto.getResCode());
        schedulingPlanInfo.setWl(saveSchedulingPlanParamDto.getWl());
        String startTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:00"));
        schedulingPlanInfo.setStartTime(LocalDateTime.parse(startTime,DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:00")));
        schedulingPlanInfo.setPlanStatus(0);
        schedulingPlanInfo.setPlanStatusInfo("未开始计算");
        schedulingPlanInfo.setOutType(saveSchedulingPlanParamDto.getOutType());
        //获取当前登录用户
        HttpSession session =  ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest().getSession();
        String loginUserName = "";
        if (session != null && session.getAttribute(SysConstant.CURRENT_USERNAME) != null){
            loginUserName = session.getAttribute(SysConstant.CURRENT_USERNAME).toString();
        }
        schedulingPlanInfo.setCreateUser(loginUserName);
        schedulingPlanInfo.setHistoryStatus(0);
        schedulingPlanInfo.setModiTime(LocalDateTime.now());
        //保存数据
        try{
            SkddSchedulingPlanInfo schedulingPlanInfoDb = schedulingPlanInfoDao.save(schedulingPlanInfo);
            schedulingOutRulesDao.saveAll(schedulingOutRulesList);
            SkddSchedulingPlanInfoDto skddSchedulingPlanInfoDto = new SkddSchedulingPlanInfoDto();
            BeanUtils.copyProperties(schedulingPlanInfoDb,skddSchedulingPlanInfoDto);
            return skddSchedulingPlanInfoDto;
        }
        catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 根据水库ID查询正在计算中的调度方案个数
     * @param resCode
     * @return
     */
    @Override
    public int queryInProgresCountByResCode(String resCode){
        return schedulingPlanInfoDao.queryInProgresCountByResCode(resCode);
    }

    /**
     * 查询未开始或计算中的调度方案
     * @return
     */
    @Override
    public List<SkddSchedulingPlanInfoDto> queryNeedSchedulingPlan(){
        List<SkddSchedulingPlanInfoDto> skddSchedulingPlanInfoDtoList = new ArrayList<>();

        List<SkddSchedulingPlanInfo> schedulingPlanInfos = schedulingPlanInfoDao.queryNeedSchedulingPlan();
        if (schedulingPlanInfos.size()>0){
            for (int i=0;i<schedulingPlanInfos.size();i++){
                SkddSchedulingPlanInfo schedulingPlanInfo = schedulingPlanInfos.get(i);
                SkddSchedulingPlanInfoDto skddSchedulingPlanInfoDto = new SkddSchedulingPlanInfoDto();
                BeanUtils.copyProperties(schedulingPlanInfo,skddSchedulingPlanInfoDto);
                skddSchedulingPlanInfoDtoList.add(skddSchedulingPlanInfoDto);
            }
        }
        return skddSchedulingPlanInfoDtoList;
    }

    /**
     * 根据方案ID更新方案计算状态
     * @param planStatus 状态值
     * @param planId 方案ID
     * @return
     */
    @Override
    public int editPlanStatusByPlanId(Integer planStatus, String planId){
        return schedulingPlanInfoDao.editPlanStatusByPlanId(planStatus, planId);
    }

    /**
     * 根据方案ID查询一个调度方案信息
     * @param planId
     * @return
     */
    @Override
    public SkddSchedulingPlanInfoDto queryByPlanId(String planId){
        SkddSchedulingPlanInfoDto skddSchedulingPlanInfoDto = null;
        SkddSchedulingPlanInfo schedulingPlanInfo = schedulingPlanInfoDao.queryByPlanId(planId);
        if (null!=schedulingPlanInfo) {
            skddSchedulingPlanInfoDto = new SkddSchedulingPlanInfoDto();
            BeanUtils.copyProperties(schedulingPlanInfo,skddSchedulingPlanInfoDto);
        }
        return skddSchedulingPlanInfoDto;
    }
}
