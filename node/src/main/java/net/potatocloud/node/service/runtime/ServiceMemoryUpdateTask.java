package net.potatocloud.node.service.runtime;

import net.potatocloud.api.service.Service;
import net.potatocloud.network.NetworkServer;
import net.potatocloud.network.packet.packets.service.ServiceMemoryUpdatePacket;

public class ServiceMemoryUpdateTask {

    private static final int UPDATE_INTERVAL = 2000;

    public ServiceMemoryUpdateTask(Service service, NetworkServer server) {
        Thread.startVirtualThread(() -> {
            while (service.isOnline()) {

                // Send current memory to the connector to keep it updated
                // We use a separate packet from ServiceUpdatePacket for performance because ServiceUpdatePacket contains stuff that does not need constant syncing
                server.broadcast().connectors().send(new ServiceMemoryUpdatePacket(service.getName(), service.getUsedMemory()));

                try {
                    Thread.sleep(UPDATE_INTERVAL);
                } catch (InterruptedException e) {
                    break;
                }
            }
        });
    }
}
