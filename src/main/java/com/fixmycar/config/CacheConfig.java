package com.fixmycar.config;

import com.fixmycar.cache.InMemoryCache;
import com.fixmycar.model.Car;
import java.util.List;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CacheConfig {

    @Bean
    public InMemoryCache<Long, Object> accountCache() {
        return new InMemoryCache<>(300_000, 200);
    }

    @Bean("singleTransactionCache")
    public InMemoryCache<Long, Car> singleTransactionCache() {
        return new InMemoryCache<>(300_000, 100);
    }

    @Bean("transactionListCache")
    public InMemoryCache<String, List<Car>> transactionListCache() {
        return new InMemoryCache<>(300_000, 100);
    }
}