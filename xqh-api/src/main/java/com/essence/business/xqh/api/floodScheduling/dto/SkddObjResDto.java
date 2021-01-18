package com.essence.business.xqh.api.floodScheduling.dto;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 水库信息表实体类
 * @company Essence
 * @author LiuGt
 * @version 1.0 2020/03/30
 */

public class SkddObjResDto implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    /**
     * 水库ID
     */

    private String resCode;

    /**
     * 慧图水库ID
     */

    private String  htGuid;

    /**
     * 水库名称
     */

    private String  resName;

    /**
     * 是否展示到系统 0:否 1:是
     */

    private Integer showStatus;

    /**
     * 所在河流名称
     */

    private String  rvnm;

    /**
     * 水库类型
     */

    private String rsvrtp;

    /**
     * 流域面积（平方公里）
     */

    private BigDecimal drna;

    /**
     * 坝址多年平均径流量（万立方米）
     */

    private BigDecimal avrw;

    /**
     * 主坝高度（m）
     */

    private BigDecimal mdhgt;

    /**
     * 主坝长度（m）
     */

    private BigDecimal mdln;

    /**
     * 最大泄洪流量（立方米/秒）
     */

    private BigDecimal mxotq;

    /**
     * 坝顶高程（m）
     */

    private BigDecimal damel;

    /**
     * 设计洪水标准［重现期］（年）
     */

    private Integer dsflzRcint;

    /**
     * 校核洪水标准［重现期］（年）
     */

    private Integer ckflzRcint;

    /**
     * 设计洪水位(m)
     */

    private BigDecimal dsflz;

    /**
     * 校核洪水位(m)
     */

    private BigDecimal ckflz;

    /**
     * 防洪高水位(m)
     */

    private BigDecimal uzfc;

    /**
     * 正常蓄水位(m)
     */

    private BigDecimal normz;

    /**
     * 汛限水位/防洪限制水位(m)
     */

    private BigDecimal fsltdz;

    /**
     * 死水位(m)
     */

    private BigDecimal ddz;

    /**
     * 总库容（万立方米）
     */

    private BigDecimal ttcp;

    /**
     * 调洪库容（万立方米）
     */

    private BigDecimal flzst;

    /**
     * 防洪库容（万立方米）
     */

    private BigDecimal fldcp;

    /**
     * 兴利库容（万立方米）
     */

    private BigDecimal actcp;

    /**
     * 死库容（万立方米）
     */

    private BigDecimal ddcp;

    /**
     * 评价代表面积/正常蓄水位相应水面面积（平方公里）
     */

    private BigDecimal asar;

    /**
     * 水库管理单位名称
     */

    private String  admnst;

    //水库所在地点

    private String res_addres;

    //大坝类型

    private String damType;

    //挡水主坝类型按材料分

    private String damMaterial;

    //挡水主坝类型按结构分

    private String damStructure;

    //主要泄洪建筑物型式

    private String floodStructure;

    //最大坝高（m）

    private BigDecimal damMaxHgt;

    //坝顶长度（m）

    private BigDecimal damMaxLn;

    //坝项高程（m）

    private BigDecimal damElevation;

    //坝项宽度（m）

    private BigDecimal damWdt;

    //溢洪道型式

    private String spillwayType;

    //堰顶高程（m）

    private BigDecimal crestEl;

    //堰顶净宽（m）

    private BigDecimal crestWdt;

    //设计洪水位下泄流量m3/s

    private BigDecimal dsftq;

    //校核洪水位下泄流量m3/s

    private BigDecimal ckftq;

    //输水设施型式

    private String wtcyfc;

    //输水设施尺寸（m）

    private String wtcysz;

    //设施全长(m)

    private BigDecimal wtcyln;

    //进口高程（m）

    private BigDecimal inkel;

    //多年平均降雨量（mm）

    private Integer avgfl;

    //生产安置人口（万人）

    private BigDecimal pdRstpp;

    //搬迁安置人口（万人）

    private BigDecimal mvRstpp;

    //建成时间（年）

    private Integer cptY;

    //建成时间（月）

    private Integer cptM;

    //水库调节性能

    private String resRp;

    //工程等别

    private String resRpl;

    //高程系统

    private String elSys;

    //设计洪峰流量（立方米/S）

    private BigDecimal dsFpkq;

    //校核洪峰流量（立方米/S）

    private BigDecimal ckFpkq;

    //设计洪水总量（万m3）

    private BigDecimal dsfc;

    //校核洪水总量（万m3）

    private BigDecimal ckfc;

    //有效库容（亿立方米）

    private BigDecimal efcp;

    //影响耕地（亩）

    private Integer infCl;

    //影响人口（人）

    private Integer infPp;

    //设计年供水量

    private BigDecimal dsYSupply;

    //设计灌溉面积（万亩）

    private BigDecimal dsIrrigatedArea;

    //主管部门

    private String cmpDpt;

    public String getResCode() {
        return resCode;
    }

    public void setResCode(String resCode) {
        this.resCode = resCode;
    }

    public String getHtGuid() {
        return htGuid;
    }

    public void setHtGuid(String htGuid) {
        this.htGuid = htGuid;
    }

    public Integer getShowStatus() {
        return showStatus;
    }

    public void setShowStatus(Integer showStatus) {
        this.showStatus = showStatus;
    }

    public String getResName() {
        return resName;
    }

    public void setResName(String resName) {
        this.resName = resName;
    }

    public String getRvnm() {
        return rvnm;
    }

    public void setRvnm(String rvnm) {
        this.rvnm = rvnm;
    }

    public String getRsvrtp() {
        return rsvrtp;
    }

    public void setRsvrtp(String rsvrtp) {
        this.rsvrtp = rsvrtp;
    }

    public BigDecimal getDrna() {
        return drna;
    }

    public void setDrna(BigDecimal drna) {
        this.drna = drna;
    }

    public BigDecimal getAvrw() {
        return avrw;
    }

    public void setAvrw(BigDecimal avrw) {
        this.avrw = avrw;
    }

    public BigDecimal getMdhgt() {
        return mdhgt;
    }

    public void setMdhgt(BigDecimal mdhgt) {
        this.mdhgt = mdhgt;
    }

    public BigDecimal getMdln() {
        return mdln;
    }

    public void setMdln(BigDecimal mdln) {
        this.mdln = mdln;
    }

    public BigDecimal getMxotq() {
        return mxotq;
    }

    public void setMxotq(BigDecimal mxotq) {
        this.mxotq = mxotq;
    }

    public BigDecimal getDamel() {
        return damel;
    }

    public void setDamel(BigDecimal damel) {
        this.damel = damel;
    }

    public Integer getDsflzRcint() {
        return dsflzRcint;
    }

    public void setDsflzRcint(Integer dsflzRcint) {
        this.dsflzRcint = dsflzRcint;
    }

    public Integer getCkflzRcint() {
        return ckflzRcint;
    }

    public void setCkflzRcint(Integer ckflzRcint) {
        this.ckflzRcint = ckflzRcint;
    }

    public BigDecimal getDsflz() {
        return dsflz;
    }

    public void setDsflz(BigDecimal dsflz) {
        this.dsflz = dsflz;
    }

    public BigDecimal getCkflz() {
        return ckflz;
    }

    public void setCkflz(BigDecimal ckflz) {
        this.ckflz = ckflz;
    }

    public BigDecimal getUzfc() {
        return uzfc;
    }

    public void setUzfc(BigDecimal uzfc) {
        this.uzfc = uzfc;
    }

    public BigDecimal getNormz() {
        return normz;
    }

    public void setNormz(BigDecimal normz) {
        this.normz = normz;
    }

    public BigDecimal getFsltdz() {
        return fsltdz;
    }

    public void setFsltdz(BigDecimal fsltdz) {
        this.fsltdz = fsltdz;
    }

    public BigDecimal getDdz() {
        return ddz;
    }

    public void setDdz(BigDecimal ddz) {
        this.ddz = ddz;
    }

    public BigDecimal getTtcp() {
        return ttcp;
    }

    public void setTtcp(BigDecimal ttcp) {
        this.ttcp = ttcp;
    }

    public BigDecimal getFlzst() {
        return flzst;
    }

    public void setFlzst(BigDecimal flzst) {
        this.flzst = flzst;
    }

    public BigDecimal getFldcp() {
        return fldcp;
    }

    public void setFldcp(BigDecimal fldcp) {
        this.fldcp = fldcp;
    }

    public BigDecimal getActcp() {
        return actcp;
    }

    public void setActcp(BigDecimal actcp) {
        this.actcp = actcp;
    }

    public BigDecimal getDdcp() {
        return ddcp;
    }

    public void setDdcp(BigDecimal ddcp) {
        this.ddcp = ddcp;
    }

    public BigDecimal getAsar() {
        return asar;
    }

    public void setAsar(BigDecimal asar) {
        this.asar = asar;
    }

    public String getAdmnst() {
        return admnst;
    }

    public void setAdmnst(String admnst) {
        this.admnst = admnst;
    }

    public String getRes_addres() {
        return res_addres;
    }

    public void setRes_addres(String res_addres) {
        this.res_addres = res_addres;
    }

    public String getDamType() {
        return damType;
    }

    public void setDamType(String damType) {
        this.damType = damType;
    }

    public String getDamMaterial() {
        return damMaterial;
    }

    public void setDamMaterial(String damMaterial) {
        this.damMaterial = damMaterial;
    }

    public String getDamStructure() {
        return damStructure;
    }

    public void setDamStructure(String damStructure) {
        this.damStructure = damStructure;
    }

    public String getFloodStructure() {
        return floodStructure;
    }

    public void setFloodStructure(String floodStructure) {
        this.floodStructure = floodStructure;
    }

    public BigDecimal getDamMaxHgt() {
        return damMaxHgt;
    }

    public void setDamMaxHgt(BigDecimal damMaxHgt) {
        this.damMaxHgt = damMaxHgt;
    }

    public BigDecimal getDamMaxLn() {
        return damMaxLn;
    }

    public void setDamMaxLn(BigDecimal damMaxLn) {
        this.damMaxLn = damMaxLn;
    }

    public BigDecimal getDamElevation() {
        return damElevation;
    }

    public void setDamElevation(BigDecimal damElevation) {
        this.damElevation = damElevation;
    }

    public BigDecimal getDamWdt() {
        return damWdt;
    }

    public void setDamWdt(BigDecimal damWdt) {
        this.damWdt = damWdt;
    }

    public String getSpillwayType() {
        return spillwayType;
    }

    public void setSpillwayType(String spillwayType) {
        this.spillwayType = spillwayType;
    }

    public BigDecimal getCrestEl() {
        return crestEl;
    }

    public void setCrestEl(BigDecimal crestEl) {
        this.crestEl = crestEl;
    }

    public BigDecimal getCrestWdt() {
        return crestWdt;
    }

    public void setCrestWdt(BigDecimal crestWdt) {
        this.crestWdt = crestWdt;
    }

    public BigDecimal getDsftq() {
        return dsftq;
    }

    public void setDsftq(BigDecimal dsftq) {
        this.dsftq = dsftq;
    }

    public BigDecimal getCkftq() {
        return ckftq;
    }

    public void setCkftq(BigDecimal ckftq) {
        this.ckftq = ckftq;
    }

    public String getWtcyfc() {
        return wtcyfc;
    }

    public void setWtcyfc(String wtcyfc) {
        this.wtcyfc = wtcyfc;
    }

    public String getWtcysz() {
        return wtcysz;
    }

    public void setWtcysz(String wtcysz) {
        this.wtcysz = wtcysz;
    }

    public BigDecimal getWtcyln() {
        return wtcyln;
    }

    public void setWtcyln(BigDecimal wtcyln) {
        this.wtcyln = wtcyln;
    }

    public BigDecimal getInkel() {
        return inkel;
    }

    public void setInkel(BigDecimal inkel) {
        this.inkel = inkel;
    }

    public Integer getAvgfl() {
        return avgfl;
    }

    public void setAvgfl(Integer avgfl) {
        this.avgfl = avgfl;
    }

    public BigDecimal getPdRstpp() {
        return pdRstpp;
    }

    public void setPdRstpp(BigDecimal pdRstpp) {
        this.pdRstpp = pdRstpp;
    }

    public BigDecimal getMvRstpp() {
        return mvRstpp;
    }

    public void setMvRstpp(BigDecimal mvRstpp) {
        this.mvRstpp = mvRstpp;
    }

    public Integer getCptY() {
        return cptY;
    }

    public void setCptY(Integer cptY) {
        this.cptY = cptY;
    }

    public Integer getCptM() {
        return cptM;
    }

    public void setCptM(Integer cptM) {
        this.cptM = cptM;
    }

    public String getResRp() {
        return resRp;
    }

    public void setResRp(String resRp) {
        this.resRp = resRp;
    }

    public String getResRpl() {
        return resRpl;
    }

    public void setResRpl(String resRpl) {
        this.resRpl = resRpl;
    }

    public String getElSys() {
        return elSys;
    }

    public void setElSys(String elSys) {
        this.elSys = elSys;
    }

    public BigDecimal getDsFpkq() {
        return dsFpkq;
    }

    public void setDsFpkq(BigDecimal dsFpkq) {
        this.dsFpkq = dsFpkq;
    }

    public BigDecimal getCkFpkq() {
        return ckFpkq;
    }

    public void setCkFpkq(BigDecimal ckFpkq) {
        this.ckFpkq = ckFpkq;
    }

    public BigDecimal getDsfc() {
        return dsfc;
    }

    public void setDsfc(BigDecimal dsfc) {
        this.dsfc = dsfc;
    }

    public BigDecimal getCkfc() {
        return ckfc;
    }

    public void setCkfc(BigDecimal ckfc) {
        this.ckfc = ckfc;
    }

    public BigDecimal getEfcp() {
        return efcp;
    }

    public void setEfcp(BigDecimal efcp) {
        this.efcp = efcp;
    }

    public Integer getInfCl() {
        return infCl;
    }

    public void setInfCl(Integer infCl) {
        this.infCl = infCl;
    }

    public Integer getInfPp() {
        return infPp;
    }

    public void setInfPp(Integer infPp) {
        this.infPp = infPp;
    }

    public BigDecimal getDsYSupply() {
        return dsYSupply;
    }

    public void setDsYSupply(BigDecimal dsYSupply) {
        this.dsYSupply = dsYSupply;
    }

    public BigDecimal getDsIrrigatedArea() {
        return dsIrrigatedArea;
    }

    public void setDsIrrigatedArea(BigDecimal dsIrrigatedArea) {
        this.dsIrrigatedArea = dsIrrigatedArea;
    }

    public String getCmpDpt() {
        return cmpDpt;
    }

    public void setCmpDpt(String cmpDpt) {
        this.cmpDpt = cmpDpt;
    }
}
