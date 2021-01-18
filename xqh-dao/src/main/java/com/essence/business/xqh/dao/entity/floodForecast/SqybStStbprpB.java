package com.essence.business.xqh.dao.entity.floodForecast;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Date;

/**
 * 基本测站表实体类
 * @company Essence
 * @author lxf
 * @version 1.0 2019/10/25
 */
@Entity
@Table(name = "SQYB_ST_STBPRP_B",schema = "XQH")
public class SqybStStbprpB implements Serializable{
	private static final long serialVersionUID = 42;
	
	/***/
	@Id
	@Column(name = "STCD")
	private String stcd;
	
	/***/
	@Column(name = "STNM")
	private String stnm;
	
	/***/
	@Column(name = "RVNM")
	private String rvnm;
	
	/***/
	@Column(name = "HNNM")
	private String hnnm;
	
	/***/
	@Column(name = "bsnm")
	private String bsnm;
	
	/***/
	@Column(name = "STLC")
	private String stlc;
	
	/***/
	@Column(name = "ADDVCD")
	private String addvcd;
	
	/***/
	@Column(name = "MDBZ")
	private Double mdbz;
	
	/***/
	@Column(name = "MDPR")
	private Double mdpr;
	
	/***/
	@Column(name = "DTMNM")
	private String dtmnm;
	
	/***/
	@Column(name = "DTMEL")
	private Double dtmel;
	
	/***/
	@Column(name = "STTP")
	private String sttp;
	
	/***/
	@Column(name = "DFRTMS")
	private Integer dfrtms;
	
	/***/
	@Column(name = "FRITM")
	private String fritm;
	
	/***/
	@Column(name = "FRGRD")
	private String frgrd;
	
	/***/
	@Column(name = "EDFRYM")
	private String edfrym;
	
	/***/
	@Column(name = "BGFRYM")
	private String bgfrym;
	
	/***/
	@Column(name = "ADMAUTH")
	private String admauth;
	
	/***/
	@Column(name = "STBK")
	private String stbk;
	
	/***/
	@Column(name = "DRNA")
	private Double drna;
	
	/***/
	@Column(name = "PHCD")
	private String phcd;
	
	/***/
	@Column(name = "SUBNM")
	private String subnm;
	
	/***/
	@Column(name = "STCDT")
	private String stcdt;
	
	/***/
	@Column(name = "LGTD")
	private Double lgtd;
	
	/***/
	@Column(name = "LTTD")
	private Double lttd;
	
	/***/
	@Column(name = "DTPR")
	private Double dtpr;
	
	/***/
	@Column(name = "ESSTYM")
	private String esstym;
	
	/***/
	@Column(name = "ATCUNIT")
	private String atcunit;
	
	/**交换管理单位*/
	@Column(name = "LOCALITY")
	private String locality;
	
	/***/
	@Column(name = "STAZT")
	private Double stazt;
	
	/***/
	@Column(name = "DSTRVM")
	private Double dstrvm;
	
	/***/
	@Column(name = "USFL")
	private String usfl;
	
	/***/
	@Column(name = "COMMENTS")
	private String comments;
	
	/***/
	@Column(name = "MODITIME")
	 @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	private Date moditime;

	@Column(name = "RELEVANCESTCD")
	private String relevancestcd;

	public String getRelevancestcd() {
		return relevancestcd;
	}

	public void setRelevancestcd(String relevancestcd) {
		this.relevancestcd = relevancestcd;
	}

	/**
	 * 设置
	 * @param stcd String
	 */
	public void setStcd(String stcd) {
		this.stcd = stcd;
	}
	
	/**
	 * 获取
	 */
	public String getStcd() {
		return this.stcd;
	}
	/**
	 * 设置
	 * @param stnm String
	 */
	public void setStnm(String stnm) {
		this.stnm = stnm;
	}
	
	/**
	 * 获取
	 */
	public String getStnm() {
		return this.stnm;
	}
	/**
	 * 设置
	 * @param rvnm String
	 */
	public void setRvnm(String rvnm) {
		this.rvnm = rvnm;
	}
	
	/**
	 * 获取
	 */
	public String getRvnm() {
		return this.rvnm;
	}
	/**
	 * 设置
	 * @param hnnm String
	 */
	public void setHnnm(String hnnm) {
		this.hnnm = hnnm;
	}
	
	/**
	 * 获取
	 */
	public String getHnnm() {
		return this.hnnm;
	}
	/**
	 * 设置
	 * @param bsnm String
	 */
	public void setBsnm(String bsnm) {
		this.bsnm = bsnm;
	}
	
