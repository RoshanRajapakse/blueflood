/*
 * Copyright 2013 Rackspace
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.rackspacecloud.blueflood.service;

/**
 * Default config values for blueflood-core. Also to be used for getting config key names.
 */
public enum CoreConfig implements ConfigDefaults {
    CASSANDRA_HOSTS("127.0.0.1:19180"),
    DEFAULT_CASSANDRA_PORT("19180"),
    CASSANDRA_BINXPORT_HOSTS("localhost:9042"),
    CASSANDRA_BINXPORT_PORT("9042"),
    CASSANDRA_LOCAL_DATACENTER_NAME("datacenter1"),

    // This number is only accurate if MAX_CASSANDRA_CONNECTIONS
    // is evenly divisible by number of hosts. For its connection
    // pool, the driver class calculate the max number of
    // connections per host, by dividing this number with
    // the number of hosts. This is only being used by Astyanax
    // driver.
    MAX_CASSANDRA_CONNECTIONS("75"),

    CASSANDRA_DRIVER("astyanax"),

    // Datastax related connection pool settings
    // http://docs.datastax.com/en/developer/java-driver/3.2/manual/pooling/
    DATASTAX_CORE_CONNECTIONS_PER_HOST("5"),
    DATASTAX_MAX_CONNECTIONS_PER_HOST("10"),
    DATASTAX_MAX_REQUESTS_PER_CONNECTION("1024"),
    DATASTAX_READ_TIMEOUT_MAX_RETRIES("3"),
    DATASTAX_WRITE_TIMEOUT_MAX_RETRIES("3"),
    DATASTAX_UNAVAILABLE_MAX_RETRIES("1"),

    ROLLUP_KEYSPACE("DATA"),
    CLUSTER_NAME("Test Cluster"),

    INGESTION_MODULES(""),
    QUERY_MODULES(""),
    DISCOVERY_MODULES(""),
    TOKEN_DISCOVERY_MODULES(""),
    EVENT_LISTENER_MODULES(""),
    EVENTS_MODULES(""),

    MAX_LOCATOR_FETCH_THREADS("2"),
    MAX_ROLLUP_READ_THREADS("20"),
    MAX_ROLLUP_WRITE_THREADS("5"),

    // Discovery refers to indexing the details of locators so that they're easily searchable. Discovery modules
    // implement specific discovery mechanisms. Elasticsearch is an example of a discovery backend. Locators are indexed
    // in Elasticsearch and retrieved later to fulfill query requests.
    //
    // To avoid overwhelming a backend service like Elasticsearch, in-memory caches keep track of details that have
    // already been written. For any given time series identified by a unique locator, that time series only needs
    // discovery data written one time, so after writing a locator, the fact that it's written is cached, and we don't
    // write the same locator again. See the LOCATOR_CACHE_* and TOKEN_CACHE_* settings for configuring the
    // discovery-related caches.
    //
    // Blueflood startup is a special case that has to be dealt with. At startup, the caches are empty, so Blueflood
    // treats every locator it sees as new. This can easily lead to an excessive number of calls to a discovery backend
    // like Elasticsearch. In addition to the caches, Blueflood has discovery throttling to help avoid this case. This
    // throttling can be used to slow down discovery writes on ingestion. Slowing it down reduces overall impact on
    // Elasticsearch performance. The tradeoff is that it slows the rate at which new locators are discovered, so they
    // can't be queried immediately.

    // Sets the maximum number of new locators to write to discovery backends per minute. This is a global limit that
    // covers all tenants. It's not a fair throttle, meaning that if one tenant floods the system and maxes out the
    // throttle, all other tenants will be throttled as well. As such, set this as high as you can without causing
    // instability in your discovery backends. The configured number isn't precise. Ingestion requests are always fully
    // processed, and throttling happens between requests.
    //
    // When setting the global throttle, be sure to take into account how many Blueflood nodes you have, as well as how
    // many of them typically restart at the same time. This corresponds directly to the load Blueflood will put on
    // discovery backends while the caches fill at startup. Effectively, this setting should be something like:
    // (max acceptable backend writes) / (number of Blueflood nodes that can start/restart simultaneously)
    DISCOVERY_MAX_NEW_LOCATORS_PER_MINUTE("50000"),
    // Sets the maximum number of new locators to write to discovery backends per tenant per minute. This is a fair
    // throttle that will throttle each tenant independently. Normally you'll set this significantly lower than
    // DISCOVERY_MAX_NEW_LOCATORS_PER_MINUTE to give all tenants a fair chance of their metrics being processed for
    // discovery. The global throttle overrides the per-tenant throttle. Even if a tenant hasn't reached its per-tenant
    // limit, the global throttle can halt the tenant's discovery writes.
    DISCOVERY_MAX_NEW_LOCATORS_PER_MINUTE_PER_TENANT("500"),
    DISCOVERY_WRITER_MIN_THREADS("5"),
    DISCOVERY_WRITER_MAX_THREADS("50"),

