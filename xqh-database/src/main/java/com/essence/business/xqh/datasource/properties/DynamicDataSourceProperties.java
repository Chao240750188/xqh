/**
 * Copyright (c) 2020 essence All rights reserved.
 *
 * http://www.iessence.com.cn
 *
 * *版权所有，侵权必究！
 */

package com.essence.business.xqh.datasource.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 **多数据源属性
 *
 * @author Fengjd 421626365@qq.com
 * @since 2.3.5
 */
@ConfigurationProperties(prefix = "dynamic")
public class DynamicDataSourceProperties {
    private Map<String, DataSourceProperties> datasource = new LinkedHashMap<>();

    public Map<String, DataSourceProperties> getDatasource() {
        return datasource;
    }

    public void setDatasource(Map<String, DataSourceProperties> datasource) {
        this.datasource = datasource;
    }
}
