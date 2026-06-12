package net.potatocloud.api.platform;

import net.potatocloud.api.platform.impl.PlatformImpl;

import java.util.ArrayList;
import java.util.List;

public class PlatformBuilder {

    private final String name;
    private String downloadUrl = null;
    private boolean custom = false;
    private boolean proxy = false;
    private PlatformBase base = PlatformBase.UNKNOWN;
    private String preCacheBuilder = null;
    private String parser = null;
    private String hashType = null;
    private List<String> prepareSteps = new ArrayList<>();

    public PlatformBuilder(String name) {
        this.name = name;
    }

    public PlatformBuilder downloadUrl(String downloadUrl) {
        this.downloadUrl = downloadUrl;
        return this;
    }

    public PlatformBuilder custom(boolean custom) {
        this.custom = custom;
        return this;
    }

    public PlatformBuilder proxy(boolean proxy) {
        this.proxy = proxy;
        return this;
    }

    public PlatformBuilder base(PlatformBase base) {
        this.base = base;
        return this;
    }

    public PlatformBuilder preCacheBuilder(String preCacheBuilder) {
        this.preCacheBuilder = preCacheBuilder;
        return this;
    }

    public PlatformBuilder parser(String parser) {
        this.parser = parser;
        return this;
    }

    public PlatformBuilder hashType(String hashType) {
        this.hashType = hashType;
        return this;
    }

    public PlatformBuilder prepareStep(String step) {
        this.prepareSteps.add(step);
        return this;
    }

    public PlatformBuilder prepareSteps(List<String> prepareSteps) {
        this.prepareSteps = new ArrayList<>(prepareSteps);
        return this;
    }

    public Platform build() {
        return new PlatformImpl(name, downloadUrl, custom, proxy, base, preCacheBuilder, parser, hashType, prepareSteps);
    }
}
