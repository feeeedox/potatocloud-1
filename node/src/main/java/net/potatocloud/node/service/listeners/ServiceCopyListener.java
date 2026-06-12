package net.potatocloud.node.service.listeners;

import lombok.RequiredArgsConstructor;
import net.potatocloud.api.cluster.ClusterNode;
import net.potatocloud.api.service.ServiceManager;
import net.potatocloud.network.packet.PacketContext;
import net.potatocloud.network.packet.PacketListener;
import net.potatocloud.network.packet.packets.service.ServiceCopyPacket;
import net.potatocloud.node.cluster.ClusterManagerImpl;

import java.util.Optional;

@RequiredArgsConstructor
public class ServiceCopyListener implements PacketListener<ServiceCopyPacket> {

    private final ServiceManager serviceManager;
    private final ClusterManagerImpl clusterManager;

    @Override
    public void handle(PacketContext<ServiceCopyPacket> ctx) {
        final ServiceCopyPacket packet = ctx.packet();

        serviceManager.find(packet.serviceName()).ifPresent(service -> {
            final Optional<ClusterNode> node = service.node();

            if (node.isPresent() && !clusterManager.isLocal(node.get().name())) {
                clusterManager.sendTo(node.get().name(), packet);
                return;
            }

            serviceManager.copyTo(service, packet.templateName(), packet.filter());
        });
    }
}