	/**
	 * 获取
	 */
	public String getBsnm() {
		return this.bsnm;
	}
	/**
	 * 设置
	 * @param stlc String
	 */
	public void setStlc(String stlc) {
		this.stlc = stlc;
	}
	
	/**
	 * 获取
	 */
	public String getStlc() {
		return this.stlc;
	}
	/**
	 * 设置
	 * @param addvcd String
	 */
	public void setAddvcd(String addvcd) {
		this.addvcd = addvcd;
	}
	
	/**
	 * 获取
	 */
	public String getAddvcd() {
		return this.addvcd;
	}
	/**
	 * 设置
	 * @param mdbz Double
	 */
	public void setMdbz(Double mdbz) {
		this.mdbz = mdbz;
	}
	
	/**
	 * 获取
	 */
	public Double getMdbz() {
		return this.mdbz;
	}
	/**
	 * 设置
	 * @param mdpr Double
	 */
	public void setMdpr(Double mdpr) {
		this.mdpr = mdpr;
	}
	
	/**
	 * 获取
	 */
	public Double getMdpr() {
		return this.mdpr;
	}
	/**
	 * 设置
	 * @param dtmnm String
	 */
	public void setDtmnm(String dtmnm) {
		this.dtmnm = dtmnm;
	}
	
	/**
	 * 获取
	 */
	public String getDtmnm() {
		return this.dtmnm;
	}
	/**
	 * 设置
	 * @param dtmel Double
	 */
	public void setDtmel(Double dtmel) {
		this.dtmel = dtmel;
	}
	
	/**
	 * 获取
	 */
	public Double getDtmel() {
		return this.dtmel;
	}
	/**
	 * 设置
	 * @param sttp String
	 */
	public void setSttp(String sttp) {
		this.sttp = sttp;
	}
	
	/**
	 * 获取
	 */
	public String getSttp() {
		return this.sttp;
	}
	/**
	 * 设置
	 * @param dfrtms int
	 */
	public void setDfrtms(Integer dfrtms) {
		this.dfrtms = dfrtms;
	}
	
	/**
	 * 获取
	 */
	public Integer getDfrtms() {
		return this.dfrtms;
	}
	/**
	 * 设置
	 * @param fritm String
	 */
	public void setFritm(String fritm) {
		this.fritm = fritm;
	}
	
	/**
	 * 获取
	 */
	public String getFritm() {
		return this.fritm;
	}
	/**
	 * 设置
	 * @param frgrd String
	 */
	public void setFrgrd(String frgrd) {
		this.frgrd = frgrd;
	}
	
	/**
	 * 获取
	 */
	public String getFrgrd() {
		return this.frgrd;
	}
	/**
	 * 设置
	 * @param edfrym String
	 */
	public void setEdfrym(String edfrym) {
		this.edfrym = edfrym;
	}
	
	/**
	 * 获取
	 */
	public String getEdfrym() {
		return this.edfrym;
	}
	/**
	 * 设置
	 * @param bgfrym String
	 */
	public void setBgfrym(String bgfrym) {
		this.bgfrym = bgfrym;
	}
	
	/**
	 * 获取
	 */
	public String getBgfrym() {
		return this.bgfrym;
	}
	/**
	 * 设置
	 * @param admauth String
	 */
	public void setAdmauth(String admauth) {
		this.admauth = admauth;
	}
	
	/**
	 * 获取
	 */
	public String getAdmauth() {
		return this.admauth;
	}
	/**
	 * 设置
	 * @param stbk String
	 */
	public void setStbk(String stbk) {
		this.stbk = stbk;
	}
	
	/**
	 * 获取
	 */
	public String getStbk() {
		return this.stbk;
	}
	/**
	 * 设置
	 * @param drna Double
	 */
	public void setDrna(Double drna) {
		this.drna = drna;
	}
	
	/**
	 * 获取
	 */
	public Double getDrna() {
		return this.drna;
	}
	/**
	 * 设置
	 * @param phcd String
	 */
	public void setPhcd(String phcd) {
		this.phcd = phcd;
	}
	
	/**
	 * 获取
	 */
	public String getPhcd() {
		return this.phcd;
	}
	/**
	 * 设置
	 * @param subnm String
	 */
	public void setSubnm(String subnm) {
		this.subnm = subnm;
	}
	
	/**
	 * 获取
	 */
	public String getSubnm() {
		return this.subnm;
	}
	/**
	 * 设置
	 * @param stcdt String
	 */
	public void setStcdt(String stcdt) {
		this.stcdt = stcdt;
	}
	
