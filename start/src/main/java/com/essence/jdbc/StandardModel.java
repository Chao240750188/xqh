package com.essence.jdbc;

import org.springframework.jdbc.core.RowMapper;

import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

/**
 * 标准Model对象
 *
 * @param <T>
 * @author Gavin
 * @version 1.0 Gavin 2016年8月27日 下午1:39:37
 * @title StandardModel.java
 * @since 2016年8月27日 下午1:39:37
 */
public interface StandardModel<T> extends Serializable, RowMapper<T> {
    public T mapRow(ResultSet rs, int rowNum) throws SQLException;

    public String getFieldName(String attribute);

    public Map<String, Object> toMap();

    public Map<String, Object> pksMap();
}
