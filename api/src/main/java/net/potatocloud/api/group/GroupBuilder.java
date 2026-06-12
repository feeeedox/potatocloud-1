package net.potatocloud.api.group;

import net.potatocloud.api.group.impl.GroupImpl;
import net.potatocloud.api.property.Property;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class GroupBuilder {

    private final String name;
    private String nodeName = "";
    private String platformName;
    private String platformVersionName;
    private int minServices = 1;
    private int maxServices = 1;
    private int maxPlayers = 100;
    private int maxMemory = 2048;
    private boolean fallback = false;
    private boolean staticServices = false;
    private int startPriority = 1;
    private int startPercentage = 80;
    private String javaCommand = "java";
    private final Set<String> customJvmFlags = new HashSet<>();
    private final Map<String, Property<?>> properties = new HashMap<>();

    public GroupBuilder(String name) {
        this.name = name;
    }

    public GroupBuilder node(String nodeName) {
        this.nodeName = nodeName;
        return this;
    }

    public GroupBuilder platform(String platformName) {
        this.platformName = platformName;
        return this;
    }

    public GroupBuilder platformVersion(String platformVersionName) {
        this.platformVersionName = platformVersionName;
        return this;
    }

    public GroupBuilder minServices(int minServices) {
        this.minServices = minServices;
        return this;
    }

    public GroupBuilder maxServices(int maxServices) {
        this.maxServices = maxServices;
        return this;
    }

    public GroupBuilder maxPlayers(int maxPlayers) {
        this.maxPlayers = maxPlayers;
        return this;
    }

    public GroupBuilder maxMemory(int maxMemory) {
        this.maxMemory = maxMemory;
        return this;
    }

    public GroupBuilder fallback(boolean fallback) {
        this.fallback = fallback;
        return this;
    }

    public GroupBuilder staticServices(boolean staticServices) {
        this.staticServices = staticServices;
        return this;
    }

    public GroupBuilder startPriority(int startPriority) {
        this.startPriority = startPriority;
        return this;
    }

    public GroupBuilder startPercentage(int startPercentage) {
        this.startPercentage = startPercentage;
        return this;
    }

    public GroupBuilder javaCommand(String javaCommand) {
        this.javaCommand = javaCommand;
        return this;
    }

    public GroupBuilder customJvmFlag(String flag) {
        this.customJvmFlags.add(flag);
        return this;
    }

    public <T> GroupBuilder property(Property<T> property, T value) {
        property.value(value);
        this.properties.put(property.name(), property);
        return this;
    }

    public Group build() {
        return new GroupImpl(
                name,
                nodeName,
                platformName,
                platformVersionName,
                javaCommand,
                customJvmFlags,
                maxPlayers,
                maxMemory,
                minServices,
                maxServices,
                staticServices,
                fallback,
                startPriority,
                startPercentage,
                properties
        );
    }
}
