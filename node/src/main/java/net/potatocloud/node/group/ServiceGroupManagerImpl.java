package net.potatocloud.node.group;

import lombok.Getter;
import net.potatocloud.api.group.ServiceGroup;
import net.potatocloud.api.group.ServiceGroupManager;
import net.potatocloud.api.group.impl.ServiceGroupImpl;
import net.potatocloud.api.logging.Logger;
import net.potatocloud.api.property.Property;
import net.potatocloud.api.service.Service;
import net.potatocloud.common.FileUtils;
import net.potatocloud.network.NetworkServer;
import net.potatocloud.network.packet.packets.group.GroupAddPacket;
import net.potatocloud.network.packet.packets.group.GroupDeletePacket;
import net.potatocloud.network.packet.packets.group.GroupUpdatePacket;
import net.potatocloud.network.packet.packets.group.RequestGroupsPacket;
import net.potatocloud.node.Node;
import net.potatocloud.node.group.config.ServiceGroupStorage;
import net.potatocloud.node.group.listeners.GroupAddListener;
import net.potatocloud.node.group.listeners.GroupDeleteListener;
import net.potatocloud.node.group.listeners.GroupUpdateListener;
import net.potatocloud.node.group.listeners.RequestGroupsListener;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class ServiceGroupManagerImpl implements ServiceGroupManager {

    private final List<ServiceGroup> groups = new ArrayList<>();

    @Getter
    private final Path groupsPath;

    private final NetworkServer server;
    private final Logger logger;

    public ServiceGroupManagerImpl(Path groupsPath, NetworkServer server, Logger logger) {
        this.groupsPath = groupsPath;
        this.server = server;
        this.logger = logger;

        server.on(RequestGroupsPacket.class, new RequestGroupsListener(this));
        server.on(GroupUpdatePacket.class, new GroupUpdateListener(this, server));
        server.on(GroupAddPacket.class, new GroupAddListener(this, server));
        server.on(GroupDeletePacket.class, new GroupDeleteListener(this, server));

        loadGroups();
    }

    @Override
    public ServiceGroup getServiceGroup(String name) {
        return groups.stream()
                .filter(group -> group.getName().equalsIgnoreCase(name))
                .findFirst()
                .orElse(null);
    }

    @Override
    public List<ServiceGroup> getAllServiceGroups() {
        return Collections.unmodifiableList(groups);
    }

    @Override
    public void createServiceGroup(
            String name,
            String nodeName,
            String platformName,
            String platformVersionName,
            int minOnlineCount,
            int maxOnlineCount,
            int maxPlayers,
            int maxMemory,
            boolean fallback,
            boolean isStatic,
            int startPriority,
            int startPercentage,
            String javaCommand,
            List<String> customJvmFlags,
            Map<String, Property<?>> propertyMap
    ) {

        if (existsServiceGroup(name)) {
            return;
        }

        final ServiceGroup group = new ServiceGroupImpl(
                name,
                nodeName,
                platformName,
                platformVersionName,
                javaCommand,
                customJvmFlags,
                maxPlayers,
                maxMemory,
                minOnlineCount,
                maxOnlineCount,
                isStatic,
                fallback,
                startPriority,
                startPercentage,
                propertyMap
        );

        addServiceGroup(group);

        // Send group add packet to clients
        server.broadcast().connectors().send(new GroupAddPacket(group));

        logger.info("Group &a" + name + " &7was successfully created");
    }

    public void addServiceGroup(ServiceGroup group) {
        if (group == null || existsServiceGroup(group.getName())) {
            return;
        }

        for (String templateName : group.getServiceTemplates()) {
            Node.getInstance().getTemplateManager().createTemplate(templateName);
        }

        ServiceGroupStorage.save(group, groupsPath);
        groups.add(group);
    }

    public void registerServiceGroup(ServiceGroup group) {
        if (group == null || existsServiceGroup(group.getName())) {
            return;
        }
        groups.add(group);
    }

    @Override
    public void deleteServiceGroup(String name) {
        if (!deleteServiceGroupLocal(name)) {
            return;
        }

        server.broadcast().connectors().send(new GroupDeletePacket(name));
    }

    public boolean deleteServiceGroupLocal(String name) {
        final ServiceGroup group = getServiceGroup(name);

        if (group == null || !groups.contains(group)) {
            return false;
        }

        group.getAllServices().forEach(Service::shutdown);

        groups.remove(group);

        final Path filePath = groupsPath.resolve(name + ".yml");
        try {
            Files.deleteIfExists(filePath);
        } catch (IOException e) {
            logger.error("Failed to delete group file for: " + name);
        }
        return true;
    }

    @Override
    public void updateServiceGroup(ServiceGroup group) {
        ServiceGroupStorage.save(group, groupsPath);

        server.broadcast().connectors().send(new GroupUpdatePacket(
                group.getName(),
                group.getCustomJvmFlags(),
                group.getMaxPlayers(),
                group.getMaxMemory(),
                group.getMinOnlineCount(),
                group.getMaxOnlineCount(),
                group.isFallback(),
                group.getStartPriority(),
                group.getStartPercentage(),
                group.getServiceTemplates(),
                group.getPropertyMap()
        ));
    }

    @Override
    public boolean existsServiceGroup(String name) {
        if (name == null) {
            return false;
        }
        return groups.stream().anyMatch(group -> group != null && group.getName().equalsIgnoreCase(name));
    }

    private void loadGroups() {
        if (Files.notExists(groupsPath)) {
            return;
        }

        FileUtils.list(groupsPath).stream()
                .filter(Files::isRegularFile)
                .filter(path -> path.toString().endsWith(".yml"))
                .forEach(path -> {
                    try {
                        groups.add(ServiceGroupStorage.load(path));
                    } catch (Exception e) {
                        logger.error("Failed to load group file&8: &a" + path.getFileName());
                    }
                });
    }
}
