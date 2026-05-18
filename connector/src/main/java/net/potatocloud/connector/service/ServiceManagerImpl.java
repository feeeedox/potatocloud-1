package net.potatocloud.connector.service;

import lombok.Getter;
import net.potatocloud.api.service.Service;
import net.potatocloud.api.service.ServiceManager;
import net.potatocloud.connector.service.listeners.ServiceAddListener;
import net.potatocloud.connector.service.listeners.ServiceMemoryUpdateListener;
import net.potatocloud.connector.service.listeners.ServiceUpdateListener;
import net.potatocloud.network.NetworkClient;
import net.potatocloud.network.packet.packets.service.*;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class ServiceManagerImpl implements ServiceManager {

    private final List<Service> services = new CopyOnWriteArrayList<>();

    @Getter
    private final Map<String, CompletableFuture<Service>> pendingStarts = new ConcurrentHashMap<>();

    private final NetworkClient client;

    public ServiceManagerImpl(NetworkClient client) {
        this.client = client;

        client.on(ServiceAddPacket.class, new ServiceAddListener(this));

        client.on(ServiceRemovePacket.class, ctx -> services.remove(getService(ctx.packet().serviceName())));

        client.on(ServiceUpdatePacket.class, new ServiceUpdateListener(this));

        client.on(ServiceMemoryUpdatePacket.class, new ServiceMemoryUpdateListener(this));

        client.send(new RequestServicesPacket());
    }

    public void addService(Service service) {
        services.add(service);
    }

    @Override
    public Service getService(String name) {
        return services.stream()
                .filter(service -> service.getName().equalsIgnoreCase(name))
                .findFirst()
                .orElse(null);
    }

    @Override
    public List<Service> getAllServices() {
        return Collections.unmodifiableList(services);
    }

    @Override
    public void updateService(Service service) {
        client.send(new ServiceUpdatePacket(
                service.getName(),
                service.getStatus().name(),
                service.getMaxPlayers(),
                service.getPropertyMap())
        );
    }

    @Override
    public void startService(String groupName) {
        client.send(new StartServicePacket(groupName, null));
    }

    @Override
    public CompletableFuture<Service> startServiceAsync(String groupName) {
        final CompletableFuture<Service> future = new CompletableFuture<>();
        final String requestId = UUID.randomUUID().toString();

        pendingStarts.put(requestId, future);
        client.send(new StartServicePacket(groupName, requestId));

        return future;
    }

    @Override
    public Service getCurrentService() {
        return getService(System.getProperty("potatocloud.service.name"));
    }
}