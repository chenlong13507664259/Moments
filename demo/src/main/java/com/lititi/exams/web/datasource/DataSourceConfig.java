package com.lititi.exams.web.datasource;

import com.alibaba.druid.pool.DruidDataSource;
import com.lititi.exams.commons2.datasource.DynamicRoutingDataSource;
import com.lititi.exams.commons2.exception.LttException;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static com.lititi.exams.commons2.constant.Constant.MASTER;
import static com.lititi.exams.commons2.constant.Constant.SLAVE;


@Configuration
@MapperScan(basePackages = DataSourceConfig.BASE_PACKAGE, sqlSessionTemplateRef = "examsSqlSessionTemplate")
public class DataSourceConfig {

    //todo MapperScannerConfigurer实例化在PropertyPlaceholderConfigurer之前，所以value属性无法用在MapperScannerConfigurer实例化中
    public static final String MAPPER_XML_PATH = "classpath:com/lititi/exams/sql/demo/*.xml";
    public static final String BASE_PACKAGE = "com.lititi.exams.web.dao";

    @Bean
    public DataSource examsMasterDataSource() {
        DruidDataSource dataSource = new DruidDataSource();
        dataSource.setUrl("jdbc:mysql://localhost:3306/exams?charset=utf8mb4&useUnicode=true&characterEncoding=utf8&useSSL=false&autoReconnect=true&allowMultiQueries=true");
        dataSource.setUsername("root");
        dataSource.setPassword("123456");
        dataSource.setDriverClassName("com.mysql.cj.jdbc.Driver");
        dataSource.setInitialSize(5);
        dataSource.setMinIdle(5);
        dataSource.setMaxActive(20);
        dataSource.setMaxWait(60000);
        dataSource.setTimeBetweenEvictionRunsMillis(60000);
        dataSource.setMinEvictableIdleTimeMillis(300000);
        dataSource.setValidationQuery("SELECT 1 FROM DUAL");
        dataSource.setTestWhileIdle(true);
        dataSource.setTestOnBorrow(false);
        dataSource.setTestOnReturn(false);
        dataSource.setPoolPreparedStatements(true);
        dataSource.setConnectionInitSqls(Collections.singletonList("set names utf8mb4;"));
        return dataSource;
    }

    @Bean
    public DataSource examsSlaveDataSource() {
        DruidDataSource dataSource = new DruidDataSource();
        dataSource.setUrl("jdbc:mysql://localhost:3306/exams?charset=utf8mb4&useUnicode=true&characterEncoding=utf8&useSSL=false&autoReconnect=true&allowMultiQueries=true");
        dataSource.setUsername("root");
        dataSource.setPassword("123456");
        dataSource.setDriverClassName("com.mysql.cj.jdbc.Driver");
        dataSource.setInitialSize(5);
        dataSource.setMinIdle(5);
        dataSource.setMaxActive(20);
        dataSource.setMaxWait(60000);
        dataSource.setTimeBetweenEvictionRunsMillis(60000);
        dataSource.setMinEvictableIdleTimeMillis(300000);
        dataSource.setValidationQuery("SELECT 1 FROM DUAL");
        dataSource.setTestWhileIdle(true);
        dataSource.setTestOnBorrow(false);
        dataSource.setTestOnReturn(false);
        dataSource.setPoolPreparedStatements(true);
        dataSource.setConnectionInitSqls(Collections.singletonList("set names utf8mb4;"));
        return dataSource;
    }

    @Bean
    public DynamicRoutingDataSource examsDynamicDataSource() {
        DynamicRoutingDataSource dynamicDataSource = new DynamicRoutingDataSource();
        Map<Object, Object> map = new HashMap<>();
        map.put(MASTER, examsMasterDataSource());
        map.put(SLAVE, examsSlaveDataSource());
        dynamicDataSource.setDefaultTargetDataSource(examsMasterDataSource());
        dynamicDataSource.setTargetDataSources(map);
        return dynamicDataSource;
    }

    @Bean
    public PlatformTransactionManager examsTransactionManager(
            @Qualifier("examsDynamicDataSource") DataSource dynamicDataSource) {

        return new DataSourceTransactionManager(dynamicDataSource);
    }

    @Bean("examsSqlSessionFactoryBean")
    public SqlSessionFactoryBean examsSqlSessionFactoryBean(
            @Qualifier("examsDynamicDataSource")DataSource dynamicDataSource) throws IOException {

        SqlSessionFactoryBean sqlSessionFactory = new SqlSessionFactoryBean();
        sqlSessionFactory.setDataSource(dynamicDataSource);
        sqlSessionFactory.setMapperLocations(new PathMatchingResourcePatternResolver().getResources(MAPPER_XML_PATH));
        return sqlSessionFactory;
    }

    @Bean("examsSqlSessionTemplate")
    public SqlSessionTemplate examsSqlSessionTemplate(
            @Qualifier("examsSqlSessionFactoryBean") SqlSessionFactoryBean sqlSessionFactoryBean) throws Exception {
        SqlSessionFactory sqlSessionFactory = sqlSessionFactoryBean.getObject();
        if(sqlSessionFactory == null){
            throw new LttException("sqlSessionFactory 获取失败");
        }
        return new SqlSessionTemplate(sqlSessionFactory);
    }

}
