package com.essence.jdbc;

import com.essence.framework.jpa.Paginator;
import com.essence.framework.jpa.PaginatorParam;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.*;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


/**
 * 数据库操作
 * <br/>JdbcOperations与NamedParameterJdbcOperations的实现
 *
 * @author Gavin
 * @version 1.0 Gavin 2016年7月1日 上午9:44:04
 * @title JdbcOperator.java
 * @since 2016年7月1日 上午9:44:04
 */

@Repository
public class JdbcOperator implements DefaultJdbcOperations {
    private DataSource dataSource;
    private JdbcOperations jdbcOperations;
    private NamedParameterJdbcOperations namedJdbcOperations;

    public JdbcOperator(DataSource dataSource) {
        this.dataSource = dataSource;
        this.jdbcOperations = new JdbcTemplate(dataSource);
        this.namedJdbcOperations = new NamedParameterJdbcTemplate(dataSource);
    }

    public DataSource getDataSource() {
        return dataSource;
    }

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
        this.jdbcOperations = new JdbcTemplate(dataSource);
        this.namedJdbcOperations = new NamedParameterJdbcTemplate(dataSource);
    }

    @Override
    public JdbcOperations getJdbcOperations() {
        return jdbcOperations;
    }

    public NamedParameterJdbcOperations getNamedJdbcOperations() {
        return namedJdbcOperations;
    }

    @Override
    public <T> T execute(ConnectionCallback<T> action)
            throws DataAccessException {
        return getJdbcOperations().execute(action);
    }

    @Override
    public <T> T execute(StatementCallback<T> action)
            throws DataAccessException {
        return getJdbcOperations().execute(action);
    }

    @Override
    public void execute(String sql) throws DataAccessException {
        getJdbcOperations().execute(sql);
    }

    @Override
    public <T> T queryForObject(String sql, RowMapper<T> rowMapper)
            throws DataAccessException {
        return getJdbcOperations().queryForObject(sql, rowMapper);
    }

    @Override
    public <T> T queryForObject(String sql, Class<T> requiredType)
            throws DataAccessException {
        return getJdbcOperations().queryForObject(sql, requiredType);
    }

    @Override
    public <T> T queryForObject(String sql, Object[] args, int[] argTypes,
                                RowMapper<T> rowMapper) throws DataAccessException {
        return getJdbcOperations().queryForObject(sql, args, argTypes, rowMapper);
    }

    @Override
    public <T> T queryForObject(String sql, Object[] args, int[] argTypes,
                                Class<T> requiredType) throws DataAccessException {
        return getJdbcOperations().queryForObject(sql, args, argTypes, requiredType);
    }

    @Override
    public <T> T queryForObject(String sql, RowMapper<T> rowMapper,
                                Object... args) throws DataAccessException {
        return getJdbcOperations().queryForObject(sql, rowMapper, args);
    }

    @Override
    public <T> T queryForObject(String sql, Class<T> requiredType,
                                Object... args) throws DataAccessException {
        return getJdbcOperations().queryForObject(sql, requiredType, args);
    }

    @Override
    public Map<String, Object> queryForMap(String sql)
            throws DataAccessException {
        return getJdbcOperations().queryForMap(sql);
    }

    @Override
    public Map<String, Object> queryForMap(String sql, Object[] args,
                                           int[] argTypes) throws DataAccessException {
        return getJdbcOperations().queryForMap(sql, args, argTypes);
    }

    @Override
    public Map<String, Object> queryForMap(String sql, Object... args)
            throws DataAccessException {
        return getJdbcOperations().queryForMap(sql, args);
    }

    @Override
    public <T> List<T> queryForList(String sql, Class<T> elementType)
            throws DataAccessException {
        return getJdbcOperations().queryForList(sql, elementType);
    }

    @Override
    public <T> List<T> queryForList(String sql, RowMapper<T> rowMapper)
            throws DataAccessException {
        return getJdbcOperations().query(sql, rowMapper);
    }

    @Override
    public List<Map<String, Object>> queryForList(String sql)
            throws DataAccessException {
        return getJdbcOperations().queryForList(sql);
    }

    @Override
    public <T> List<T> queryForList(String sql, Object[] args, int[] argTypes,
                                    Class<T> elementType) throws DataAccessException {
        return getJdbcOperations().queryForList(sql, args, argTypes, elementType);
    }

    @Override
    public <T> List<T> queryForList(String sql, Class<T> elementType,
                                    Object... args) throws DataAccessException {
        return getJdbcOperations().queryForList(sql, elementType, args);
    }

    @Override
    public <T> List<T> queryForList(String sql, RowMapper<T> rowMapper,
                                    Object... args) throws DataAccessException {
        return getJdbcOperations().query(sql, rowMapper, args);
    }

    @Override
    public List<Map<String, Object>> queryForList(String sql, Object[] args,
                                                  int[] argTypes) throws DataAccessException {
        return getJdbcOperations().queryForList(sql, args, argTypes);
    }

    @Override
    public List<Map<String, Object>> queryForList(String sql, Object... args)
            throws DataAccessException {
        return getJdbcOperations().queryForList(sql, args);
    }

    @Override
    public SqlRowSet queryForRowSet(String sql) throws DataAccessException {
        return getJdbcOperations().queryForRowSet(sql);
    }

    @Override
    public <T> T execute(PreparedStatementCreator psc,
                         PreparedStatementCallback<T> action) throws DataAccessException {
        return getJdbcOperations().execute(psc, action);
    }

    @Override
    public <T> T execute(String sql, PreparedStatementCallback<T> action)
            throws DataAccessException {
        return getJdbcOperations().execute(sql, action);
    }

    @Override
    public SqlRowSet queryForRowSet(String sql, Object[] args, int[] argTypes)
            throws DataAccessException {
        return getJdbcOperations().queryForRowSet(sql, args, argTypes);
    }

    @Override
    public SqlRowSet queryForRowSet(String sql, Object... args)
            throws DataAccessException {
        return getJdbcOperations().queryForRowSet(sql, args);
    }

    @Override
    public int update(String sql) throws DataAccessException {
        return getJdbcOperations().update(sql);
    }

    @Override
    public int update(PreparedStatementCreator psc) throws DataAccessException {
        return getJdbcOperations().update(psc);
    }

    @Override
    public int update(PreparedStatementCreator psc, KeyHolder generatedKeyHolder)
            throws DataAccessException {
        return getJdbcOperations().update(psc, generatedKeyHolder);
    }

    @Override
    public int update(String sql, PreparedStatementSetter pss)
            throws DataAccessException {
        return getJdbcOperations().update(sql, pss);
    }

    @Override
    public int update(String sql, Object[] args, int[] argTypes)
            throws DataAccessException {
        return getJdbcOperations().update(sql, args, argTypes);
    }

    @Override
    public int update(String sql, Object... args) throws DataAccessException {
        return getJdbcOperations().update(sql, args);
    }

    @Override
    public int[] batchUpdate(String[] sql) throws DataAccessException {
        return getJdbcOperations().batchUpdate(sql);
    }

    @Override
    public int[] batchUpdate(String sql, BatchPreparedStatementSetter pss)
            throws DataAccessException {
        return getJdbcOperations().batchUpdate(sql, pss);
    }

    @Override
    public int[] batchUpdate(String sql, List<Object[]> batchArgs) {
        return batchUpdate(sql, batchArgs, new int[0]);
    }

    @Override
    public int[] batchUpdate(String sql, List<Object[]> batchArgs,
                             int[] argTypes) {
        return BatchUpdateUtils.executeBatchUpdate(sql, batchArgs, argTypes, jdbcOperations);
    }

    @Override
    public <T> T execute(CallableStatementCreator csc,
                         CallableStatementCallback<T> action) throws DataAccessException {
        return getJdbcOperations().execute(csc, action);
    }

    @Override
    public <T> T execute(String callString, CallableStatementCallback<T> action)
            throws DataAccessException {
        return getJdbcOperations().execute(callString, action);
    }

    @Override
    public Map<String, Object> call(CallableStatementCreator csc,
                                    List<SqlParameter> declaredParameters) throws DataAccessException {
        return getJdbcOperations().call(csc, declaredParameters);
    }

    @Override
    public <T> T execute(String sql, SqlParameterSource paramSource,
                         PreparedStatementCallback<T> action) throws DataAccessException {
        return getNamedJdbcOperations().execute(sql, paramSource, action);
    }

    @Override
    public <T> T execute(String sql, Map<String, ?> paramMap,
                         PreparedStatementCallback<T> action) throws DataAccessException {
        return getNamedJdbcOperations().execute(sql, paramMap, action);
    }

    @Override
    public <T> T queryForObject(String sql, SqlParameterSource paramSource,
                                RowMapper<T> rowMapper) throws DataAccessException {
        return getNamedJdbcOperations().queryForObject(sql, paramSource, rowMapper);
    }

    @Override
    public <T> T queryForObject(String sql, Map<String, ?> paramMap,
                                RowMapper<T> rowMapper) throws DataAccessException {
        return getNamedJdbcOperations().queryForObject(sql, paramMap, rowMapper);
    }

    @Override
    public <T> T queryForObject(String sql, SqlParameterSource paramSource,
                                Class<T> requiredType) throws DataAccessException {
        return getNamedJdbcOperations().queryForObject(sql, paramSource, requiredType);
    }

    @Override
    public <T> T queryForObject(String sql, Map<String, ?> paramMap,
                                Class<T> requiredType) throws DataAccessException {
        return getNamedJdbcOperations().queryForObject(sql, paramMap, requiredType);
    }

    @Override
    public Map<String, Object> queryForMap(String sql,
                                           SqlParameterSource paramSource) throws DataAccessException {
        return getNamedJdbcOperations().queryForMap(sql, paramSource);
    }

    @Override
    public Map<String, Object> queryForMap(String sql, Map<String, ?> paramMap)
            throws DataAccessException {
        return getNamedJdbcOperations().queryForMap(sql, paramMap);
    }

    @Override
    public <T> List<T> queryForList(String sql, SqlParameterSource paramSource,
                                    Class<T> elementType) throws DataAccessException {
        return getNamedJdbcOperations().queryForList(sql, paramSource, elementType);
    }

    @Override
    public <T> List<T> queryForList(String sql, Map<String, ?> paramMap,
                                    Class<T> elementType) throws DataAccessException {
        return getNamedJdbcOperations().queryForList(sql, paramMap, elementType);
    }

    @Override
    public List<Map<String, Object>> queryForList(String sql,
                                                  SqlParameterSource paramSource) throws DataAccessException {
        return getNamedJdbcOperations().queryForList(sql, paramSource);
    }

    @Override
    public List<Map<String, Object>> queryForList(String sql,
                                                  Map<String, ?> paramMap) throws DataAccessException {
        return getNamedJdbcOperations().queryForList(sql, paramMap);
    }

    @Override
    public SqlRowSet queryForRowSet(String sql, SqlParameterSource paramSource)
            throws DataAccessException {
        return getNamedJdbcOperations().queryForRowSet(sql, paramSource);
    }

    @Override
    public SqlRowSet queryForRowSet(String sql, Map<String, ?> paramMap)
            throws DataAccessException {
        return getNamedJdbcOperations().queryForRowSet(sql, paramMap);
    }

    @Override
    public int update(String sql, SqlParameterSource paramSource)
            throws DataAccessException {
        return getNamedJdbcOperations().update(sql, paramSource);
    }

    @Override
    public int update(String sql, Map<String, ?> paramMap)
            throws DataAccessException {
        return getNamedJdbcOperations().update(sql, paramMap);
    }

    @Override
    public int update(String sql, SqlParameterSource paramSource,
                      KeyHolder generatedKeyHolder) throws DataAccessException {
        return getNamedJdbcOperations().update(sql, paramSource, generatedKeyHolder);
    }

    @Override
    public int update(String sql, SqlParameterSource paramSource,
                      KeyHolder generatedKeyHolder, String[] keyColumnNames)
            throws DataAccessException {
        return getNamedJdbcOperations().update(sql, paramSource, generatedKeyHolder, keyColumnNames);
    }

    @Override
    public int[] batchUpdate(String sql, Map<String, ?>[] batchValues) {
        return getNamedJdbcOperations().batchUpdate(sql, batchValues);
    }

    @Override
    public int[] batchUpdate(String sql, SqlParameterSource[] batchArgs) {
        return getNamedJdbcOperations().batchUpdate(sql, batchArgs);
    }

    @Override
    public int updateTable(String table, Object pk, String[] keys, Object... values) {
        StringBuilder sql = new StringBuilder("update ");
        sql.append(table);
        sql.append(" set ");
        for (String key : keys) {
            sql.append(key + "=?,");
        }
        sql.deleteCharAt(sql.length() - 1);
        sql.append(" where c_id=?");
        Object[] values2 = new Object[values.length + 1];
        for (int i = 0; i < values.length; i++) {
            values2[i] = values[i];
        }
        values2[values2.length - 1] = pk;
        return update(sql.toString(), values2);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> Paginator<T> queryForPage(String sql, Class<T> clazz, int page, int pageSize, Object... objectArgs)
            throws IllegalArgumentException, DataAccessException {
        Paginator<T> p = new Paginator<T>(page, pageSize);
        String countSql = new StringBuilder("select count(1) from (").append(sql).append(") t").toString();

        Integer count = getJdbcOperations().queryForObject(countSql,
                Integer.class, objectArgs);
        p.setTotalCount(count);
        if (count > 0) {
            String localsql = SqlExcutor.getQueryForPageSql(sql, p);
            if (Map.class.isAssignableFrom(clazz)) {
                List<Map<String, Object>> items = getJdbcOperations().queryForList(localsql, objectArgs);
                p.setItems((List<T>) items);
            } else if (RowMapper.class.isAssignableFrom(clazz)) {
                RowMapper<T> rm = null;
                try {
                    rm = (RowMapper<T>) clazz.newInstance();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                List<T> items = getJdbcOperations().query(localsql, objectArgs, rm);
                p.setItems(items);
            } else {
                throw new IllegalArgumentException(clazz.getName() + "对象未实现RowMapper接口！");
            }
        }
        return p;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> Paginator<T> queryForPage(String sql, Class<T> clazz, int page, int pageSize, Map<String, ?> mapArgs)
            throws IllegalArgumentException, DataAccessException {
        Paginator<T> p = new Paginator<T>(page, pageSize);
        String countSql = new StringBuilder("select count(1) from (").append(sql).append(") t").toString();
        int count = getNamedJdbcOperations().queryForObject(countSql, mapArgs, Integer.class);
        p.setTotalCount(count);
        if (count > 0) {
            String localsql = SqlExcutor.getQueryForPageSql(sql, p);
            if (Map.class.isAssignableFrom(clazz)) {
                List<Map<String, Object>> items = getNamedJdbcOperations().queryForList(localsql, mapArgs);
                p.setItems((List<T>) items);
            } else if (RowMapper.class.isAssignableFrom(clazz)) {
                RowMapper<T> rm = null;
                try {
                    rm = (RowMapper<T>) clazz.newInstance();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                List<T> items = getNamedJdbcOperations().query(localsql, mapArgs, rm);
                p.setItems(items);
            } else {
                throw new IllegalArgumentException(clazz.getName() + "对象未实现RowMapper接口！");
            }
        }
        return p;
    }

    /**
     * 因为推荐使用jpa的分页，所以这里相关复杂搜索做了删减
     */
    @Override
    public <T> Paginator<T> queryForPage(String sql2, StandardModel<T> model, PaginatorParam param)
            throws IllegalArgumentException, DataAccessException {
        int pageSize = param.getPageSize();
        Paginator<T> p = new Paginator<T>(param.getCurrentPage(), pageSize);
        StringBuilder sql = new StringBuilder("select ess_jdbc_temp1.* from (" + sql2 + ") ess_jdbc_temp1");
        //List<PaginatorParamCondition<T>> conditions=param.getConditions();
        //boolean hasSearch=false;

        String search = param.getSearch();
        List<Object> finalParam = new ArrayList<Object>();
        if (search != null
                && !search.equals("")
                && param.getFileds() != null
                && param.getFileds().size() > 0) {
            search = search.trim();
            search = search.replaceAll(" +", " ");
            String[] searchs = search.split(" ");
            List<String> fields = param.getFileds();
            sql.append(" where 1=1");
            for (String aSearch : searchs) {
                sql.append(" and ( 1=2");
                for (int i = 0; i < fields.size(); i++) {
                    sql.append(" or ess_jdbc_temp1." + model.getFieldName(fields.get(i)) + " like ?");
                    finalParam.add("%" + aSearch + "%");
                }
                sql.append(" )");
            }
            //hasSearch=true;
        }
		
		/*if(conditions!=null && conditions.size()>0){
			if(!hasSearch){
				sql.append(" where 1=1");
			}
			for(int i=0;i<conditions.size();i++){
				PaginatorParamCondition<T> ppc=conditions.get(i);
				String fieldName=model.getFieldName(ppc.getName());
				if(fieldName!=null){
					String condition=ppc.getCondition();
					if(condition!=null&&!condition.trim().equals("")){
						condition=condition.trim();
						if(!condition.equals("")){
							if(ppc.getType()==null){
								ppc.setType("string");
							}
							if(ppc.getType().equals("time")){
								if(condition.equals("等于")){
									String dbType=JdbcUtil.getDatabaseProductName().toLowerCase();
									if("oracle".equals(dbType)){
										sql.append(" and to_char(ess_jdbc_temp1."+fieldName+",'yyyy-mm-dd') =?");
									}else if("mysql".equals(dbType)){
										sql.append(" and date_format(ess_jdbc_temp1."+fieldName+",'%Y-%m-%d') =?");
									}else{
										sql.append(" and ess_jdbc_temp1."+fieldName+" =?");
										System.out.println(dbType+":该数据库日期类型未特殊处理，可能搜索结果会有异常！");
									}
									finalParam.add(ppc.getValue());
								}else if(condition.equals("不等于")){
									String dbType2=JdbcUtil.getDatabaseProductName().toLowerCase();
									if("oracle".equals(dbType2)){
										sql.append(" and to_char(ess_jdbc_temp1."+fieldName+",'yyyy-mm-dd') !=?");
									}else if("mysql".equals(dbType2)){
										sql.append(" and date_format(ess_jdbc_temp1."+fieldName+",'%Y-%m-%d') !=?");
									}else{
										sql.append(" and ess_jdbc_temp1."+fieldName+" !=?");
										System.out.println(dbType2+":该数据库日期类型未特殊处理，可能搜索结果会有异常！");
									}
									finalParam.add(ppc.getValue());
								}else if(condition.equals("是空")){
									sql.append(" and ess_jdbc_temp1."+fieldName+" is null");
								}else if(condition.equals("不是空")){
									sql.append(" and ess_jdbc_temp1."+fieldName+" is not null");
								}else if(condition.equals("小于等于")){
									try {
										SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
										Date date=df.parse(ppc.getValue().toString());
										Calendar cal=Calendar.getInstance();
										cal.setTime(date);
										cal.add(Calendar.DAY_OF_MONTH, 1);
										date=cal.getTime();
										ppc.setValue(date);
										sql.append(" and ess_jdbc_temp1."+fieldName+" <?");
										finalParam.add(ppc.getValue());
									} catch (ParseException e) {
									}
								}else if(condition.equals("大于等于")){
									try {
										SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
										Date date=df.parse(ppc.getValue().toString());
										ppc.setValue(date);
										sql.append(" and ess_jdbc_temp1."+fieldName+" >=?");
										finalParam.add(ppc.getValue());
									} catch (ParseException e) {
									}
									break;
								}else{
									System.out.println("错误条件："+condition);
								}
							}else{
								if(condition.equals("等于")){
									sql.append(" and ess_jdbc_temp1."+fieldName+" =?");
									finalParam.add(ppc.getValue());
								}else if(condition.equals("不等于")){
									sql.append(" and ess_jdbc_temp1."+fieldName+" !=?");
									finalParam.add(ppc.getValue());
								}else if(condition.equals("包含")){
									sql.append(" and ess_jdbc_temp1."+fieldName+" like ?");
									finalParam.add("%"+ppc.getValue()+"%");
								}else if(condition.equals("不包含")){
									sql.append(" and ess_jdbc_temp1."+fieldName+" not like ?");
									finalParam.add("%"+ppc.getValue()+"%");
								}else if(condition.equals("开始以")){
									sql.append(" and ess_jdbc_temp1."+fieldName+" like ?");
									finalParam.add(ppc.getValue()+"%");
								}else if(condition.equals("结束以")){
									sql.append(" and ess_jdbc_temp1."+fieldName+" like ?");
									finalParam.add("%"+ppc.getValue());
								}else if(condition.equals("是空")){
									sql.append(" and ess_jdbc_temp1."+fieldName+" is null");
								}else if(condition.equals("不是空")){
									sql.append(" and ess_jdbc_temp1."+fieldName+" is not null");
								}else if(condition.equals("小于")){
									sql.append(" and ess_jdbc_temp1."+fieldName+" < ?");
									finalParam.add(ppc.getValue());
								}else if(condition.equals("小于等于")){
									sql.append(" and ess_jdbc_temp1."+fieldName+" <= ?");
									finalParam.add(ppc.getValue());
								}else if(condition.equals("大于")){
									sql.append(" and ess_jdbc_temp1."+fieldName+" > ?");
									finalParam.add(ppc.getValue());
								}else if(condition.equals("大于等于")){
									sql.append(" and ess_jdbc_temp1."+fieldName+" >= ?");
									finalParam.add(ppc.getValue());
								}else{
									System.out.println("错误条件："+condition);
								}
							}
							
							
							
							if(condition.equals("like")
									||condition.equals("not like")
									){
								sql.append(" and ess_jdbc_temp1."+fieldName+" "+condition+" ?");
								finalParam.add("%"+ppc.getValue()+"%");
							}else if(condition.indexOf(" ")>-1 || condition.indexOf("/*")>-1){
								System.out.println("非法条件："+condition);
							}else{
								sql.append(" and ess_jdbc_temp1."+fieldName+" "+condition+" ?");
								finalParam.add(ppc.getValue());
							}
						}
					}
				}
			}
		}*/

        Object[] objs = finalParam.toArray();
        String countSql = new StringBuilder("select count(1) from (").append(sql).append(") ess_jdbc_temp2").toString();
        int count = queryForObject(countSql, Integer.class, objs);
        p.setTotalCount(count);
        if (count > 0) {
            String localsql = SqlExcutor.getQueryForPageSql(sql.toString(), p);
            List<T> items = queryForList(localsql, model, objs);
            p.setItems(items);
        }
        return p;
    }

    /**
     * 查询第几个
     *
     * @param index 从1开始
     */
    @Override
    @SuppressWarnings("unchecked")
    public <T> T queryForOne(String sql, Class<T> clazz, int index,
                             Object... objectArgs) throws IllegalArgumentException, DataAccessException {
        String localsql = SqlExcutor.getQueryForPageSql(sql, new Paginator<T>(index, 1));
        if (Map.class.isAssignableFrom(clazz)) {
            return (T) getJdbcOperations().queryForMap(localsql, objectArgs);
        } else if (RowMapper.class.isAssignableFrom(clazz)) {
            RowMapper<T> rm = null;
            try {
                rm = (RowMapper<T>) clazz.newInstance();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return queryForObject(localsql, rm, objectArgs);
        } else {
            throw new IllegalArgumentException(clazz.getName() + "对象未实现RowMapper接口！");
        }
    }

    /**
     * 查询第几个
     *
     * @param index 从1开始
     */
    @Override
    @SuppressWarnings("unchecked")
    public <T> T queryForOne(String sql, Class<T> clazz, int index,
                             Map<String, ?> mapArgs) throws IllegalArgumentException, DataAccessException {
        String localsql = SqlExcutor.getQueryForPageSql(sql, new Paginator<T>(index, 1));
        if (Map.class.isAssignableFrom(clazz)) {
            return (T) getNamedJdbcOperations().queryForMap(localsql, mapArgs);
        } else if (RowMapper.class.isAssignableFrom(clazz)) {
            RowMapper<T> rm = null;
            try {
                rm = (RowMapper<T>) clazz.newInstance();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return queryForObject(localsql, mapArgs, rm);
        } else {
            throw new IllegalArgumentException(clazz.getName() + "对象未实现RowMapper接口！");
        }
    }
}
