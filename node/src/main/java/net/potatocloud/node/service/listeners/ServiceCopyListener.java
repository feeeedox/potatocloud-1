package net.potatocloud.node.service.listeners;

import lombok.RequiredArgsConstructor;
import net.potatocloud.api.service.Service;
import net.potatocloud.api.service.ServiceManager;
import net.potatocloud.network.packet.PacketContext;
import net.potatocloud.network.packet.PacketListener;
import net.potatocloud.network.packet.packets.service.ServiceCopyPacket;
import net.potatocloud.node.cluster.ClusterManagerImpl;

@RequiredArgsConstructor
public class ServiceCopyListener implements PacketListener<ServiceCopyPacket> {

    private final ServiceManager serviceManager;
    private final ClusterManagerImpl clusterManager;

    @Override
    public void handle(PacketContext<ServiceCopyPacket> ctx) {
        final ServiceCopyPacket packet = ctx.packet();
        final Service service = serviceManager.getService(packet.serviceName());
        if (service == null) {
            return;
        }

        final String nodeName = service.nodeName();
        if (!clusterManager.isLocal(nodeName)) {
            clusterManager.sendTo(nodeName, packet);
            return;
        }

        service.copy(packet.templateName(), packet.filter());
    }
}
