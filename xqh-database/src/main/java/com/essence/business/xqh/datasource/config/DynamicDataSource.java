/**
 * Copyright (c) 2020 essence All rights reserved.
 *
 * http://www.iessence.com.cn
 *
 * *版权所有，侵权必究！
 */

package com.essence.business.xqh.datasource.config;

import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

/**
 **多数据源
 *
 * @author Fengjd 421626365@qq.com
 * @since 2.3.5
 */
public class DynamicDataSource extends AbstractRoutingDataSource {

    @Override
    protected Object determineCurrentLookupKey() {
        return DynamicContextHolder.peek();
    }

}
