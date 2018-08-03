package pk.lucidxpo.ynami.spring.features;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.togglz.core.repository.StateRepository;
import org.togglz.core.repository.cache.CachingStateRepository;
import org.togglz.core.repository.jdbc.JDBCStateRepository;

import javax.sql.DataSource;

@Configuration
public class TogglzConfiguration {
    @Autowired
    private DataSource dataSource;

    @Value("${togglz.table.name}")
    private String tableName;

    @Value("${togglz.caching.state.repository.ttl}")
    private Long cachingStateRepositoryTtl;

    /*
     * The CachingStateRepository will act as a cache for persistentStateRepository.
     * It will cache the results for 'cachingStateRepositoryTtl' milliseconds. If you omit the timeout, lookups will be cached
     * until setFeatureState() is called for the specific feature. Using the cache without a timeout only makes sense
     * if the repository state is never modified directly (by modifying the database table for example).
     */
    @Bean
    @ConditionalOnProperty(name = "config.persistable.feature.toggles", havingValue = "true")
    public StateRepository getStateRepository() {
        final StateRepository persistentStateRepository = new JDBCStateRepository(dataSource, tableName);
        return new CachingStateRepository(persistentStateRepository, cachingStateRepositoryTtl);
    }
}