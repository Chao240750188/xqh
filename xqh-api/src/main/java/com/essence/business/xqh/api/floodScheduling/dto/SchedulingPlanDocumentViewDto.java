package com.essence.business.xqh.api.floodScheduling.dto;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;

/**
 * 调度方案管理展示实体类
 * LiuGt add at 2020-04-01
 */
public class SchedulingPlanDocumentViewDto {

    /**
     * 主键ID
     */
    private String id;

    /**
     * 水库ID
     */
    private String resCode;

    /**
     * 水库名称
     */
    private String resName;

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
     * 调度方案文档名称
     */
    private String attachfileName;

    /**
     * 调度方案文档后缀
     */
    private String attachfileSuff;

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

    public String getResName() {
        return resName;
    }

    public void setResName(String resName) {
        this.resName = resName;
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

    public String getAttachfileName() {
        return attachfileName;
    }

    public void setAttachfileName(String attachfileName) {
        this.attachfileName = attachfileName;
    }

    public String getAttachfileSuff() {
        return attachfileSuff;
    }

    public void setAttachfileSuff(String attachfileSuff) {
        this.attachfileSuff = attachfileSuff;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }
}
