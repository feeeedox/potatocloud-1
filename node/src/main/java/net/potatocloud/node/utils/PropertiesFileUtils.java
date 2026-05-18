package net.potatocloud.node.utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.util.Properties;

public final class PropertiesFileUtils {

    private PropertiesFileUtils() {
    }

    public static Properties loadProperties(Path path) {
        final Properties properties = new Properties();

        try (InputStream in = Files.newInputStream(path)) {
            properties.load(in);
            return properties;
        } catch (NoSuchFileException e) {
            return null;
        } catch (IOException e) {
            throw new RuntimeException("Failed to load properties from file: " + path, e);
        }
    }

    public static void saveProperties(Properties properties, Path filePath) {
        try (OutputStream out = Files.newOutputStream(filePath)) {
            properties.store(out, null);
        } catch (IOException e) {
            throw new RuntimeException("Failed to save properties to file: " + filePath, e);
        }
    }
}
