package com.essence.business.xqh.dao.entity.floodScheduling;

import com.fasterxml.jackson.annotation.JsonFormat;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 调度方案文档信息表实体类
 * @company Essence
 * @author LiuGt
 * @version 1.0 2020/03/30
 */
@Entity
@Table(name = "SKDD_SCHEDULING_PLAN_DOCUMENT")
public class SkddSchedulingPlanDocument implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @Id
    @Column(name = "ID")
    private String id;

    /**
     * 水库ID
     */
    @Column(name = "RES_CODE")
    private String resCode;

    /**
     * 调度方案名称
     */
    @Column(name = "plan_name")
    private String planName;

    /**
     * 预案日期
     */
    @Column(name = "preplan_date")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern="yyyy-MM-dd HH:mm:ss")
    private LocalDateTime preplanDate;

    /**
     * 编制单位
     */
    @Column(name = "organization_unit")
    private String organizationUnit;

    /**
     * 所在区县
     */
    @Column(name = "districts")
    private String districts;

    /**
     * 调度方案文档路径
     */
    @Column(name = "attachfile_path")
    private String attachfilePath;

    /**
     * 创建时间
     */
    @Column(name = "create_time")
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
