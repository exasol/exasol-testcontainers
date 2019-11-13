package com.exasol.config;

import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;

import com.exasol.config.BucketFsServiceConfiguration.Builder;

/**
 * This class provides an abstraction of the {@code EXAConf} cluster configuration file.
 */
public class ClusterConfiguration {
    private static final String KEY_SEPARATOR = ":";
    private static final String BUCKET_SERVICE_KEY_PREFIX = "BucketFS" + KEY_SEPARATOR;
    private static final String BUCKET_KEY_PREFIX = "Bucket" + KEY_SEPARATOR;
    private static final String SECTION_SEPARATOR = "/";
    private static final String DEFAULT_BUCKET_SECTION = "BucketFS:bfsdefault/Bucket:default";
    private final Map<String, String> parameters;

    /**
     * Create a new instance of an Exasol {@link ClusterConfiguration}.
     *
     * @param parameters configuration options as map of keys and values
     */
    public ClusterConfiguration(final Map<String, String> parameters) {
        this.parameters = parameters;
    }

    /**
     * Get the read password for the default Bucket ({@code bfsdefault/default}).
     *
     * @return read password
     */
    public String getDefaultBucketReadPassword() {
        return this.parameters.get(DEFAULT_BUCKET_SECTION + "/ReadPasswd");
    }

    /**
     * Get the write password for the default Bucket ({@code bfsdefault/default}).
     *
     * @return write password
     */
    public String getDefaultBucketWritePassword() {
        return this.parameters.get(DEFAULT_BUCKET_SECTION + "/WritePasswd");
    }

    /**
     * Get the configuration of a BucketFS service.
     *
     * @param name name of the BuckteFS service
     * @return service configuration
     */
    public BucketFsServiceConfiguration getBucketFsServiceConfiguration(final String name) {
        final String serviceKey = BUCKET_SERVICE_KEY_PREFIX + name;
        final Builder builder = BucketFsServiceConfiguration //
                .builder() //
                .name(name) //
                .httpPort(Integer.parseInt(getOrDefault(serviceKey, "HttpPort", "0"))) //
                .httpsPort(Integer.parseInt(getOrDefault(serviceKey, "HttpsPort", "0")));
        final Set<String> bucketNames = getBucketNames(serviceKey);
        for (final String bucketName : bucketNames) {
            addBucketConfiguration(serviceKey, bucketName, builder);
        }
        return builder.build();
    }

    private void addBucketConfiguration(final String serviceKey, final String bucketName, final Builder builder) {
        final String bucketKey = BUCKET_KEY_PREFIX + bucketName;
        builder.addBucketConfiguration(BucketConfiguration //
                .builder() //
                .name(bucketName) //
                .readPassword(get(serviceKey, bucketKey, "ReadPasswd")) //
                .writePassword(get(serviceKey, bucketKey, "WritePasswd")) //
                .publiclyReadable("true".equalsIgnoreCase(getOrDefault(serviceKey, bucketKey, "Public", "false")))
                .build());
    }

    private Set<String> getBucketNames(final String serviceKey) {
        return this.parameters.entrySet() //
                .stream() //
                .map(Entry::getKey) //
                .filter(key -> key.startsWith(serviceKey + SECTION_SEPARATOR + BUCKET_KEY_PREFIX)) //
                .map(key -> key.substring(key.lastIndexOf(KEY_SEPARATOR) + 1).replaceAll("/.*", "")) //
                .collect(Collectors.toSet());
    }

    private String getOrDefault(final String sectionKey, final String paramterKey, final String defaultValue) {
        return this.parameters.getOrDefault(sectionKey + SECTION_SEPARATOR + paramterKey, defaultValue);
    }

    private String get(final String sectionKey, final String subSectionKey, final String paramterKey) {
        return this.parameters.get(sectionKey + SECTION_SEPARATOR + subSectionKey + SECTION_SEPARATOR + paramterKey);
    }

    private String getOrDefault(final String sectionKey, final String subSectionKey, final String paramterKey,
            final String defaultValue) {
        return this.parameters.getOrDefault(
                sectionKey + SECTION_SEPARATOR + subSectionKey + SECTION_SEPARATOR + paramterKey, defaultValue);
    }
}
