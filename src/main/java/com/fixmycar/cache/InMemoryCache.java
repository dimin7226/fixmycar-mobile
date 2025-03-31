package com.fixmycar.cache;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@SuppressWarnings("squid:S6829")
@Component
public class InMemoryCache<K, V> {

    private static final Logger logger = LoggerFactory.getLogger(InMemoryCache.class);

    private static class CacheEntry<V> {
        @Getter
        private final V value;
        private final long expiryTime;

        public CacheEntry(V value, long expiryTime) {
            this.value = value;
            this.expiryTime = expiryTime;
        }

        public boolean isExpired() {
            return System.currentTimeMillis() >= expiryTime;
        }
    }

    private final Map<K, CacheEntry<V>> cache;
    private final long ttlMillis;

    public InMemoryCache() {
        this(300_000, 100);  // TTL 5 минут, максимальный размер 100
    }

    public InMemoryCache(long ttlMillis, int maxSize) {
        this.ttlMillis = ttlMillis;
        this.cache = new LinkedHashMap<>(maxSize, 0.75f, true) {
            @Override
            protected boolean removeEldestEntry(Map.Entry<K, CacheEntry<V>> eldest) {
                boolean shouldRemove = size() > maxSize;
                if (shouldRemove) {
                    logger.info("Evicting eldest entry: {}", eldest.getKey());
                }
                return shouldRemove;
            }
        };

        ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
        scheduler.scheduleAtFixedRate(this::evictExpiredEntries,
                ttlMillis, ttlMillis, TimeUnit.MILLISECONDS);
    }

    public V get(K key) {
        CacheEntry<V> entry = cache.get(key);
        if (entry == null) {
            logger.info("Cache miss for key: {}", key);
            return null;
        }
        if (entry.isExpired()) {
            logger.info("Cache entry expired for key: {}", key);
            cache.remove(key);
            return null;
        }
        logger.info("Cache hit for key: {}", key);
        return entry.getValue();
    }

    public void put(K key, V value) {
        CacheEntry<V> entry = new CacheEntry<>(value, System.currentTimeMillis() + ttlMillis);
        cache.put(key, entry);
        logger.info("Cache put for key: {}", key);
    }

    public void evict(K key) {
        cache.remove(key);
        logger.info("Cache evict for key: {}", key);
    }

    public void clear() {
        cache.clear();
        logger.info("Cache cleared");
    }

    private void evictExpiredEntries() {
        cache.entrySet().removeIf(e -> e.getValue().isExpired());
    }
}
