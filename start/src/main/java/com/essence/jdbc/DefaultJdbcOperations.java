package com.essence.jdbc;

import org.springframework.jdbc.core.JdbcTemplate;

/**
 * 合并两类jdbc的接口
 *
 * @author Gavin
 * @company Essence
 * @see JdbcTemplate
 */
public interface DefaultJdbcOperations extends JdbcOperations, NamedParameterJdbcOperations {

}
