package com.essence.jdbc;

import com.essence.framework.jpa.Paginator;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;


/**
 * sql处理程序
 *
 * @author Gavin
 * @version 1.0 Gavin 2016年8月22日 下午2:10:29
 * @title SqlExcutor.java
 * @since 2016年8月22日 下午2:10:29
 */
public class SqlExcutor {

    /**
     * 将条件拼接到sql语句中，并防止注入
     *
     * @param sql
     * @param map
     * @return
     * @deprecated
     */
    public static SqlAndParam getSql(String sql, Map<String, Object> map) {
        if (map != null && map.size() > 0) {
            SqlAndParam sp = new SqlAndParam();
            List<Object> params = new ArrayList<Object>();
            Iterator<Entry<String, Object>> itr = map.entrySet().iterator();
            StringBuilder sb = new StringBuilder("select * from (" + sql + ") ess_jdbc_temp1 where 1=1");
            while (itr.hasNext()) {
                Entry<String, Object> entry = itr.next();
                String key = entry.getKey();
                Object value = entry.getValue();
                if (key.indexOf(" ") > -1 || key.indexOf("/*") > -1) {
                    sb.append(" and 1=2");
                    sp.setInfo("非法条件：" + key);
                    break;
                } else {
                    sb.append(" and " + key + "?");
                    params.add(value);
                }
            }
            sp.setSql(sb.toString());
            sp.setParam(params.toArray());
            return sp;
        } else {
            return null;
        }
    }

    public static String getQueryForPageSql(String sql, Paginator<?> p) throws IllegalArgumentException {
        if (p.getPageSize() < 0) {
            return sql;
        } else {
            StringBuilder querySql = new StringBuilder();
            String dbtype = JdbcUtil.getDatabaseProductName();
            if ("oracle".equals(dbtype)) {
                querySql.append("SELECT * FROM ( SELECT ESS_JDBC_TEMP_A.*,ROWNUM ESS_JDBC_TEMP_A_ROWNUM FROM ( ");
                querySql.append(sql);
                querySql.append(") ESS_JDBC_TEMP_A WHERE ROWNUM <");
                querySql.append(p.getEndIndex() + 1);
                querySql.append(") WHERE ESS_JDBC_TEMP_A_ROWNUM>");
                querySql.append(p.getStartIndex() - 1);
            } else if ("mysql".equals(dbtype)) {
                querySql.append(sql);
                querySql.append(" limit ")
                        .append(p.getPageSize()).append(" offset ")
                        .append(p.getStartIndex() - 1).toString();
            } else {
                throw new IllegalArgumentException(dbtype + "数据库类型未被支持！");
            }
            return querySql.toString();
        }
    }
}
