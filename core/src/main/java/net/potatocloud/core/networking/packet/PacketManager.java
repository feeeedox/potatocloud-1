package net.potatocloud.core.networking.packet;

import net.potatocloud.core.networking.NetworkConnection;
import net.potatocloud.core.networking.packet.request.PendingRequest;
import net.potatocloud.core.networking.packet.request.RequestPacket;
import net.potatocloud.core.networking.packet.request.ResponsePacket;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;

public final class PacketManager {

    private final Map<Integer, Supplier<? extends Packet>> packets = new ConcurrentHashMap<>();
    private final Map<Class<? extends Packet>, Integer> packetIds = new ConcurrentHashMap<>();

    private final Map<Class<? extends Packet>, CopyOnWriteArrayList<PacketListener<? extends Packet>>> listeners = new ConcurrentHashMap<>();

    private final Map<Integer, PendingRequest<?>> pending = new ConcurrentHashMap<>();
    private final AtomicInteger requestCounter = new AtomicInteger(1);
    private final Map<Integer, Integer> requestIds = new ConcurrentHashMap<>();

    public <T extends Packet> void register(int id, Class<T> clazz, Supplier<? extends Packet> supplier) {
        packets.put(id, supplier);
        packetIds.put(clazz, id);
    }

    public Packet createPacket(int id) {
        final Supplier<? extends Packet> supplier = packets.get(id);
        return supplier != null ? supplier.get() : null;
    }

    public int packetId(Packet packet) {
        final Integer id = packetIds.get(packet.getClass());
        if (id == null) {
            throw new IllegalStateException("Packet not registered: " + packet.getClass().getName());
        }

        return id;
    }

    public int requestId(Packet packet) {
        return requestIds.getOrDefault(packetId(packet), 0);
    }

    public void requestId(Packet packet, int requestId) {
        if (requestId != 0) {
            requestIds.put(packetId(packet), requestId);
        }
    }

    public void removeRequest(Packet packet) {
        requestIds.remove(packetId(packet));
    }

    public <T extends Packet> void on(Class<T> type, PacketListener<T> listener) {
        listeners.computeIfAbsent(type, _ -> new CopyOnWriteArrayList<>()).add(listener);
    }

    public <T extends ResponsePacket> CompletableFuture<T> request(NetworkConnection connection, RequestPacket packet, Class<T> type) {
        final int id = requestCounter.getAndIncrement();
        requestIds.put(packetId(packet), id);

        final CompletableFuture<T> future = new CompletableFuture<>();
        pending.put(id, new PendingRequest<>(type, future));

        connection.send(packet);

        future.whenComplete((_, _) -> pending.remove(id));

        return future;
    }

    @SuppressWarnings("unchecked")
    public <T extends Packet> void dispatch(NetworkConnection connection, T packet) {
        if (packet instanceof ResponsePacket response) {
            final int id = requestId(packet);
            removeRequest(packet);

            final PendingRequest<?> pendingRequest = pending.get(id);

            if (pendingRequest.future() == null || pendingRequest.future().isDone()) {
                return;
            }

            if (pendingRequest.responseType().isInstance(response)) {
                ((CompletableFuture<Object>) pendingRequest.future()).complete(response);
            } else {
                pendingRequest.future().completeExceptionally(new IllegalStateException(
                        "Expected " + pendingRequest.responseType().getSimpleName() + " but got " + response.getClass().getSimpleName()));
            }
            return;
        }

        final List<PacketListener<? extends Packet>> list = listeners.get(packet.getClass());
        if (list == null) {
            return;
        }

        int requestId = 0;
        if (packet instanceof RequestPacket requestPacket) {
            requestId = requestId(requestPacket);
        }

        final PacketContext<T> ctx = new PacketContext<>(connection, this, packet, requestId);

        for (PacketListener<? extends Packet> handler : list) {
            ((PacketListener<T>) handler).handle(ctx);
        }
    }
}