    MAX_DISCOVERY_RESULT_SIZE("10000"),

    TOKEN_DISCOVERY_WRITER_MIN_THREADS("5"),
    TOKEN_DISCOVERY_WRITER_MAX_THREADS("50"),

    // Maximum threads that would access the cache concurrently
    META_CACHE_MAX_CONCURRENCY("50"),

    // Setting this to true will enable batched meta reads and writes from db (lazy loads and writes)
    META_CACHE_BATCHED_READS("false"),
    META_CACHE_BATCHED_WRITES("false"),

    META_CACHE_BATCHED_READS_THRESHOLD("100"), // how many rows to read at a time? (batch size)
    META_CACHE_BATCHED_READS_TIMER_MS("10"),  // how often to read? (batch timer) (read faster than writes)
    META_CACHE_BATCHED_READS_PIPELINE_DEPTH("10"), // how many outstanding batches? (1 thread per batch).
    META_CACHE_BATCHED_READS_QUEUE_SIZE("1000"),

    META_CACHE_BATCHED_WRITES_THRESHOLD("100"),  // how many meta columns to write at a time? (batch size)
    META_CACHE_BATCHED_WRITES_TIMER_MS("20"),   // how often to write? (batch timer)
    META_CACHE_BATCHED_WRITES_PIPELINE_DEPTH("10"), // how many outstanding batches? (1 thread per batch).
    META_CACHE_BATCHED_WRITES_QUEUE_SIZE("1000"),

    // Maximum timeout waiting on exhausted connection pools in milliseconds.
    // Maps directly to Astyanax's ConnectionPoolConfiguration.setMaxTimeoutWhenExhausted
    MAX_TIMEOUT_WHEN_EXHAUSTED("2000"),
    SCHEDULE_POLL_PERIOD("60000"),

    // Config refresh interval (If a new config is pushed out, we need to pick up the changes)
    // time is in milliseconds
    CONFIG_REFRESH_PERIOD("10000"),

    // this is a special string, or a comma list of integers. e.g.: "1,2,3,4"
    // valid shards are 0..127
    SHARDS("ALL"),

    // thread sleep times between shard push/pulls.
    SHARD_PUSH_PERIOD("2000"),
    SHARD_PULL_PERIOD("20000"),

    // blueflood uses zookeeper to acquire locks before working on shards
    ZOOKEEPER_CLUSTER("NONE"),

    // 20 min
    SHARD_LOCK_HOLD_PERIOD_MS("1200000"),
    // 1 min
    SHARD_LOCK_DISINTERESTED_PERIOD_MS("60000"),
    // 2 min
    SHARD_LOCK_SCAVENGE_INTERVAL_MS("120000"),
    MAX_ZK_LOCKS_TO_ACQUIRE_PER_CYCLE("1"),

    INTERNAL_API_CLUSTER("127.0.0.1:50020,127.0.0.1:50020"),

    RIEMANN_HOST(""), //string: address of riemann server where events should be sent.
    RIEMANN_PORT("5555"),
    RIEMANN_PREFIX(""), //string: prefix metric names with this. useful for telling metrics from backfiller vs normal BF instance. (RIEMANN_LOCALHOST and RIEMANN_TAGS are better though)
    RIEMANN_LOCALHOST(""), //string: name of the server blueflood is running on.
    RIEMANN_TAGS(""), //comma-delimited list of strings: tags to append to metric events. ex- blueflood,ingest
    RIEMANN_SEPARATOR(""), //string: separator between metric name components. if set to "|" would result in: prefix|metric_name|rate_5m
    RIEMANN_TTL(""), //float: number of seconds until metric TTLs out of riemann's index

    GRAPHITE_HOST(""),
    GRAPHITE_PORT("2003"),
    GRAPHITE_PREFIX("unconfiguredNode.metrics."),
    GRAPHITE_REPORT_PERIOD_SECONDS("30"),

    INGEST_MODE("true"),
    ROLLUP_MODE("true"),
    QUERY_MODE("true"),

    METRICS_BATCH_WRITER_THREADS("50"),

    METRIC_BATCH_SIZE("100"),

    // The locator cache is a local, in-memory cache that keeps track of locators that have been inserted into Cassandra
    // and Elasticsearch for various tracking purposes, like marking a delayed locator dirty for rollup or indexing a
    // locator and tokens for later querying. The cache is used to avoid unnecessary writes to these remote systems.

