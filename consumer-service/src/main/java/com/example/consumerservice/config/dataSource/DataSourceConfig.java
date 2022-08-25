package com.example.consumerservice.config.dataSource;

import com.zaxxer.hikari.HikariDataSource;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;



@Configuration
@MapperScan(basePackages = {"com.gmall.managerservice.mapper"})

public class DataSourceConfig {


    @Bean
    @Primary
    @ConfigurationProperties(prefix = "spring.datasource.xxx")
    public DataSourceProperties firstProperties() {
        return new DataSourceProperties();
    }

    @Bean
    @ConfigurationProperties(prefix = "spring.datasource.xxx")
    public DataSourceProperties secondProperties() {//这是是用hikariCP的时候用的
        return new DataSourceProperties();
    }


    @Bean
    @Primary
    @ConfigurationProperties(prefix = "spring.datasource.xxx")
    public HikariDataSource firstDataSource() {
        return firstProperties().initializeDataSourceBuilder().type(HikariDataSource.class).build();

    }


    @Bean
    @ConfigurationProperties(prefix = "spring.datasource.xxx")
    public HikariDataSource secondDataSource() {

        return secondProperties().initializeDataSourceBuilder().type(HikariDataSource.class).build();
    }


    /**
     * 动态数据源
     * @return 数据源实例
     */
    @Bean
    public DataSource dynamicDataSource() {
        DynamicRoutingDataSource dataSource = new DynamicRoutingDataSource();
        // 设置默认的数据源
        dataSource.setDefaultTargetDataSource(firstDataSource());
        Map<Object, Object> dataSourceMap = new HashMap<>(2);
        dataSourceMap.put(DataSourceKey.DB_1, firstDataSource());
        dataSourceMap.put(DataSourceKey.DB_2, secondDataSource());
        dataSource.setTargetDataSources(dataSourceMap);
        return dataSource;
    }

    @Bean
    public SqlSessionFactory sqlSessionFactory() throws Exception {
        SqlSessionFactoryBean sqlSessionFactoryBean = new SqlSessionFactoryBean();
        sqlSessionFactoryBean.setDataSource(dynamicDataSource());
        //此处设置为了解决找不到mapper文件的问题
        sqlSessionFactoryBean.setMapperLocations(new PathMatchingResourcePatternResolver().getResources("classpath*:mapper/*.xml"));
        //开启自动驼峰命名转换 将数据库下划线转换为java驼峰命名 eg: user_name -> userName
        sqlSessionFactoryBean.getObject().getConfiguration().setMapUnderscoreToCamelCase(true);
        return sqlSessionFactoryBean.getObject();
    }

    @Bean
    public SqlSessionTemplate sqlSessionTemplate() throws Exception {
        return new SqlSessionTemplate(sqlSessionFactory());
    }

    /**
     * 事务管理
     * @return 事务管理实例
     */
    @Bean
    public PlatformTransactionManager platformTransactionManager() {
        return new DataSourceTransactionManager(dynamicDataSource());
    }

}
