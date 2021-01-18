package com.essence.prop;

import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.id.UUIDGenerator;

import java.io.Serializable;
import java.lang.reflect.Method;

/**
 *重写id生成策略方法 CustomUUIDGenerator类的写法;可以实现实体类传id主键就保存你传的主键，如果不传id，就使用系统的uuid。
 * @Author huangxiaoli
 * @Description
 * @Date 15:53 2020/6/5
 * @Param
 * @return
 **/
public class CustormUUIDGenerator extends UUIDGenerator {

    @Override
    public Serializable generate(SharedSessionContractImplementor session, Object object) throws HibernateException {
        Object id =  getFieldValueByName("id", object);
        if (id != null) {
            return (Serializable) id;
        }
        return super.generate(session, object);
    }

    private Object getFieldValueByName(String fieldName, Object o) {
        try {
            String firstLetter = fieldName.substring(0, 1).toUpperCase();
            String getter = "get" + firstLetter + fieldName.substring(1);
            Method method = o.getClass().getMethod(getter, new Class[] {});
            Object value = method.invoke(o, new Object[] {});
            return value;
        } catch (Exception e) {
            return null;
        }
    }
}