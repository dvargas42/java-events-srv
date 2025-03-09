package br.com.nlw.events.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

@EnableCaching
@TestConfiguration
public class TestCacheConfig {

  @Bean
  @Primary
  ConcurrentMapCacheManager cacheManager() {
    return new ConcurrentMapCacheManager();
  }
}
