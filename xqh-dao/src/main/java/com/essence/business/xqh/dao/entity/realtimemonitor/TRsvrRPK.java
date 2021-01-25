package com.essence.business.xqh.dao.entity.realtimemonitor;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Date;

/**
 * @Description  
 * @Author  Hunter
 * @Date 2021-01-23 
 */

@Data
public class TRsvrRPK implements Serializable {

	private static final long serialVersionUID =  1915903753984016932L;


	/**
	 * 测站编码：本表指报送水库水情信息代表站的测站编码。
	 */
	private String stcd;

	/**
	 * 时间：水情发生的时间。
	 */
	private Date tm;

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof TRsvrRPK)) return false;
		if (!super.equals(o)) return false;

		TRsvrRPK tRsvrRPK = (TRsvrRPK) o;

		if (getStcd() != null ? !getStcd().equals(tRsvrRPK.getStcd()) : tRsvrRPK.getStcd() != null) return false;
		return getTm() != null ? getTm().equals(tRsvrRPK.getTm()) : tRsvrRPK.getTm() == null;
	}

	@Override
	public int hashCode() {
		int result = super.hashCode();
		result = 31 * result + (getStcd() != null ? getStcd().hashCode() : 0);
		result = 31 * result + (getTm() != null ? getTm().hashCode() : 0);
		return result;
	}
}
