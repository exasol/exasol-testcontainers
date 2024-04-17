package com.exasol.testutil;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

import java.io.*;

/**
 * This class provides a method to verify that a class is serializable.
 */
public class SerializableVerifier {

    private SerializableVerifier() {
        // not instantiable
    }

    /**
     * Verify that the given object is serializable.
     *
     * @param type   type of the object
     * @param object object to verify
     * @param <T>    type of the object
     */
    public static <T> void assertSerializable(final Class<T> type, final T object) {
        final byte[] serialized = serialize(object);
        final T deserialized = deserialize(serialized, type);
        assertThat(deserialized, equalTo(object));
    }

    private static <T> byte[] serialize(final T object) {
        final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        try (ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream)) {
            objectOutputStream.writeObject(object);
        } catch (final IOException exception) {
            throw new AssertionError("Failed to serialize object: " + exception.getMessage(), exception);
        }
        return byteArrayOutputStream.toByteArray();
    }

    private static <T> T deserialize(final byte[] serializedObject, final Class<T> clazz) {
        try (ObjectInputStream objectInputStream = new ObjectInputStream(new ByteArrayInputStream(serializedObject))) {
            return clazz.cast(objectInputStream.readObject());
        } catch (final IOException | ClassNotFoundException exception) {
            throw new AssertionError("Failed to deserialize object: " + exception.getMessage(), exception);
        }
    }
}
