package com.lititi.exams.commons2.config;

import com.lititi.exams.commons2.cache.CacheOperator;
import com.lititi.exams.commons2.cache.LttCacheManager;
import com.lititi.exams.commons2.cache.RedisCache;
import com.lititi.exams.commons2.cache.serializer.redis.FastJsonSerializer;
import com.lititi.exams.commons2.cache.serializer.redis.LttJDKSerializer;
import com.lititi.exams.commons2.cache.serializer.redis.RedisSessionJavaSerializer;
import com.lititi.exams.commons2.enumeration.RedisDB;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import redis.clients.jedis.JedisPoolConfig;

import java.time.Duration;
import java.util.HashSet;
import java.util.Set;

@Configuration
@ConditionalOnMissingBean({CacheOperator.class})
@EnableConfigurationProperties({LttRedisProperties.class})
public class CacheOperatorAutoConfiguration {

    @Autowired
    private LttRedisProperties lttRedisProperties;

    @Bean
    public FastJsonSerializer fastJsonSerializer() {
        return new FastJsonSerializer();
    }

    @Bean
    public LttJDKSerializer lttJDKSerializer() {
        return new LttJDKSerializer();
    }

    @Bean
    public RedisSessionJavaSerializer lttJavaSerializer() {
        return new RedisSessionJavaSerializer();
    }

    /**
     * @return jedisPoolConfig
     */
    @Bean
    public JedisPoolConfig jedisPoolConfig() {
        JedisPoolConfig poolConfig = new JedisPoolConfig();
        LttRedisProperties.Pool pool = lttRedisProperties.getPool();
        if (pool != null) {
            poolConfig.setMaxTotal(pool.getMaxActive());
            poolConfig.setMaxIdle(pool.getMaxIdle());
            poolConfig.setMinIdle(pool.getMinIdle());
            poolConfig.setMaxWaitMillis(pool.getMaxWait().toMillis());
            Duration timeBetweenEvictionRuns = pool.getTimeBetweenEvictionRuns();
            if (timeBetweenEvictionRuns != null) {
                poolConfig.setTimeBetweenEvictionRunsMillis(timeBetweenEvictionRuns.toMillis());
            }
        }
        return poolConfig;
    }

    /**
     * @return jedisConnectionFactoryOtherMaster
     */
    private JedisConnectionFactory getJedisConnectionFactoryOtherMaster() {
        RedisStandaloneConfiguration redisStandaloneConfiguration = new RedisStandaloneConfiguration();
        LttRedisProperties.Other other = lttRedisProperties.getOther();
        if (other != null && other.getMaster() != null) {
            LttRedisProperties.Other.Master master = other.getMaster();
            redisStandaloneConfiguration.setDatabase(RedisDB.OTHER.value());
            redisStandaloneConfiguration.setPassword(master.getPassword());
            redisStandaloneConfiguration.setHostName(master.getHost());
            redisStandaloneConfiguration.setPort(master.getPort());
            redisStandaloneConfiguration.setUsername(master.getUsername());
        } else {
            // 设置默认值
            redisStandaloneConfiguration.setDatabase(RedisDB.OTHER.value());
            redisStandaloneConfiguration.setHostName("localhost");
            redisStandaloneConfiguration.setPort(6379);
        }
        return new JedisConnectionFactory(redisStandaloneConfiguration);
    }

    /**
     * @return jedisConnectionFactoryOtherSlave
     */
    private JedisConnectionFactory getJedisConnectionFactoryOtherSlave() {
        RedisStandaloneConfiguration redisStandaloneConfiguration = new RedisStandaloneConfiguration();
        LttRedisProperties.Other other = lttRedisProperties.getOther();
        if (other != null && other.getSlave() != null) {
            LttRedisProperties.Other.Slave slave = other.getSlave();
            redisStandaloneConfiguration.setDatabase(RedisDB.OTHER.value());
            redisStandaloneConfiguration.setPassword(slave.getPassword());
            redisStandaloneConfiguration.setHostName(slave.getHost());
            redisStandaloneConfiguration.setPort(slave.getPort());
            redisStandaloneConfiguration.setUsername(slave.getUsername());
        } else {
            // 设置默认值
            redisStandaloneConfiguration.setDatabase(RedisDB.OTHER.value());
            redisStandaloneConfiguration.setHostName("localhost");
            redisStandaloneConfiguration.setPort(6379);
        }
        return new JedisConnectionFactory(redisStandaloneConfiguration);
    }


