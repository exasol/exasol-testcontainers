# Exasol Java Testcontainer User Guide

The Exasol Java Testcontainer is based on the [testcontainers](https://testcontainers.org) open source project. It is intended for use in automated integration tests of Java software that uses Exasol.

## Notation Used in This Document

Syntax definitions in this document are written in [Augmented Backus-Naur Form (ABNF)](https://tools.ietf.org/html/rfc5234).

## Getting Test Containers Into Your Project

Exasol test containers are built using [Apache Maven](https://maven.apache.org/), so integrating the release packages into your project is easy with Maven.

Please check out ["Introduction to the Dependency Mechanism"](http://maven.apache.org/guides/introduction/introduction-to-dependency-mechanism.html), if you want to learn about how maven handles dependencies and dependency scopes.

We assume here that you are familiar with the basics.

### Exasol Test Containers as Maven Dependency

Just add the following dependency to add the Exasol test containers to your project.

```xml
<dependency>
    <groupId>com.exasol</groupId>
    <artifactId>exasol-testcontainers</artifactId>
    <version><!-- add latest version here --></version>
    <scope>test</scope>
</dependency>
```

As always, check for the latest version of the dependencies.

Note that you only need the `exasol-testcontainers` dependency in the `test` scope. It won't appear in your production release. The same is true for all transitive dependencies of the test containers.

### Exasol JDBC Driver as Maven Dependency

You also need the Exasol JDBC Driver as this is not available on Maven Central, you need to add the Exasol Artifactory as a source:

```xml
<repositories>
    <repository>
        <id>maven.exasol.com</id>
        <url>https://maven.exasol.com/artifactory/exasol-releases</url>
        <snapshots>
            <enabled>false</enabled>
        </snapshots>
    </repository>
</repositories>
```

Then add the JDBC driver dependency:

```xml
<dependency>
    <groupId>com.exasol</groupId>
    <artifactId>exasol-jdbc</artifactId>
    <version>6.2.3</version>
    <scope>runtime</scope>
</dependency>
```

The JDBC driver is best used in scope `runtime`, meaning that it is not needed for building production code or test code. It just needs to be available at runtime. This is typical for a JDBC driver.

### Test Containers and JUnit 5

We recommend using [test containers together with JUnit 5](https://www.testcontainers.org/test_framework_integration/junit_5/). If you want to do that, please also add the following dependency.

```xml
<dependency>
    <groupId>org.testcontainers</groupId>
    <artifactId>junit-jupiter</artifactId>
    <version>1.12.5</version>
    <scope>test</scope>
</dependency>
```

## Creating an Exasol Testcontainer in a JUnit 5 Test

This Example demonstrates how to create a test container from within a JUnit 5 test with minimum effort.

```java
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import com.exasol.containers.ExasolContainer;
import com.exasol.containers.ExasolContainerConstants;


@Testcontainers
class BucketTest {
    private static final Logger LOGGER = LoggerFactory.getLogger(BucketTest.class);

    @Container
    private static final ExasolContainer<? extends ExasolContainer<?>> CONTAINER = new ExasolContainer<>();

    // ...
}
```

Let's go through the code line by line.

The first two imports provide support for annotations that you need in order to tell `testcontainers` to automatically initialize the docker container. You annotate the test class with `@Testcontainers`.

The `@Container` tells the `testcontainers` framework to initialize the static variable `CONTAINER` with control object for the Exasol docker container.

Under the hood the framework takes care of downloading the docker image &mdash; unless it is already locally cached. Then it starts the container and waits for the service inside to be ready. The `ExasolContainer` implements its own waiting strategy to make sure that the services you need in your tests are available once your test code takes over.

Make sure that the complete setup of the `ExasolContainer` happens in that initialization of the annotated private static class variable.

### Static, Instance Variable or Local Variable

When defining a container you have the choice between making the variable static, an instance variable or  local one. The main difference is when and how often the `testcontainer` framework replaces the docker instance if you use the annotation `@Container`.

Put the annotation on a static class variable and the docker instance is created once per test class. Put it on an instance variable to let the framework create a fresh docker instance per test case.

### Starting the Container "by Hand"

Of course you can also simply assign the `ExasolContainer` to a non-annotated variable (e.g. a local variable) and start the container yourself.

```java
try (final ExasolContainer<? extends ExasolContainer<?>> container = new ExasolContainer<>()) {
    container.with...().start();
}
```

In that case, mind the try-with-resources block, to make sure, all dependent resources implementation `AutoClosable` are properly cleaned up afterwards.

Also put all builder methods &mdash; recognizable by the prefix `with` &mdash; _into_ the body of the `try` block, so that static code analyzers can verify the clean up. Otherwise the assignment isn't result of the `new()` but a return parameter from a builder method. And that could potentially be a different object, so that a resource leak would be possible.

### Choosing the Version of Exasol in the Container

Note: Containers using Exasol version 6.2 and below are no longer supported since exasol-testcontainers version 5.1.3.

If you use the constructor without parameters, the test container picks a fixed version for you. More precisely the following two lines are equivalent:

```java
private static final ExasolContainer<? extends ExasolContainer<?>> CONTAINER = new ExasolContainer<>();
```

```java
private static final ExasolContainer<? extends ExasolContainer<?>> CONTAINER = new ExasolContainer<>(
        ExasolContainerConstants.EXASOL_DOCKER_IMAGE_REFERENCE);
```

If you need a different Exasol version for your tests, replace the value of the constructor parameter with the name of the Exasol docker container you need.

You can specify a complete docker image name as in the example below

    exasol/docker-db:7.2.1-d1

Alternatively you can use the Exasol version for convenience:

    7.0.12
    7.0.11
    7.1

If necessary you can even pick a specific Docker image revision (though in most cases only one will exist anyway):

    6.2-d1
    6.1.14-d2

If you omit the Docker image revision, it always defaults to `d1`.

### Using Non-standard Port Numbers

Exasol's `docker-db` has a fixed cluster configuration. The ports for the database service and the BucketFS service are constant. That being said, if you want to experiment with your own Docker images, you might use different port numbers.

As you will learn in section ["Automatic Cluster Configuration Parsing"](#automatic-cluster-configuration-parsing), the testcontainer can configure a lot of settings automatically by reading the cluster configuration.

Unfortunately this in case of exposed ports, this mechanism kicks in too late. Ports must be exposed _before_ the container starts. And at that time the cluster configuration obviously is not available yet.

That means if you use non-standard ports for your services, you must expose the ports manually using the "withExposedPorts(...)" method when you instantiate the container. Keep in mind that doing this overrides the default ports that are exposed by the container, so make sure you also include those in the "withExposedPorts(...)".

## Overriding the Docker Image in a Build

If your want to test your software against multiple versions or variants of Exasol without altering your code, you can do so by setting the `com.exasol.dockerdb.image` Java property.

This property accepts all forms of image reference that the constructor of the ExasolContainer knows. Namely version numbers like `7.0.2`, or complete docker image names like `exasol/docker-db:7.0.2`.

When you set this property, the `ExasolTestcontainer` will start the Exasol docker image that value of the property refers to instead of the one defined in the constructor call.

You can use this in a continuous integration build for example to iterate through a list of Exasol versions that you want to test your software against.

If you explicitly want to prevent overriding, you can use the constructor `ExasolContainer(final String dockerImageName, final boolean allowImageOverride)` while setting `allowImageOverride` to `false`.

## SSH Access and Temporary Credentials

For Exasol version 8 and later `docker exec` to the internals of the container is disabled. Instead, ETC uses SSH with auto-generated temporary credentials.

1. If a directory was explicitly specified with `withTemporaryCredentialsDirectory()` then ETC uses this directory.
2. If directory `target` exists then ETC uses this directory.
3. If directory `build` exists then ETC uses this directory.
4. Else ETC uses directory `target`, checks if the directory exists and creates it if necessary.

For Exasol versions below 8 you can ignore this feature.

## Automatic Cluster Configuration Parsing

Many integration tests rely on knowing the setup of the cluster. The Exasol test container comes with a parser that reads the cluster configuration from the running docker container.

This configuration for example contains the setup of BucketFS services and the buckets they host.

To inspect this configuration use the following method:

```java
final ClusterConfiguration configuration = container.getClusterConfiguration();
```

Check the JavaDoc of [`ClusterConfiguration`](../../src/main/java/com/exasol/config/ClusterConfiguration.java) for details about the configuration structure and contents.

### Generated Passwords

Since as the name suggests, the Exasol test container is intended as a test support module, we need to be able to get the automatically generated random passwords inside the container.

The parser for the `ClusterConfiguration` extracts them.

This is why **you should not use test containers in a production environment**. Obviously integration tests need to simulate user access and for that they need to have the passwords. But in any real world scenario outside of a test this would be a clear security issue.

## Database Access

Of course the most important feature of the Exasol test container is to provide a running database instance and access to it.

### Getting a JDBC `Connection` Object Directly from the Container

The most convenient way to get a database connection is to simply call `createConnection()`.

```java
final Connection connection = container.createConnection("") ;
```
This gives you a connection with the default user that the container is configured to. Unless you change anything this is the `SYS` user.

You could add query parameters which would be attached to the JDBC connection string, but for Exasol you really don't need that.

If you want to create a connection for a different user &mdash; e.g. when testing restricted access to data &mdash; you can specify user and password and create a connection for a different user.

```java
final Connection connection = container.createConnectionForUser("johndoe", "z6K+Px38a@Lm71");
```

In case you prefer a more manual approach, you can also use `getJdbcUrl()` and create the connection yourself.

### Getting the Address Part of an EXA Connection

"EXA Connections" are a special form of [connection](https://docs.exasol.com/sql/create_connection.htm) that you can use between Exasol database clients and the database. A popular example is loading from one Exasol cluster into another using the [`IMPORT`](https://docs.exasol.com/sql/import.htm) command.

The simplest possible EXA connection consist of a hostname followed by a colon and a port number. Instead of the hostname you can also use an IP or IP range.

The method `getExaConnectionAddress()` gives you the address part that you need to create such a connection to the Exasol instance running inside the test container from the host.

## Working with Buckets

The Exasol test container provides access to buckets in BucketFS. This is useful if your tests need to work with files in buckets. If you for example want to test a UDF script, you can upload it prior to the test using a `Bucket` control object.

### Understanding Bucket Contents

One thing you need to know about buckets objects inside a bucket is that they are not stored in a hierarchical directory structure. Instead they are flat files that can have slashes in their names so that it looks like a directory structure at first glance.

Since this internal detail deviates from what users are used to see, the bucket access methods implemented in this project simulate a regular structure.

Another thing worth noting is that if you store files in some selected archive formats (e.g. [TAR archives](https://www.gnu.org/software/tar/)) in a bucket, BucketFS presents the archive contents as a hierarchical structure.

### Specifying "Paths" Inside a Bucket

Some bucket actions require a path inside the bucket as parameter. While those paths are always relative to the root of the bucket, the Exasol test container lets you treat them as absolute paths too.

That means that the following two paths are both relative to the bucket root:

```
EXAClusterOS/
/EXAClusterOS/
```

### Getting a Bucket Control Object

You can get access to a bucket in BucketFS by requesting a `Bucket` control object from the container.

```java
final Bucket bucket = this.container.getBucket("mybucketfs", "mybucket");
```

If you just need access to the default bucket (i.e. the bucket which in a standard setup of Exasol always exists), use the following convenience method.

```java
final Bucket bucket = this.container.getDefaultBucket();
```

### Listing Bucket Contents

The following code lists the contents of a buckets root.

```java
final List<String> bucketContents = bucket.listContents();
```

You can also list the contents of a "path" within a bucket. "Path" is set in quotes here since objects in buckets are &mdash; as mentioned earlier &mdash; all files directly in the root of the bucket.

### Uploading a File to BucketFS

Especially when testing UDF scripts, this comes in handy. You can upload files from a local filesystem into a bucket as follows.

```java
bucket.uploadFile(source, destination);
```

Where `source` is an object of type `Path` that points to a local file system and `destination` is a string defining the path relative to the bucket's root to where the file should be uploaded.

### Uploading Files Into a "Directory"

As mentioned in section ["Specifying Paths Inside a Bucket"](#specifying-paths-inside-a-bucket) BucketFS only simulates a path structure. For your convenience the file upload lets you choose a "directory" in the bucket to which you want to upload.

If you chose this variant, the original filename from the local path is appended to the path inside the bucket.

As an example let's assume you want to upload a jar file from a local directory like this:

```java
bucket.uploadFile("repo/virtual-schemas/3.0.1/virtual-schemas-3.0.1.jar", "jars/");
```

In this case the `Bucket` treats the destination path in the bucket as if you wrote `jars/virtual-schemas-3.0.1.jar`.

### Uploading Text as a File

It's a common use-case test scenarios to create small files of well-defined content and upload them to BucketFS. Most of the time those are configuration files.

Use the following convenience method to write a string directly to a file in a bucket.

```java
bucket.uploadStringContent(content, destination);
```

Here `content` is the `String` that you want to write an destination is again the path inside the bucket.

### Blocking vs. Non-blocking Upload

In integration tests you usually want reproducible test cases. This is why the standard implementation of `uploadFile(...)` blocks the call until the underlying object is synchronized in the bucket.

In rare cases you might want more control over that process, for example if you plan bulk-upload of a large number of small files and want to shift the check to the end of that operation.

For those special occasions there is an overloaded method `uploadFile(source, destination, blocking-flag)` where you can choose to upload in non-blocking fashion.

The same style of overloaded function exists for text content upload too in the method `upload(content, destination, blocking-flag)`.

Unless you really need it and know exactly what you are doing, we recommend to stick to blocking operation for your tests.

### Downloading a File from BucketFS

Downloading a file is straight forward:

```java
bucket.downloadFile(source, destination);
```

Here the source is a path inside the bucket and destination is a path on a local file system.

### Automatic Authentication at a BucketFS Service

If you are wondering how to provide the access passwords for the buckets &mdash; you don't have to. Part of this test containers convenience is hiding the details of bucket authentication in integration tests.

After all you want to test whether the software that you want to using in combination with Exasol works and not Exasol's authentication mechanisms.

The test container knows the cluster setup, including the [bucket credentials](#generated-passwords). The test container handles the authentication for you when working with buckets.

### Managing Buckets and Services

Creating and deleting of buckets and BucketFS services is not yet supported by the Exasol test container.

## Installing Custom Script Language Containers (SLC)

Exasol User Defined Functions (UDF) run inside a Script Language Container. The Exasol DB contains SLC that is used by default. If you need to test UDFs against a custom SLC (e.g. containing a newer Java version or additional libraries), you can install a custom SLC.

```java
// Create Script Language Container definition
ScriptLanguageContainer slc = ScriptLanguageContainer.builder()
    .language(Language.JAVA)
    .localFile(Path.of("/path/to/java17-slc.tar.gz"))
    .build();
// Pass definition to ExasolContainer
ExasolContainer<? extends ExasolContainer<?>> container = new ExasolContainer<>().withReuse(true).withScriptLanguageContainer(slc);
```

You can also specify a released SLC from [script-languages-release](https://github.com/exasol/script-languages-release/releases):

```java
ScriptLanguageContainer slc = ScriptLanguageContainer.builder()
    .language(Language.PYTHON)
    .slcRelease("7.1.0", "template-Exasol-all-python-3.10_release.tar.gz")
    .sha512sum("db19e16b9cb5b3d02c44ad6e401eb9296b0483c0078a2e23ac00ad7f26473d115febb0c799a86aed33f49252947a86aa7e8927571a2679131da52e5fc090939c")
    .build();
```

You can also specify the URL directly:

```java
ScriptLanguageContainer slc = ScriptLanguageContainer.builder()
    .language(Language.PYTHON)
    .url("https://github.com/exasol/script-languages-release/releases/download/7.1.0/template-Exasol-all-python-3.10_release.tar.gz")
    .sha512sum("db19e16b9cb5b3d02c44ad6e401eb9296b0483c0078a2e23ac00ad7f26473d115febb0c799a86aed33f49252947a86aa7e8927571a2679131da52e5fc090939c")
    .build();
```

Please note:
* The SHA512 checksum is required to ensure the validity of downloaded files.
* When reusing a Docker Container, ETC installs the SLC only once.
* By default, ETC uses SLC aliases `JAVA`, `R` and `PYTHON3` depending on the chosen language. This will overwrite the default SLC for the language. If necessary you can specify a custom alias, e.g. `.alias("JAVA17")`.
* See the [Exasol DB documentation](https://docs.exasol.com/db/latest/database_concepts/udf_scripts/adding_new_packages_script_languages.htm) for details about installing SLCs.

## Viewing Cluster Logs

A running Exasol cluster produces a lot of logs. In the Docker version those logs are located internally under `/exa/logs`. Since this happens inside the Docker container, logs by default are not visible in an integration test.

### Mapping Logs to a Directory on the Host

You can map the logs to a directory on the host &mdash; typically a temporary directory in order to be able to read them during the test.

To achieve this, call the method `withClusterLogsPath(final Path clusterLogsHostPath)` when initializing the Exasol test container.

Example:

```java
private static final Path TEMP_DIR = createTempDir();

private static Path createTempDir() {
    final File tempDir = Files.createTempDir();
    tempDir.deleteOnExit();
    return tempDir.toPath();
}

@Container
private static final ExasolContainer<? extends ExasolContainer<?>> container
        = new ExasolContainer<>().withClusterLogsPath(TEMP_DIR);
```

#### Don't mix Annotations That Depend on Each Other

Note that you should not use the [`@TempDir` annotation of JUnit](https://junit.org/junit5/docs/current/api/org.junit.jupiter.api/org/junit/jupiter/api/io/TempDir.html) in this particular situation because the _initialization order of annotated fields is not guaranteed_.

If you combine two or more annotations and the initialization of one of them depends on the other one being initialized already you can end up with a `NullPointerException`.

In this concrete example the container got initialized before the temporary directory during experiments with `@TempDir`.

We therefore recommend the more inconvenient but safer way shown in the example code above.

### Producing Support Information Archives

Exasol ships with a utility called `exasupport`. This programs creates an archive that contains all the important information for debugging issues in the database in a standardized format:

* System information
* Logs
* Configuration
* Core-dumps (if any)

You can tell the ETC to create such a package, given that you are working with Exasol 7 or later.

One way is to request that via the API:

```java
.withSupportInformationRecordedAtExit(<target-directory-on-the-host>, <exit-type>)
```

This configures the ETC to map a host directory so that support archives are created there.

On log level `INFO` you will see a message like the one below, taken from a JUnit test where the feature was enabled:

```
2021-07-23 09:26:22.514 [INFO   ] Container exiting with EXIT_SUCCESS. Monitoring is set to EXIT_ANY.
Wrote support archive to: /tmp/junit16072898015769568356/exacluster_debuginfo_2021_07_23-09_26_16.tar.gz
```

As you can see, `exasupport` created an archive `exacluster_debuginfo_<date>-<time>.tar.gz` that then appears in the mapped host directory. Since the monitored exit type included the actual exit type, ETC triggered `exasupport`.

You can pick the exit type to be monitored from the following list:

* `EXIT_ANY`
* `EXIT_SUCCESS`
* `EXIT_ERROR`
* `EXIT_NONE` (default)

If you run the ETC in a CI build, it is more convenient to configure this feature via system properties:

* `com.exasol.containers.support_information_target_dir`
* `com.exasol.containers.monitored_exit`

The effect is the same, but you get better decoupling from your code and need to configure this in one place only instead of many tests.

## Networking

### Running the Exasol Test Container in a Docker Network

For integration tests you sometimes need to connect services via a network. In case of test containers a convenient way is to use the docker network feature.

Add the following to your container definition.

```java
try (final Network network = Network.newNetwork();
     final ExasolContainer<? extends ExasolContainer<?>> sourceContainer =
             new ExasolContainer<>()
             // ...
            .withNetwork(network);
) {
    // ...
}
```

#### Use Internal IP Addresses Instead of Network Aliases

Docker network aliases work like hostnames inside a Docker network. Usually they can be used just like hostnames and are then resolved to IP addresses by standard DNS tools.

Unfortunately in case of Exasol, this does _not_ work, because the proprietary DNS implementation of Exasol does not support this. Use IP addresses if you want to access a service running in a different container from an Exasol container instead.

Check the method `getDockerNetworkInternalIpAddress()` in the `ExasolContainer` class to learn how to determine the Docker-network-internal IP address of a container.

If your are looking for an example, please check the integration test `ExaLoaderBetweenTwoContainersIT`.

### Secure Connections with TLS

Exasol uses TLS certificates to encrypt JDBC connection, bucket access and the RPC API. In a normal installation you can [configure a custom certificate](https://github.com/exasol/exasol-java-tutorial/blob/main/tls-tutorial/doc/tls_with_exasol.md). The docker container automatically generates a new self-signed certificate at startup.

#### Securing JDBC Connections

The Exasol JDBC driver verifies the server's certificate by default or when option `validateservercertificate=1` is specified. If the server's hostname does not match the hostname specified in the certificate, the connection will fail. To avoid this, you can specify a fingerprint of the certificate in the JDBC URL.

When startup is finished, ETC will automatically add this fingerprint the the JDBC URL returned by `ExasolContainer.getJdbcUrl()` and enable certificate validation. This is also true for all connections you create e.g. using `ExasolContainer.createConnection()`.

#### Securing HTTPS Connections

In order to connect via HTTPS (e.g. BucketFS or the RPC API) either can disable the certificate check completely using a custom `X509TrustManager` or add the container's self-signed certificate to the trust store. You can retrieve the certificate as a `java.security.cert.X509Certificate` with method `ExasolContainer.getTlsCertificate()` and then use it to create a `java.net.http.HttpClient`:

```java
X509Certificate certificate = CONTAINER.getTlsCertificate();

KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
keyStore.load(null);
keyStore.setCertificateEntry("caCert", certificate);

TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
trustManagerFactory.init(keyStore);

SSLContext sslContext = SSLContext.getInstance("TLS");
sslContext.init(null, trustManagerFactory.getTrustManagers(), null);

HttpClient httpClient = HttpClient.newBuilder().sslContext(sslContext).build();
```

In case the hostname specified in the certificate does not match the docker container's hostname, you may need to disable hostname verification by adding system property `jdk.internal.httpclient.disableHostnameVerification` with value `true`. We recommend starting the Java VM with argument `-Djdk.internal.httpclient.disableHostnameVerification=true` to avoid timing issues.

**Note:** This disables hostname verification globally. You should not use this in a production environment.

To use the certificate with `javax.net.ssl.HttpsURLConnection`, you need to call [setSSLSocketFactory()](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/javax/net/ssl/HttpsURLConnection.html#setSSLSocketFactory(javax.net.ssl.SSLSocketFactory)) and [setHostnameVerifier()](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/javax/net/ssl/HttpsURLConnection.html#setHostnameVerifier(javax.net.ssl.HostnameVerifier)).

## Installing Drivers for External Data Sources and Sinks

You can install JDBC drivers that you can use in the ExaLoader (for `IMPORT` statements) and User Defined Functions (UDFs) like this:

```java
final ExasolDriverManager driverManager = EXASOL.getDriverManager();
final DatabaseDriver driver = JdbcDriver.builder("EXAMPLE_DRIVER")
        .prefix("jdbc:examle:")
        .sourceFile(driverFile)
        .mainClass("org.example.Driver").build();
driverManager.install(driver);
```

The builder parameter gets the name under which the driver is registered in Exasol. It needs to start with a letter, optionally followed by only letters, numbers or underscores.

You also need to provide the prefix for the JDBC URL, and the main class of the driver that serves as entry point.

The source file is optional. If you provide one, the ETC will install the driver for you. If not the file needs to be present in BucketFS before you use it.

## EXAoperation Emulation

The Exasol variant in the `docker-db` which is the basis of the Exasol test container, does not feature EXAoperation. That being said, there are situations where integration test requires a subset of EXAoperations functions.

For this particular purpose the test container implementation comes with an emulation of selected EXAoperation features.

### EXAoparation Plug-ins

Plug-ins are one way to extend Exasol with custom functionality. Since the test containers only contain an emulation of EXAoperation, sticking to the plug-in naming and structure conventions is important.

The emulation expects plug-ins to have the following structure.

    Plugin.<plug-in-name>-<version>-<date>.pkg      (TAR archive)
     |
     '- Plugin.<plug-in-name>-<version>.tar.gz      (TAR archive)
         |
         '- usr
             |
             '- opt
                 |
                 '- EXAplugins
                     |
                     '- <plug-in-name>-<version>
                         |
                         '-- exaoperation-gate
                              |
                              '- plugin-functions

Versions have the following format:

    version = 1*DIGIT *("." 1*DIGIT)

For example: `3.2.0`

The date must be in the following format:

    date = year "-" month "-" day

    year = 4DIGIT

    month = 2DIGIT

    day = 2DIGIT

Example: `2020-01-31`

The file `plugin-functions` must be executable.

Note that package signatures are ignored for now.

### Installing and Uninstalling EXAoperation Plug-ins

You can install plug-ins on an Exasol cluster via the EXAoperation emulator. The emulation takes the path of such a plug-in and lets you install it.

```java
final ExaOperation exaOperation = container.getExaOperation();
final Plugin plugin = exaOperation.installPluginPackage(
    Path.of( "test/resources/Plugin.FooBar-1.0.0-2020-02-02.pkg" )
);
```

After this step, the plug-in package is installed in the file system of the Exasol cluster.

`Plugin` offers a few convenience functions that wrap common function calls. Please note that support for these functions depends on the plugin in question; refer to the appropriate user manual when in doubt.

Every plugin should be able to provide a usage description through the `listFunctions` call:
```java
final List<String> pluginFunctions = plugin.listFunctions();
```


### Listing Installed Plug-Ins

To get a list of installed plugins, use the following call:

```java
final List<String> pluginNames = exaOperation.getPluginNames();
```


### Getting a Previously Installed Plug-in

If you already installed a plug-in and need access to its control object, use the following method:

```java
final Plugin plugin = exaOperation.getPlugin("Foo.Bar-1.0.0");
```

### Plug-in Scripts

Plug-ins contain one or more scripts. The only mandatory one is called `plugin-functions`. For other functions, naming conventions (e.g. `start` and `stop`) are established but not enforced.

Call the method `functions()` to execute the `plugin-functions` script.

You can call any script using the following method:

```java
plugin.runScript("my-script-name");
```

### Methods for Functions Following Established Naming Conventions

The `PlugIn` class contains convenience methods for calling some of the most popular plug-in scripts:

* `start()`
* `stop()`
* `restart()`
* `status()`

## Tweaking

### Limiting Service Dependencies

Not every integration test you implement uses BucketFS or User Defined Functions (UDFs). If you know that you only need a subset of the Services that the Exasol Docker DB provides, you can specify them explicitly when creating the container.
This way the test container code is able to skip some parts so that your test can start faster. Overall that can speed up your integration tests considerably.

Use the following function to explicitly specify which services you depend on.

```java
ExasolContainer.withRequiredServices(final ExasolService... services)
```

You can choose from the following optional services defined in the enumeration `ExasolService`:

* `BUCKETFS`
* `UDF`

The following snippet tells the test containers that you need BucketFS but you are not using UDFs.

```java
container.withRequiredServices(ExasolService.BUCKETFS)
```

If services depend on each other, the test container implementation automatically adds missing services. By default all services are selected.

Handle this option with care. If you explicitly list services you depend on and forget one, this will lead to unexpected behavior in your tests.

### Improving Random Data Acquisition

If key generation steps inside the Docker container are slow, installing the `rng-tools` on the host can improve the situation.

```bash
sudo apt install rng-tools
```
## Configuration

Exasol testcontainers uses the configuration of Testcontainers in file `~/.testcontainers.properties`. See the [Testcontainers documentation](https://java.testcontainers.org/features/configuration/) for details.

### Reusing Container for Speedup

Exasol testcontainers can reuse the running container to avoid the startup delay.
For that you need to append `withReuse(true)` at container creation.

```java
new ExasolContainer<>()
// ...
.withReuse(true);
```

In addition you have to add `testcontainers.reuse.enable=true` to `~/.testcontainers.properties` on your machine. This only enables the reuse for the local development but not for continuous integration (CI).

Then Exasol testcontainers will keep your container running after the tests and reuse across the tests.
Since testcontainers will not terminate the container any more you have to manually terminate it.
You can find the container id using `docker ps` command and terminate it by running `docker rm -f <CONTAINER-ID>`.

Please note that the reuse capability works reliably with Exasol version 7 and above. We disabled that option for older versions.

### Use Docker Running in a Virtual Machine

If your Docker server is running in a virtual machine, you can configure exasol-testcontainer to use it by adding the following line to  `~/.testcontainers.properties`, no need to configure environment variables.

```properties
docker.host=tcp://192.168.56.6:2375
```

Adapt IP address and port to your setup. See the Testcontainers documentation for details on [Docker host detection](https://java.testcontainers.org/features/configuration/#customizing-docker-host-detection).

## Troubleshooting

### Warning: Failure when attempting to lookup auth config

The test containers produce this error message when there is no Docker configuration file in your home directory. In case you don't need one the simplest fix is to create an empty JSON:

```bash
mkdir "$HOME"/.docker
echo '{}' > "$HOME"/.docker/config.json
```
