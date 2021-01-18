package com.essence.business.xqh.api.floodScheduling.dto;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 调度方案文档信息表实体类
 * @company Essence
 * @author LiuGt
 * @version 1.0 2020/03/30
 */
public class SkddSchedulingPlanDocumentDto implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    private String id;

    /**
     * 水库ID
     */
    private String resCode;

    /**
     * 调度方案名称
     */
    private String planName;

    /**
     * 预案日期
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern="yyyy-MM-dd HH:mm:ss")
    private LocalDateTime preplanDate;

    /**
     * 编制单位
     */
    private String organizationUnit;

    /**
     * 所在区县
     */
    private String districts;

    /**
     * 调度方案文档路径
     */
    private String attachfilePath;

    /**
     * 创建时间
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern="yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getResCode() {
        return resCode;
    }

    public void setResCode(String resCode) {
        this.resCode = resCode;
    }

    public String getPlanName() {
        return planName;
    }

    public void setPlanName(String planName) {
        this.planName = planName;
    }

    public LocalDateTime getPreplanDate() {
        return preplanDate;
    }

    public void setPreplanDate(LocalDateTime preplanDate) {
        this.preplanDate = preplanDate;
    }

    public String getOrganizationUnit() {
        return organizationUnit;
    }

    public void setOrganizationUnit(String organizationUnit) {
        this.organizationUnit = organizationUnit;
    }

    public String getDistricts() {
        return districts;
    }

    public void setDistricts(String districts) {
        this.districts = districts;
    }

    public String getAttachfilePath() {
        return attachfilePath;
    }

    public void setAttachfilePath(String attachfilePath) {
        this.attachfilePath = attachfilePath;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }
}
