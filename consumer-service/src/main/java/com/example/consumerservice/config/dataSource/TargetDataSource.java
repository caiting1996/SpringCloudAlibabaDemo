package com.example.consumerservice.config.dataSource;

import java.lang.annotation.*;


@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface TargetDataSource {
    DataSourceKey  name() default DataSourceKey.DB_1;
}
