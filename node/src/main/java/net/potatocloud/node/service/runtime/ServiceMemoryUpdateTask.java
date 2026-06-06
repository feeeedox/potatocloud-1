package net.potatocloud.node.service.runtime;

import net.potatocloud.api.service.Service;
import net.potatocloud.network.NetworkServer;
import net.potatocloud.network.packet.packets.service.ServiceMemoryUpdatePacket;
import net.potatocloud.node.cluster.ClusterManagerImpl;

public class ServiceMemoryUpdateTask {

    private static final int UPDATE_INTERVAL = 2000;

    public ServiceMemoryUpdateTask(Service service, NetworkServer server, ClusterManagerImpl clusterManager) {
        Thread.startVirtualThread(() -> {
            while (service.isOnline()) {

                // send current memory to the connector to keep it updated
                // we use a separate packet from ServiceUpdatePacket for performance because ServiceUpdatePacket contains stuff that does not need constant syncing
                final ServiceMemoryUpdatePacket packet = new ServiceMemoryUpdatePacket(service.getName(), service.getUsedMemory());
                server.broadcast().connectors().send(packet);
                clusterManager.broadcast(packet);

                try {
                    Thread.sleep(UPDATE_INTERVAL);
                } catch (InterruptedException e) {
                    break;
                }
            }
        });
    }
}
