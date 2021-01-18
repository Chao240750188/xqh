package com.essence.business.xqh.api.floodForecast.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;


/**
 * 水库名称
 * @author NoBugNoCode
 *
 * 2019年10月24日 下午5:52:16
 */
public class SqybResDto implements Serializable {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

    private String resCode; //水库编码


	private String htStcd; //关联慧图对应水库测站的ID


    private String  resName; //水库名称


    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date  fromDate; //录入时间
    

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date  toDate; //修改时间


	private BigDecimal ddz; //死水位


	private BigDecimal normz; //正常蓄水位


	private BigDecimal ckflz; //校核洪水位

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

	public Date getFromDate() {
		return fromDate;
	}

	public void setFromDate(Date fromDate) {
		this.fromDate = fromDate;
	}

	public Date getToDate() {
		return toDate;
	}

	public void setToDate(Date toDate) {
		this.toDate = toDate;
	}

	public String getHtStcd() {
		return htStcd;
	}

	public BigDecimal getDdz() {
		return ddz;
	}

	public void setDdz(BigDecimal ddz) {
		this.ddz = ddz;
	}

	public BigDecimal getNormz() {
		return normz;
	}

	public void setNormz(BigDecimal normz) {
		this.normz = normz;
	}

	public BigDecimal getCkflz() {
		return ckflz;
	}

	public void setCkflz(BigDecimal ckflz) {
		this.ckflz = ckflz;
	}

	public void setHtStcd(String htStcd) {
		this.htStcd = htStcd;
	}

	@Override
	public String toString() {
		return "Res [resCode=" + resCode + ", resName=" + resName + ", fromDate=" + fromDate + ", toDate=" + toDate
				+ "]";
	}
    
}
