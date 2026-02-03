package net.potatocloud.api.utils;

import lombok.Getter;

import java.util.Objects;

@Getter
public class Version implements Comparable<Version> {

    private final int major;
    private final int minor;
    private final int patch;

    protected Version(int major, int minor, int patch) {
        this.major = major;
        this.minor = minor;
        this.patch = patch;
    }

    public static Version of(int major, int minor, int patch) {
        return new Version(major, minor, patch);
    }

    public static Version fromString(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }

        final String[] parts = value.split("\\.");
        return new Version(
                Integer.parseInt(parts[0]),
                Integer.parseInt(parts[1]),
                Integer.parseInt(parts[2])
        );
    }

    @Override
    public int compareTo(Version other) {
        if (major != other.major) {
            return Integer.compare(major, other.major);
        }
        if (minor != other.minor) {
            return Integer.compare(minor, other.minor);
        }
        if (patch != other.patch) {
            return Integer.compare(patch, other.patch);
        }

        if (this instanceof BetaVersion && !(other instanceof BetaVersion)) {
            return -1;
        }
        if (!(this instanceof BetaVersion) && other instanceof BetaVersion) {
            return 1;
        }

        return 0;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Version other)) {
            return false;
        }
        return major == other.major && minor == other.minor && patch == other.patch;
    }

    @Override
    public int hashCode() {
        return Objects.hash(major, minor, patch);
    }

    @Override
    public String toString() {
        return major + "." + minor + "." + patch;
    }
}
