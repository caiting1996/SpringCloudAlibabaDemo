package com.example.consumerservice.config;


import com.example.basecommon.utils.utils.RedisUtil;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.ClusterServersConfig;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;



@Configuration
public class RedisConfig extends CachingConfigurerSupport {

    @Bean
    public CacheManager cacheManager(final LettuceConnectionFactory factory) {
        RedisCacheManager.RedisCacheManagerBuilder builder
                = RedisCacheManager.RedisCacheManagerBuilder.fromConnectionFactory(factory);

        return builder.build();
    }

    @Bean(name = "springSessionDefaultRedisSerializer")
    public GenericJackson2JsonRedisSerializer genericJackson2JsonRedisSerializer( ) {
        return new GenericJackson2JsonRedisSerializer();
    }

    @Bean
    @ConditionalOnMissingBean(name = "redisTemplate")
    public RedisTemplate<String, Object> redisTemplate(final LettuceConnectionFactory connectionFactory) {
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(connectionFactory);
        // 配置默认的序列化器
        redisTemplate.setDefaultSerializer(genericJackson2JsonRedisSerializer());

        StringRedisSerializer stringRedisSerializer = new StringRedisSerializer();
        // 设置 Key 的默认序列化机制
        redisTemplate.setKeySerializer(stringRedisSerializer);
        // hash的key也采用String的序列化方式
        redisTemplate.setHashKeySerializer(stringRedisSerializer);
        redisTemplate.afterPropertiesSet();
        return redisTemplate;
    }


    @Autowired
    RedisTemplate<String, Object> redisTemplate;

    @Bean
    @ConditionalOnBean(RedisTemplate.class)
    public RedisUtil redisUtil( ) {
        return new RedisUtil(redisTemplate);
    }

    @Bean
    public RedissonClient redisClient() {
        Config config = new Config();
        ClusterServersConfig clusterServersConfig=config.useClusterServers();
        clusterServersConfig.addNodeAddress("redis://");
        //这里ip和port改成自己的redis地址和端口

        return Redisson.create(config);
    }

}
