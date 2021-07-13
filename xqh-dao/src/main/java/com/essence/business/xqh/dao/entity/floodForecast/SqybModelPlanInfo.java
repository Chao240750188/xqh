package com.essence.business.xqh.dao.entity.floodForecast;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@Entity
@Table(name = "SQYB_MODEL_PLANINFO")
public class SqybModelPlanInfo implements Serializable {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id
    @Column(name = "PLAN_ID")
    private String planId; //方案id
    
    @Column(name = "PLAN_NAME")
    private String planName;//方案名称
    
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @Column(name = "START_TIME")
    private Date startTime;//计算开始时间
    
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @Column(name = "END_TIME")
    private Date endTime; //计算结束时间
    
    @Column(name = "RES_CODE")
    private String resCode; //关联水库编码
    
    @Column(name = "MODEL_ID")
    private String modelId; //模型id

    @Column(name = "AUTORUN_SIGN")  //滚动计算标志（0=手动计算，1=滚动计算）
    private Integer autoRunSign;

    @Column(name = "PLAN_STATUS")
    private  Integer planStatus; //方案计算状态（0=未开始计算，1=计算中，2=计算成功，-1=计算失败）
    
    @Column(name = "PLAN_STATUS_INFO")
    private  String planStatusInfo; //方案计算状态信息
    
    @Column(name = "NT")
    private String nt; //备注
    
    @Column(name = "PLAN_TYPE")
    private String planType; //方案类型（0实时方案，1预测方案）
    
    @Column(name = "DATA_TYPE")
    private String dataType; //数据获取类型（0自动导入，1人工导入）
    
    @Column(name = "RAIN_TYPE")
    private String rainType; //雨型类型（峰值出现的时间点不同，1,2,3）
    
    @Column(name = "PLAN_FORESEE")
    private String planForesee; //预见期（3,6,12,24小时）

    @Column(name = "PLAN_FORESEE_TOTAL_RAIN")
    private BigDecimal planForeseeTotalRain; //预见期总降雨
    
    @Column(name = "INITIAL_DAMAGE")
    private Double initialDamage; //初损值
    
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @Column(name = "MODI_TIME")
    private Date modiTime;//时间戳
    
    @Transient
    private String modelName;//模型名称
    
    @Transient
    private String resName;//水库名称

    public String getPlanId() {
        return planId;
    }

    public void setPlanId(String planId) {
        this.planId = planId;
    }

    public String getPlanName() {
        return planName;
    }

    public void setPlanName(String planName) {
        this.planName = planName;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public String getResCode() {
        return resCode;
    }

    public void setResCode(String resCode) {
        this.resCode = resCode;
    }

    public String getModelId() {
        return modelId;
    }

    public void setModelId(String modelId) {
        this.modelId = modelId;
    }

    public Integer getAutoRunSign() {
        return autoRunSign;
    }

    public void setAutoRunSign(Integer autoRunSign) {
        this.autoRunSign = autoRunSign;
    }

    public Integer getPlanStatus() {
        return planStatus;
    }

    public void setPlanStatus(Integer planStatus) {
        this.planStatus = planStatus;
    }

    public String getPlanStatusInfo() {
        return planStatusInfo;
    }

    public void setPlanStatusInfo(String planStatusInfo) {
        this.planStatusInfo = planStatusInfo;
    }

    public String getNt() {
        return nt;
    }

    public void setNt(String nt) {
        this.nt = nt;
    }

    public String getPlanType() {
        return planType;
    }

    public void setPlanType(String planType) {
        this.planType = planType;
    }

    public String getDataType() {
        return dataType;
    }

    public void setDataType(String dataType) {
        this.dataType = dataType;
    }

    public String getRainType() {
        return rainType;
    }

    public void setRainType(String rainType) {
        this.rainType = rainType;
    }

    public String getPlanForesee() {
        return planForesee;
    }

    public void setPlanForesee(String planForesee) {
        this.planForesee = planForesee;
    }

    public BigDecimal getPlanForeseeTotalRain() {
        return planForeseeTotalRain;
    }

    public void setPlanForeseeTotalRain(BigDecimal planForeseeTotalRain) {
        this.planForeseeTotalRain = planForeseeTotalRain;
    }

    public Double getInitialDamage() {
        return initialDamage;
    }

    public void setInitialDamage(Double initialDamage) {
        this.initialDamage = initialDamage;
    }

    public Date getModiTime() {
        return modiTime;
    }

    public void setModiTime(Date modiTime) {
        this.modiTime = modiTime;
    }

    public String getModelName() {
        return modelName;
    }

    public void setModelName(String modelName) {
        this.modelName = modelName;
    }

    public String getResName() {
        return resName;
    }

    public void setResName(String resName) {
        this.resName = resName;
    }
}
