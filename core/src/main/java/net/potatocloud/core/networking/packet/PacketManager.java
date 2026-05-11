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
    private final Map<Class<? extends Packet>, CopyOnWriteArrayList<PacketListener<? extends Packet>>> listeners = new ConcurrentHashMap<>();
    private final Map<Integer, PendingRequest<?>> pending = new ConcurrentHashMap<>();
    private final AtomicInteger requestCounter = new AtomicInteger(1);

    public void register(int id, Supplier<? extends Packet> supplier) {
        packets.put(id, supplier);
    }

    public Packet createPacket(int id) {
        final Supplier<? extends Packet> supplier = packets.get(id);
        return supplier != null ? supplier.get() : null;
    }

    public <T extends Packet> void on(Class<T> type, PacketListener<T> listener) {
        listeners.computeIfAbsent(type, _ -> new CopyOnWriteArrayList<>()).add(listener);
    }

    public <T extends ResponsePacket> CompletableFuture<T> request(NetworkConnection connection, RequestPacket packet, Class<T> type) {
        final int id = requestCounter.getAndIncrement();
        packet.requestId(id);

        final CompletableFuture<T> future = new CompletableFuture<>();
        pending.put(id, new PendingRequest<>(type, future));

        connection.send(packet);

        future.whenComplete((_, _) -> pending.remove(id));

        return future;
    }

    @SuppressWarnings("unchecked")
    public <T extends Packet> void dispatch(NetworkConnection connection, T packet) {
        if (packet instanceof ResponsePacket response) {
            final int id = response.requestId();
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
        if (packet instanceof RequestPacket req) {
            requestId = req.requestId();
        }

        final PacketContext<T> ctx = new PacketContext<>(connection, packet, requestId);

        for (PacketListener<? extends Packet> handler : list) {
            ((PacketListener<T>) handler).handle(ctx);
        }
    }
}