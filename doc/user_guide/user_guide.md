# Exasol Java Testcontainer User Guide

The Exasol Java Testcontainer is based on the [testcontainers](https://testcontainers.org) open source project. It is intended for use in automated integration tests of Java software that uses Exasol.

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
    private static ExasolContainer<? extends ExasolContainer<?>> container = new ExasolContainer<>(
            ExasolContainerConstants.EXASOL_DOCKER_IMAGE_REFERENCE);

    // ...
}
```

Let's go through the code line by line.

The first two imports provide support for annotations that you need in order to tell `testcontainers` to automatically initialize the docker container. You annotate the test class with `@Testcontainers`.

The `@Container` tells the `testcontainers` framework to initialize the static variable `container` with control object for the Exasol docker container.

Under the hood the framework takes care of downloading the docker image &mdash; unless it is already locally cached. Then it starts the container and waits for the service inside to be ready. The `ExasolContainer` implements its own waiting strategy to make sure that the services you need in your tests are available once your test code takes over.

Make sure that the complete setup of the `container` happens in that initialization of the annotated private static class variable.

## Automatic Cluster Configuration Parsing

Many integration tests rely on knowing the setup of the cluster. The Exasol test container comes with a parser that reads the cluster configuration from the running docker container.

This configuration for example contains the setup of BucketFS services and the buckets they host.

To inspect this configuration use the following method:

```java
final ClusterConfiguration configuration = this.container.getClusterConfiguration();
```

Check the JavaDoc of [`ClusterConfiguration`](../../src/main/java/com/exasol/config/ClusterConfiguration.java) for details about the configuration structure and contents.

### Generated Passwords

Since as the name suggests, the Exasol test container is intended as a test support module, we need to be able to get the automatically generated random passwords inside the container.

The parser for the `ClusterConfiguration` extracts them.

This is why **you should not use test containers in a production environment**. Obviously integration tests need to simulate user access and for that they need to have the passwords. But in any real world scenario outside of a test this would be a clear security issue.

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

### Automatic Authentication at a BucketFS Service

If you are wondering how to provide the access passwords for the buckets &mdash; you don't have to. Part of this test containers convenience is hiding the details of bucket authentication in integration tests.

After all you want to test whether the software that you want to using in combination with Exasol works and not Exasol's authentication mechanisms.

The test container knows the cluster setup, including the [bucket credentials](#generated-passwords). The test container handles the authentication for you when working with buckets.

### Managing Buckets and Services

Creating and deleting of buckets and BucketFS services is not yet supported by the Exasol test container.

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
        = new ExasolContainer<>(ExasolContainerConstants.EXASOL_DOCKER_IMAGE_REFERENCE)
                .withClusterLogsPath(TEMP_DIR);
```

#### Don't mix Annotations That Depend on Each Other

Note that you should not use the [`@TempDir` annotation of JUnit](https://junit.org/junit5/docs/current/api/org/junit/jupiter/api/io/TempDir.html) in this particular situation because the _initialization order of annotated fields is not guaranteed_.

If you combine two or more annotations and the initialization of one of them depends on the other one being initialized already you can end up with a `NullPointerException`.

In this concrete example the container got initialized before the temporary directory during experiments with `@TempDir`.

We therefore recommend the more inconvenient but safer way shown in the example code above. 

## Tweaking

### Improving Random Data Acquisition

If key generation steps inside the Docker container are slow, installing the `rng-tools` on the host can improve the situation.

```bash
sudo apt install rng-tools
```