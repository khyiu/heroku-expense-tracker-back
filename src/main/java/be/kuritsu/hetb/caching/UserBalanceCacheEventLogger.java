package be.kuritsu.hetb.caching;

import java.math.BigDecimal;

import org.ehcache.event.CacheEvent;
import org.ehcache.event.CacheEventListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UserBalanceCacheEventLogger implements CacheEventListener<String, BigDecimal> {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserBalanceCacheEventLogger.class);

    @Override
    public void onEvent(CacheEvent<? extends String, ? extends BigDecimal> event) {
        LOGGER.info("Cache : {} - [{}] cache event fire - {} - {}", CacheNames.USER_BALANCE_CACHE,
                event.getType(), event.getKey(), event.getNewValue());
    }
}
