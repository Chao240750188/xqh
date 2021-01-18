package com.essence.jdbc;

import java.util.Arrays;

/**
 * 生成sql和对应条件，过时的，推荐用PaginatorParam
 *
 * @author Gavin
 * @version 1.0 Gavin 2016年9月1日 下午6:47:59
 * @title SqlAndParam.java
 * @since 2016年9月1日 下午6:47:59
 * @deprecated
 */
public class SqlAndParam {
    private String sql;
    private Object[] param;
    private String info;

    @Override
    public String toString() {
        return "SqlAndParam [sql=" + sql + ", param=" + Arrays.toString(param) + ", info=" + info + "]";
    }

    public String getSql() {
        return sql;
    }

    public void setSql(String sql) {
        this.sql = sql;
    }

    public Object[] getParam() {
        return param;
    }

    public void setParam(Object[] param) {
        this.param = param;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

}
