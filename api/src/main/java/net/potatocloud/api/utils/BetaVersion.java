package net.potatocloud.api.utils;

import lombok.Getter;

@Getter
public class BetaVersion extends Version {

    private final int beta;

    private BetaVersion(int major, int minor, int patch, int beta) {
        super(major, minor, patch);
        this.beta = beta;
    }

    public static BetaVersion of(int major, int minor, int patch, int beta) {
        return new BetaVersion(major, minor, patch, beta);
    }

    @Override
    public int compareTo(Version other) {
        final int base = super.compareTo(other);
        if (base != 0) {
            return base;
        }

        if (other instanceof BetaVersion otherBeta) {
            return Integer.compare(this.beta, otherBeta.beta);
        }

        return 0;
    }

    @Override
    public String toString() {
        return super.toString() + "-beta." + beta;
    }
}