    // This setting tunes the concurrency of the locator cache. All threads writing to the database contend for access
    // to this cache. Setting the concurrency too low will impact metric writing throughput.
    LOCATOR_CACHE_CONCURRENCY("16"),
    // Sets the length of time a locator is kept in the cache after it's written or last accessed. Any given locator
    // will typically be seen at regular intervals in a production setting, but load balancing can spread occurrences of
    // a locator across multiple Blueflood nodes. This setting should account for the combination of both. For instance,
    // if data points for a locator arrive every minute and there are six Blueflood nodes, setting this any lower than 7
    // is virtually guaranteed to expire cache entries too early. In reality, it's probably necessary to set it
    // significantly higher due to other factors affecting the distribution of locators around nodes.
    LOCATOR_CACHE_TTL_MINUTES("60"),
    // Sets the length of time a delayed locator is kept in the cache. This guards against excessive writes to the
    // "delayed locator" column family in cassandra. Note that both the locator and the slot are taken into account
    // here, so a single locator could account for multiple entries in this portion of the cache.
    LOCATOR_CACHE_DELAYED_TTL_SECONDS("30"),

    // The token cache is similar in use to the locator cache, described previously. When
    // ENABLE_TOKEN_SEARCH_IMPROVEMENTS is turned on, both the locator cache and the token cache are used to avoid
    // unnecessary writes to Elasticsearch. The token cache specifically caches the non-leaf tokens of a locator.
    TOKEN_CACHE_TTL_MINUTES("60"),

    CASSANDRA_REQUEST_TIMEOUT("10000"),
    // set <= 0 to not retry
    CASSANDRA_MAX_RETRIES("5"),

    // v1.0 defaults to ','. This configuration option provides backwards compatibility.
    // Using legacy separators is deprecated as of 2.0 and will be removed in 3.0
    USE_LEGACY_METRIC_SEPARATOR("false"),

    ROLLUP_BATCH_MIN_SIZE("5"),
    ROLLUP_BATCH_MAX_SIZE("100"),

    // Assume, for calculating granularity for GetByPoints queries, that data is sent at this interval.
    GET_BY_POINTS_ASSUME_INTERVAL("30000"),

    // Rollups repair on read
    REPAIR_ROLLUPS_ON_READ("true"),

    // valid options are: GEOMETRIC, LINEAR, and LESSTHANEQUAL
    GET_BY_POINTS_GRANULARITY_SELECTION("GEOMETRIC"),

    METADATA_CACHE_PERSISTENCE_ENABLED("false"),
    METADATA_CACHE_PERSISTENCE_PATH("/dev/null"),
    METADATA_CACHE_PERSISTENCE_PERIOD_MINS("10"),
    META_CACHE_RETENTION_IN_MINUTES("10"),
    
    // how long we typically wait to schedule a rollup.
    ROLLUP_DELAY_MILLIS("300000"),
    SHORT_DELAY_METRICS_ROLLUP_DELAY_MILLIS("300000"),
    LONG_DELAY_METRICS_ROLLUP_WAIT_MILLIS("300000"),
    TRACKER_DELAYED_METRICS_MILLIS("300000"),

    //the granularity for which we store delayed metrics. Allowed values are 5m, 20m, 60m, 240m, 1440m
    DELAYED_METRICS_STORAGE_GRANULARITY("20m"),
    //the granularity upto which we re-roll only the delayed metrics instead of the entire shard
    DELAYED_METRICS_REROLL_GRANULARITY("60m"),
    RECORD_DELAYED_METRICS("true"),

    SHOULD_STORE_UNITS("true"),
    USE_ES_FOR_UNITS("false"),
    // Should at least be equal to the number of the netty worker threads, if http module is getting loaded
    ES_UNIT_THREADS("50"),
    ROLLUP_ON_READ_THREADS("50"),
    TURN_OFF_RR_MPLOT("false"),
    ROLLUP_ON_READ_REPAIR_THREADS("250"),
    ROLLUP_ON_READ_REPAIR_SIZE_PER_THREAD( "5" ),
    ROLLUP_ON_READ_TIMEOUT_IN_SECONDS("10"),

    // 3 days - this matches the TTL for our metrics_full table, we don't accept anything older than the TTL.
    BEFORE_CURRENT_COLLECTIONTIME_MS("259200000"),
    // 10 minutes
    AFTER_CURRENT_COLLECTIONTIME_MS("600000"),

    //Feature to improve token search. Enabling this will persist metric name tokens separately.
    ENABLE_TOKEN_SEARCH_IMPROVEMENTS("false"),

    //Count raw metrics ingested per tenant
    ENABLE_PER_TENANT_METRICS("false"),

    ENABLE_DTX_INGEST_BATCH("false"),

    // Cross-Origin Resource Sharing
    CORS_ENABLED("false"),
    CORS_ALLOWED_ORIGINS("*"),
    CORS_ALLOWED_METHODS("GET, POST"),
    CORS_ALLOWED_HEADERS("X-Auth-Token, Accept"),
    CORS_ALLOWED_MAX_AGE("1728000");

    static {
        Configuration.getInstance().loadDefaults(CoreConfig.values());
    }
    private String defaultValue;
    private CoreConfig(String value) {
        this.defaultValue = value;
    }
    public String getDefaultValue() {
        return defaultValue;
    }
}
