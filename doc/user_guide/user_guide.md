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
final List<String> bucketContents = bucket.listContents("/");
```

### Uploading a File to BucketFS

Especially when testing UDF scripts, this comes in handy. You can upload files from a local filesystem into a bucket as follows.

```java
bucket.uploadFile(source, destination);
```

Where `source` is an object of type `Path` that points to a local file system and `destination` is a string defining the path relative to the bucket's root to where the file should be uploaded.

### Uploading Test as a File

It's a common use-case test scenarios to create small files of well-defined content and upload them to BucketFS. Most of the time those are configuration files.

Use the following convenience method to write a string directly to a file in a bucket.

```java
bucket.uploadStringContent(content, destination); 
```

Here `content` is the `String` that you want to write an destination is again the path inside the bucket.

### Automatic Authentication at a BucketFS Service

If you are wondering how to provide the access passwords for the buckets &mdash; you don't have to. Part of this test containers convenience is hiding the details of bucket authentication in integration tests.

After all you want to test whether the software that you want to using in combination with Exasol works and not Exasol's authentication mechanisms.

The test container knows the cluster setup, including the [bucket credentials](#generated-passwords). The test container handles the authentication for you when working with buckets.

## Tweaking

### Improving Random Data Acquisition

If key generation steps inside the Docker container are slow, installing the `rng-tools` on the host can improve the situation.

```bash
sudo apt install rng-tools
```