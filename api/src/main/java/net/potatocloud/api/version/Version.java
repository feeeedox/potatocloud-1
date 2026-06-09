package net.potatocloud.api.version;

import java.util.Objects;

public class Version implements Comparable<Version> {

    private final int major;
    private final int minor;
    private final int patch;
    private final String tag;

    protected Version(int major, int minor, int patch, String tag) {
        this.major = major;
        this.minor = minor;
        this.patch = patch;
        this.tag = tag;
    }

    public static Version of(int major, int minor, int patch) {
        return new Version(major, minor, patch, null);
    }

    public static Version fromString(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }

        String version = value.trim();

        if (version.startsWith("v") || version.startsWith("V")) {
            version = version.substring(1);
        }

        String tag = null;

        final String[] split = version.split("-", 2);

        version = split[0];

        if (split.length == 2) {
            tag = split[1];
        }

        final String[] parts = version.split("\\.");

        if (parts.length != 3) {
            return null;
        }

        try {
            return new Version(
                    Integer.parseInt(parts[0]),
                    Integer.parseInt(parts[1]),
                    Integer.parseInt(parts[2]),
                    tag
            );
        } catch (NumberFormatException e) {
            return null;
        }
    }

    public int major() {
        return major;
    }

    public int minor() {
        return minor;
    }

    public int patch() {
        return patch;
    }

    public String tag() {
        return tag;
    }

    @Override
    public int compareTo(Version other) {
        if (major != other.major) {
            return Integer.compare(major, other.major);
        }
        if (minor != other.minor) {
            return Integer.compare(minor, other.minor);
        }
        return Integer.compare(patch, other.patch);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Version other)) {
            return false;
        }
        return major == other.major && minor == other.minor && patch == other.patch && Objects.equals(tag, other.tag);
    }

    @Override
    public int hashCode() {
        return Objects.hash(major, minor, patch, tag);
    }

    @Override
    public String toString() {
        return tag == null ? major + "." + minor + "." + patch : major + "." + minor + "." + patch + "-" + tag;
    }
}
