package net.potatocloud.node.service;

import net.potatocloud.api.service.Service;
import net.potatocloud.core.networking.NetworkServer;
import net.potatocloud.core.networking.packet.packets.service.ServiceMemoryUpdatePacket;

public class ServiceMemoryUpdateTask {

    private static final int UPDATE_INTERVAL = 2000;

    public ServiceMemoryUpdateTask(Service service, NetworkServer server) {
        Thread.startVirtualThread(() -> {
            while (service.isOnline()) {

                // Send current memory to the connector to keep it updated
                // We use a separate packet from ServiceUpdatePacket for performance because ServiceUpdatePacket contains stuff that does not need constant syncing
                server.generateBroadcast().broadcast(new ServiceMemoryUpdatePacket(service.getName(), service.getUsedMemory()));

                try {
                    Thread.sleep(UPDATE_INTERVAL);
                } catch (InterruptedException e) {
                    break;
                }
            }
        });
    }
}
