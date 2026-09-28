package com.twittvl.backend.config;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJacksonJsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext.SerializationPair;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import tools.jackson.databind.jsontype.BasicPolymorphicTypeValidator;
import tools.jackson.databind.jsontype.PolymorphicTypeValidator;

import java.time.Duration;

@Configuration
@EnableCaching
public class RedisConfig {
     //shared serializer for both redisTemplate and cashManager
    @Bean
    public  GenericJacksonJsonRedisSerializer redisJsonSerializer(){
        //only backend package will be deserialized, not front
        PolymorphicTypeValidator validator = BasicPolymorphicTypeValidator.builder()
                .allowIfSubType("com.twittvl.backend")
                .build();
        return GenericJacksonJsonRedisSerializer.builder()
                .enableDefaultTyping(validator)
                .build();
    }

    //main interface for java to interact with redis
    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory connectionFactory,
                                                       GenericJacksonJsonRedisSerializer serializer) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(serializer);
        template.setHashKeySerializer(new StringRedisSerializer());
        template.setHashValueSerializer(serializer);
        template.afterPropertiesSet(); //to end needed configuration
        return template;
    }

    //interface for spring managing cache
    @Bean
    public CacheManager cacheManager(RedisConnectionFactory connectionFactory,
                                     GenericJacksonJsonRedisSerializer serializer) {
        RedisCacheConfiguration defaultConfig = RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofMinutes(10)) // placeholder, for expiration of cache of data after 10 minutes
                .disableCachingNullValues() //null values hold no values, don't cache them
                .serializeKeysWith(SerializationPair.fromSerializer(new StringRedisSerializer())) //store cache keys as string
                .serializeValuesWith(SerializationPair.fromSerializer(serializer)); // store cache values as json

        return RedisCacheManager.builder(connectionFactory)
                .cacheDefaults(defaultConfig)
                .build();
    }
}
//@Cacheable
//     ↓
//CacheManager
//     ↓
//RedisConnectionFactory
//     ↓
//   Redis