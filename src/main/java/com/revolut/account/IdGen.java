package com.revolut.account;

import java.nio.ByteBuffer;
import java.util.Base64;
import java.util.UUID;

/**
 * Generator of short, globally unique identifiers based on UUID.
 */
public class IdGen {

    private IdGen() {
        // disable creation
    }

    public static String newID() {
        return toShortString(UUID.randomUUID());
    }

    private static String toShortString(UUID uuid) {
        ByteBuffer byteBuffer = ByteBuffer.allocate(16);
        byteBuffer.putLong(uuid.getMostSignificantBits());
        byteBuffer.putLong(uuid.getLeastSignificantBits());

        return Base64.getEncoder().withoutPadding()
                .encodeToString(byteBuffer.array())
                .replaceAll("/", "_")
                .replaceAll("\\+", "-");
    }
}
