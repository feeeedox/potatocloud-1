package net.potatocloud.node.utils;

import lombok.SneakyThrows;
import org.apache.commons.codec.digest.DigestUtils;

import java.io.File;
import java.io.FileInputStream;

public final class HashUtils {

    private HashUtils() {
    }

    @SneakyThrows
    public static String sha256(File file) {
        try (FileInputStream stream = new FileInputStream(file)) {
            return DigestUtils.sha256Hex(stream);
        }
    }
}
