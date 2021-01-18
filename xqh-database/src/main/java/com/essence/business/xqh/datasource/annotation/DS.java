package com.essence.business.xqh.datasource.annotation;

import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({
        ElementType.METHOD,ElementType.TYPE
})
@Inherited
public @interface DS {
    String value() default "";
}