    /**
     * @return jedisConnectionSessionSlave
     */
    private JedisConnectionFactory getJedisConnectionFactorySessionSlave() {
        RedisStandaloneConfiguration redisStandaloneConfiguration = new RedisStandaloneConfiguration();
        LttRedisProperties.Session session = lttRedisProperties.getSession();
        if (session != null && session.getSlave() != null) {
            LttRedisProperties.Session.Slave slave = session.getSlave();
            redisStandaloneConfiguration.setDatabase(RedisDB.SESSION.value());
            redisStandaloneConfiguration.setPassword(slave.getPassword());
            redisStandaloneConfiguration.setHostName(slave.getHost());
            redisStandaloneConfiguration.setPort(slave.getPort());
            redisStandaloneConfiguration.setUsername(slave.getUsername());
        } else {
            // 设置默认值
            redisStandaloneConfiguration.setDatabase(RedisDB.SESSION.value());
            redisStandaloneConfiguration.setHostName("localhost");
            redisStandaloneConfiguration.setPort(6379);
        }
        return new JedisConnectionFactory(redisStandaloneConfiguration);
    }
    /**
     * @return jedisConnectionSessionMaster
     */
    private JedisConnectionFactory getJedisConnectionFactorySessionMaster() {
        RedisStandaloneConfiguration redisStandaloneConfiguration = new RedisStandaloneConfiguration();
        LttRedisProperties.Session session = lttRedisProperties.getSession();
        if (session != null && session.getMaster() != null) {
            LttRedisProperties.Session.Master master = session.getMaster();
            redisStandaloneConfiguration.setDatabase(RedisDB.SESSION.value());
            redisStandaloneConfiguration.setPassword(master.getPassword());
            redisStandaloneConfiguration.setHostName(master.getHost());
            redisStandaloneConfiguration.setPort(master.getPort());
            redisStandaloneConfiguration.setUsername(master.getUsername());
        } else {
            // 设置默认值
            redisStandaloneConfiguration.setDatabase(RedisDB.SESSION.value());
            redisStandaloneConfiguration.setHostName("localhost");
            redisStandaloneConfiguration.setPort(6379);
        }
        return new JedisConnectionFactory(redisStandaloneConfiguration);
    }


    /**
     * @param lttJDKSerializer
     * @return redisTemplateOtherMaster
     */
    @Bean
    public RedisTemplate redisTemplateOtherMaster(LttJDKSerializer lttJDKSerializer) {
        RedisTemplate template = new RedisTemplate();
        template.setDefaultSerializer(lttJDKSerializer);
        template.setConnectionFactory(getJedisConnectionFactoryOtherMaster());
        return template;
    }

    /**
     * @param lttJDKSerializer
     * @return redisTemplateOtherSlave
     */
    @Bean
    public RedisTemplate redisTemplateOtherSlave(LttJDKSerializer lttJDKSerializer) {
        RedisTemplate template = new RedisTemplate();
        template.setDefaultSerializer(lttJDKSerializer);
        template.setConnectionFactory(getJedisConnectionFactoryOtherSlave());
        return template;
    }

    /**
     * @param lttJDKSerializer
     * @return redisTemplateSessionMaster
     */
    @Bean
    public RedisTemplate redisTemplateSessionMaster(LttJDKSerializer lttJDKSerializer) {
        RedisTemplate template = new RedisTemplate();
        template.setDefaultSerializer(lttJDKSerializer);
        template.setConnectionFactory(getJedisConnectionFactorySessionMaster());
        return template;
    }


    /**
     * @param lttJavaSerializer
     * @return redisTemplateSessionSlave
     */
    @Bean
    public RedisTemplate redisTemplateSessionSlave(RedisSessionJavaSerializer lttJavaSerializer) {
        RedisTemplate template = new RedisTemplate();
        template.setDefaultSerializer(lttJavaSerializer);
        template.setConnectionFactory(getJedisConnectionFactorySessionSlave());
        return template;
    }

    @Bean
    public LttCacheManager cacheManager(RedisTemplate redisTemplateOtherMaster, RedisTemplate redisTemplateOtherSlave,
        RedisTemplate redisTemplateSessionMaster, RedisTemplate redisTemplateSessionSlave) {
        LttCacheManager cacheManager = new LttCacheManager();
        Set<RedisCache> caches = new HashSet<>();
        RedisCache otherCacheMaster = new RedisCache("OtherCacheMaster", redisTemplateOtherMaster, 0);
        RedisCache otherCacheSlave = new RedisCache("OtherCacheSlave", redisTemplateOtherSlave, 0);
        RedisCache sessionCacheMaster = new RedisCache("SessionCacheMaster", redisTemplateSessionMaster, 0);
        RedisCache sessionVacheSlave = new RedisCache("SessionCacheSlave", redisTemplateSessionSlave, 0);
        caches.add(otherCacheMaster);
        caches.add(otherCacheSlave);
        caches.add(sessionCacheMaster);
        caches.add(sessionVacheSlave);
        cacheManager.setCaches(caches);
        return cacheManager;
    }

    @Bean
    public CacheOperator cacheOperator() {
        return new CacheOperator();
    }
}
