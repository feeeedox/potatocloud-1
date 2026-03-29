package net.potatocloud.node.utils;

import org.apache.commons.codec.digest.DigestUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public final class HashUtils {

    private HashUtils() {
    }

    public static String sha256(File file) {
        try (FileInputStream stream = new FileInputStream(file)) {
            return DigestUtils.sha256Hex(stream);
        } catch (IOException e) {
            throw new RuntimeException("Failed to get SHA-256 for file: " + file, e);
        }
    }
}
