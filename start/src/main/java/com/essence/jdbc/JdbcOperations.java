package com.essence.jdbc;

import com.essence.framework.jpa.Paginator;
import com.essence.framework.jpa.PaginatorParam;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.*;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.jdbc.support.rowset.SqlRowSet;

import java.util.List;
import java.util.Map;


/**
 * 提取自springjdbc JdbcOperations接口中常用的方法<br/>
 * 实现类为 {@link JdbcTemplate}.
 *
 * @author Gavin
 * @company Essence
 * @see JdbcTemplate
 */
public interface JdbcOperations {

    <T> T execute(ConnectionCallback<T> action) throws DataAccessException;

    <T> T execute(StatementCallback<T> action) throws DataAccessException;

    void execute(String sql) throws DataAccessException;

    <T> T queryForObject(String sql, RowMapper<T> rowMapper) throws DataAccessException;

    <T> T queryForObject(String sql, Class<T> requiredType) throws DataAccessException;

    Map<String, Object> queryForMap(String sql) throws DataAccessException;

    <T> List<T> queryForList(String sql, Class<T> elementType) throws DataAccessException;

    <T> List<T> queryForList(String sql, RowMapper<T> rowMapper) throws DataAccessException;

    List<Map<String, Object>> queryForList(String sql) throws DataAccessException;

    SqlRowSet queryForRowSet(String sql) throws DataAccessException;

    int update(String sql) throws DataAccessException;

    int[] batchUpdate(String[] sql) throws DataAccessException;

    <T> T execute(PreparedStatementCreator psc, PreparedStatementCallback<T> action)
            throws DataAccessException;

    <T> T execute(String sql, PreparedStatementCallback<T> action) throws DataAccessException;

    <T> T queryForObject(String sql, Object[] args, int[] argTypes, RowMapper<T> rowMapper)
            throws DataAccessException;

    <T> T queryForObject(String sql, RowMapper<T> rowMapper, Object... args)
            throws DataAccessException;

    <T> T queryForObject(String sql, Object[] args, int[] argTypes, Class<T> requiredType)
            throws DataAccessException;

    <T> T queryForObject(String sql, Class<T> requiredType, Object... args) throws DataAccessException;

    Map<String, Object> queryForMap(String sql, Object[] args, int[] argTypes) throws DataAccessException;

    Map<String, Object> queryForMap(String sql, Object... args) throws DataAccessException;

    <T> List<T> queryForList(String sql, Object[] args, int[] argTypes, Class<T> elementType)
            throws DataAccessException;

    <T> List<T> queryForList(String sql, Class<T> elementType, Object... args) throws DataAccessException;

    <T> List<T> queryForList(String sql, RowMapper<T> rowMapper, Object... args) throws DataAccessException;

    List<Map<String, Object>> queryForList(String sql, Object[] args, int[] argTypes) throws DataAccessException;

    List<Map<String, Object>> queryForList(String sql, Object... args) throws DataAccessException;

    SqlRowSet queryForRowSet(String sql, Object[] args, int[] argTypes) throws DataAccessException;

    SqlRowSet queryForRowSet(String sql, Object... args) throws DataAccessException;

    int update(PreparedStatementCreator psc) throws DataAccessException;

    int update(PreparedStatementCreator psc, KeyHolder generatedKeyHolder) throws DataAccessException;

    int update(String sql, PreparedStatementSetter pss) throws DataAccessException;

    int update(String sql, Object[] args, int[] argTypes) throws DataAccessException;

    int update(String sql, Object... args) throws DataAccessException;

    int[] batchUpdate(String sql, BatchPreparedStatementSetter pss) throws DataAccessException;

    public int[] batchUpdate(String sql, List<Object[]> batchArgs);

    public int[] batchUpdate(String sql, List<Object[]> batchArgs, int[] argTypes);

    <T> T execute(CallableStatementCreator csc, CallableStatementCallback<T> action)
            throws DataAccessException;

    <T> T execute(String callString, CallableStatementCallback<T> action) throws DataAccessException;

    Map<String, Object> call(CallableStatementCreator csc, List<SqlParameter> declaredParameters)
            throws DataAccessException;

    public int updateTable(String table, Object pk, String[] keys, Object... values);

    public <T> Paginator<T> queryForPage(String sql, Class<T> clazz, int page, int pageSize, Object... objectArgs) throws IllegalArgumentException, DataAccessException;

    public <T> T queryForOne(String sql, Class<T> clazz, int index,
                             Object... objectArgs) throws IllegalArgumentException, DataAccessException;

    public <T> Paginator<T> queryForPage(String sql2, StandardModel<T> model, PaginatorParam param);
}
