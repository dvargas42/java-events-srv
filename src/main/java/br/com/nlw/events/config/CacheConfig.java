package br.com.nlw.events.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;

import java.time.Duration;

@Configuration
public class CacheConfig {

     @Bean
     public RedisCacheManager cacheManager(RedisConnectionFactory redisConnectionFactory) {
         RedisCacheConfiguration config = RedisCacheConfiguration.defaultCacheConfig()
             .entryTtl(Duration.ofMinutes(2)) // Define a expiração
             .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(new GenericJackson2JsonRedisSerializer()));

         return RedisCacheManager.builder(redisConnectionFactory)
             .cacheDefaults(config)
             .build();
     }
}