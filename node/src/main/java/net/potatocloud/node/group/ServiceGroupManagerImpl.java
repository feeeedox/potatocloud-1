package net.potatocloud.node.group;

import lombok.Getter;
import net.potatocloud.api.CloudAPI;
import net.potatocloud.api.group.ServiceGroup;
import net.potatocloud.api.group.ServiceGroupManager;
import net.potatocloud.api.logging.Logger;
import net.potatocloud.api.service.Service;
import net.potatocloud.common.FileUtils;
import net.potatocloud.network.NetworkServer;
import net.potatocloud.network.packet.packets.group.GroupAddPacket;
import net.potatocloud.network.packet.packets.group.GroupDeletePacket;
import net.potatocloud.network.packet.packets.group.GroupUpdatePacket;
import net.potatocloud.network.packet.packets.group.RequestGroupsPacket;
import net.potatocloud.node.Node;
import net.potatocloud.node.cluster.ClusterManagerImpl;
import net.potatocloud.node.group.config.ServiceGroupStorage;
import net.potatocloud.node.group.listeners.GroupAddListener;
import net.potatocloud.node.group.listeners.GroupDeleteListener;
import net.potatocloud.node.group.listeners.GroupUpdateListener;
import net.potatocloud.node.group.listeners.RequestGroupsListener;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class ServiceGroupManagerImpl implements ServiceGroupManager {

    private final List<ServiceGroup> groups = new ArrayList<>();

    @Getter
    private final Path groupsPath;

    private final NetworkServer server;
    private final Logger logger;
    private final ClusterManagerImpl clusterManager;

    public ServiceGroupManagerImpl(Path groupsPath, NetworkServer server, Logger logger, ClusterManagerImpl clusterManager) {
        this.groupsPath = groupsPath;
        this.server = server;
        this.logger = logger;
        this.clusterManager = clusterManager;

        server.on(RequestGroupsPacket.class, new RequestGroupsListener(this));
        server.on(GroupUpdatePacket.class, new GroupUpdateListener(this, server, clusterManager));
        server.on(GroupAddPacket.class, new GroupAddListener(this, server, clusterManager));
        server.on(GroupDeletePacket.class, new GroupDeleteListener(this, server, clusterManager));

        loadGroups();
    }

    @Override
    public Optional<ServiceGroup> find(String name) {
        return groups.stream()
                .filter(group -> group.name().equalsIgnoreCase(name))
                .findFirst();
    }

    @Override
    public List<ServiceGroup> groups() {
        return Collections.unmodifiableList(groups);
    }

    @Override
    public void create(ServiceGroup group) {
        if (exists(group.name())) {
            return;
        }

        addServiceGroup(group);

        server.broadcast().connectors().send(new GroupAddPacket(group));
        clusterManager.broadcast(new GroupAddPacket(group));

        logger.info("Group &a" + group.name() + " &7was successfully created");
    }

    public void addServiceGroup(ServiceGroup group) {
        if (group == null || exists(group.name())) {
            return;
        }

        for (String templateName : group.templates()) {
            Node.getInstance().templateManager().createTemplate(templateName);
        }

        ServiceGroupStorage.save(group, groupsPath);
        groups.add(group);
    }

    public void registerServiceGroup(ServiceGroup group) {
        if (group == null || exists(group.name())) {
            return;
        }
        groups.add(group);
    }

    public void unregisterServiceGroup(String name) {
        groups.removeIf(group -> group.name().equalsIgnoreCase(name));
    }

    @Override
    public void delete(ServiceGroup group) {
        if (!deleteLocal(group.name())) {
            return;
        }

        server.broadcast().connectors().send(new GroupDeletePacket(group.name()));
        clusterManager.broadcast(new GroupDeletePacket(group.name()));
    }

    public boolean deleteLocal(String name) {
        final Optional<ServiceGroup> group = find(name);

        if (group.isEmpty() || !groups.contains(group.get())) {
            return false;
        }

        for (Service service : group.get().services()) {
            CloudAPI.instance().serviceManager().stop(service); // todo
        }

        groups.remove(group.get());

        final Path filePath = groupsPath.resolve(name + ".yml");
        try {
            Files.deleteIfExists(filePath);
        } catch (IOException e) {
            logger.error("Failed to delete group file for: " + name);
        }
        return true;
    }

    @Override
    public void update(ServiceGroup group) {
        ServiceGroupStorage.save(group, groupsPath);

        final GroupUpdatePacket updatePacket = new GroupUpdatePacket(
                group.name(),
                group.customJvmFlags(),
                group.maxPlayers(),
                group.maxMemory(),
                group.minServices(),
                group.maxServices(),
                group.fallback(),
                group.startPriority(),
                group.startPercentage(),
                group.templates(),
                group.getPropertyMap()
        );
        server.broadcast().connectors().send(updatePacket);
        clusterManager.broadcast(updatePacket);
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
