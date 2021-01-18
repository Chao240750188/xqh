package com.essence.jdbc;

import com.essence.framework.jpa.Paginator;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCallback;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.jdbc.support.rowset.SqlRowSet;

import java.util.List;
import java.util.Map;


/**
 * 提取自springjdbc NamedParameterJdbcOperations接口中常用的方法<br/>
 * 实现类为 {@link JdbcTemplate}.
 *
 * @author Gavin
 * @see NamedParameterJdbcTemplate
 * @see JdbcOperations
 */
public interface NamedParameterJdbcOperations {

    JdbcOperations getJdbcOperations();

    <T> T execute(String sql, SqlParameterSource paramSource, PreparedStatementCallback<T> action)
            throws DataAccessException;

    <T> T execute(String sql, Map<String, ?> paramMap, PreparedStatementCallback<T> action)
            throws DataAccessException;

    <T> T execute(String sql, PreparedStatementCallback<T> action) throws DataAccessException;

    <T> T queryForObject(String sql, SqlParameterSource paramSource, RowMapper<T> rowMapper)
            throws DataAccessException;

    <T> T queryForObject(String sql, Map<String, ?> paramMap, RowMapper<T> rowMapper)
            throws DataAccessException;

    <T> T queryForObject(String sql, SqlParameterSource paramSource, Class<T> requiredType)
            throws DataAccessException;

    <T> T queryForObject(String sql, Map<String, ?> paramMap, Class<T> requiredType)
            throws DataAccessException;

    Map<String, Object> queryForMap(String sql, SqlParameterSource paramSource) throws DataAccessException;

    Map<String, Object> queryForMap(String sql, Map<String, ?> paramMap) throws DataAccessException;

    <T> List<T> queryForList(String sql, SqlParameterSource paramSource, Class<T> elementType)
            throws DataAccessException;

    <T> List<T> queryForList(String sql, Map<String, ?> paramMap, Class<T> elementType)
            throws DataAccessException;

    List<Map<String, Object>> queryForList(String sql, SqlParameterSource paramSource) throws DataAccessException;

    List<Map<String, Object>> queryForList(String sql, Map<String, ?> paramMap) throws DataAccessException;

    SqlRowSet queryForRowSet(String sql, SqlParameterSource paramSource) throws DataAccessException;

    SqlRowSet queryForRowSet(String sql, Map<String, ?> paramMap) throws DataAccessException;

    int update(String sql, SqlParameterSource paramSource) throws DataAccessException;

    int update(String sql, Map<String, ?> paramMap) throws DataAccessException;

    int update(String sql, SqlParameterSource paramSource, KeyHolder generatedKeyHolder)
            throws DataAccessException;

    int update(String sql, SqlParameterSource paramSource, KeyHolder generatedKeyHolder, String[] keyColumnNames)
            throws DataAccessException;

    int[] batchUpdate(String sql, Map<String, ?>[] batchValues);

    int[] batchUpdate(String sql, SqlParameterSource[] batchArgs);

    public <T> Paginator<T> queryForPage(String sql, Class<T> clazz, int page, int pageSize, Map<String, ?> mapArgs)
            throws IllegalArgumentException, DataAccessException;

    public <T> T queryForOne(String sql, Class<T> clazz, int index,
                             Map<String, ?> mapArgs) throws IllegalArgumentException, DataAccessException;
}