	/**
	 * 获取
	 */
	public String getStcdt() {
		return this.stcdt;
	}
	/**
	 * 设置
	 * @param lgtd Double
	 */
	public void setLgtd(Double lgtd) {
		this.lgtd = lgtd;
	}
	
	/**
	 * 获取
	 */
	public Double getLgtd() {
		return this.lgtd;
	}
	/**
	 * 设置
	 * @param lttd Double
	 */
	public void setLttd(Double lttd) {
		this.lttd = lttd;
	}
	
	/**
	 * 获取
	 */
	public Double getLttd() {
		return this.lttd;
	}
	/**
	 * 设置
	 * @param dtpr Double
	 */
	public void setDtpr(Double dtpr) {
		this.dtpr = dtpr;
	}
	
	/**
	 * 获取
	 */
	public Double getDtpr() {
		return this.dtpr;
	}
	/**
	 * 设置
	 * @param esstym String
	 */
	public void setEsstym(String esstym) {
		this.esstym = esstym;
	}
	
	/**
	 * 获取
	 */
	public String getEsstym() {
		return this.esstym;
	}
	/**
	 * 设置
	 * @param atcunit String
	 */
	public void setAtcunit(String atcunit) {
		this.atcunit = atcunit;
	}
	
	/**
	 * 获取
	 */
	public String getAtcunit() {
		return this.atcunit;
	}
	/**
	 * 设置交换管理单位
	 * @param locality String
	 */
	public void setLocality(String locality) {
		this.locality = locality;
	}
	
	/**
	 * 获取交换管理单位
	 */
	public String getLocality() {
		return this.locality;
	}
	/**
	 * 设置
	 * @param stazt Double
	 */
	public void setStazt(Double stazt) {
		this.stazt = stazt;
	}
	
	/**
	 * 获取
	 */
	public Double getStazt() {
		return this.stazt;
	}
	/**
	 * 设置
	 * @param dstrvm Double
	 */
	public void setDstrvm(Double dstrvm) {
		this.dstrvm = dstrvm;
	}
	
	/**
	 * 获取
	 */
	public Double getDstrvm() {
		return this.dstrvm;
	}
	/**
	 * 设置
	 * @param usfl String
	 */
	public void setUsfl(String usfl) {
		this.usfl = usfl;
	}
	
	/**
	 * 获取
	 */
	public String getUsfl() {
		return this.usfl;
	}
	/**
	 * 设置
	 * @param comments String
	 */
	public void setComments(String comments) {
		this.comments = comments;
	}
	
	/**
	 * 获取
	 */
	public String getComments() {
		return this.comments;
	}
	/**
	 * 设置
	 * @param moditime Date
	 */
	public void setModitime(Date moditime) {
		this.moditime = moditime;
	}
	
	/**
	 * 获取
	 */
	public Date getModitime() {
		return this.moditime;
	}

	/**
	* 重写toString方法
	* @return String
	*/
	public String toString() {
		return
		"stcd:"+getStcd()+","+
		"stnm:"+getStnm()+","+
		"rvnm:"+getRvnm()+","+
		"hnnm:"+getHnnm()+","+
		"bsnm:"+getBsnm()+","+
		"stlc:"+getStlc()+","+
		"addvcd:"+getAddvcd()+","+
		"mdbz:"+getMdbz()+","+
		"mdpr:"+getMdpr()+","+
		"dtmnm:"+getDtmnm()+","+
		"dtmel:"+getDtmel()+","+
		"sttp:"+getSttp()+","+
		"dfrtms:"+getDfrtms()+","+
		"fritm:"+getFritm()+","+
		"frgrd:"+getFrgrd()+","+
		"edfrym:"+getEdfrym()+","+
		"bgfrym:"+getBgfrym()+","+
		"admauth:"+getAdmauth()+","+
		"stbk:"+getStbk()+","+
		"drna:"+getDrna()+","+
		"phcd:"+getPhcd()+","+
		"subnm:"+getSubnm()+","+
		"stcdt:"+getStcdt()+","+
		"lgtd:"+getLgtd()+","+
		"lttd:"+getLttd()+","+
		"dtpr:"+getDtpr()+","+
		"esstym:"+getEsstym()+","+
		"atcunit:"+getAtcunit()+","+
		"locality:"+getLocality()+","+
		"stazt:"+getStazt()+","+
		"dstrvm:"+getDstrvm()+","+
		"usfl:"+getUsfl()+","+
		"comments:"+getComments()+","+
		"moditime:"+getModitime();
	}
}