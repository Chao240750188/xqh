package com.essence.business.xqh.api.floodScheduling.service;

import com.essence.business.xqh.api.floodScheduling.dto.SchedulingPlanDocPageListParamDto;
import com.essence.business.xqh.api.floodScheduling.dto.SchedulingPlanDocumentViewDto;
import com.essence.business.xqh.api.floodScheduling.dto.SkddSchedulingPlanDocumentDto;
import com.essence.framework.jpa.Paginator;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * 调度方案文档信息表服务接口
 * @company Essence
 * @author LiuGt
 * @version 1.0 2020/03/30
 */
public interface SchedulingPlanDocumentService {

    /**
     * 分页查询调度方案文档列表
     * @param param
     * @return
     */
    Paginator<SkddSchedulingPlanDocumentDto> getSchedulingPlanDocListPage(SchedulingPlanDocPageListParamDto param);

    /**
     * 添加一个调度方案信息
     * @param schedulingPlanDocument
     * @return
     */
    SkddSchedulingPlanDocumentDto addSchedulingPlan(SkddSchedulingPlanDocumentDto schedulingPlanDocument);

    /**
     * 根据ID查询一个调度方案文档信息
     * @param id
     * @return
     */
    SchedulingPlanDocumentViewDto querySchedulingPlanById(String id);

    /**
     * 编辑一个调度方案信息
     * @param schedulingPlanDocument
     * @return
     */
    SkddSchedulingPlanDocumentDto editSchedulingPlan(SkddSchedulingPlanDocumentDto schedulingPlanDocument);

    /**
     * 删除一个调度方案信息
     * @param id
     */
    boolean deleteSchedulingPlanById(String id);

    /**
     * 上传调度方案文档文件
     */
    List<String> uploadSchedulingPlanFile(String id, MultipartFile files);

    /**
     * 根据ID删除一个调度方案的附件文件
     * @param id
     * @return
     */
    boolean deleteSchedulingPlanDocById(String id);

    /**
     * 预览调度方案附件pdf
     * @param request
     * @param response
     * @param id
     */
    void previewSchedulingPlanDoc(HttpServletRequest request, HttpServletResponse response, String id) throws IOException;

    /**
     * 预览调度方案附件pdf
     * @param request
     * @param response
     * @param resCode
     */
    void previewSchedulingPlanDocByResCode(HttpServletRequest request, HttpServletResponse response, String resCode) throws IOException;
}
