package net.potatocloud.node.utils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;

public final class HashUtils {

    private static final HexFormat HEX = HexFormat.of();

    private HashUtils() {
    }

    public static String sha256(Path path) {
        return HEX.formatHex(digest("SHA-256", path));
    }

    public static String md5(Path path) {
        return HEX.formatHex(digest("MD5", path));
    }

    private static byte[] digest(String algorithm, Path path) {
        try {
            final MessageDigest messageDigest = MessageDigest.getInstance(algorithm);
            messageDigest.update(Files.readAllBytes(path));
            return messageDigest.digest();
        } catch (IOException | NoSuchAlgorithmException e) {
            throw new RuntimeException("Failed to hash file: " + path, e);
        }
    }
}
