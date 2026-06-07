package net.potatocloud.node.service.listeners;

import lombok.RequiredArgsConstructor;
import net.potatocloud.api.cluster.ClusterNode;
import net.potatocloud.api.service.Service;
import net.potatocloud.api.service.ServiceManager;
import net.potatocloud.network.packet.PacketContext;
import net.potatocloud.network.packet.PacketListener;
import net.potatocloud.network.packet.packets.service.ServiceExecuteCommandPacket;
import net.potatocloud.node.cluster.ClusterManagerImpl;

import java.util.Optional;

@RequiredArgsConstructor
public class ServiceExecuteCommandListener implements PacketListener<ServiceExecuteCommandPacket> {

    private final ServiceManager serviceManager;
    private final ClusterManagerImpl clusterManager;

    @Override
    public void handle(PacketContext<ServiceExecuteCommandPacket> ctx) {
        final ServiceExecuteCommandPacket packet = ctx.packet();

        serviceManager.find(packet.serviceName()).ifPresent(service -> {
            final Optional<ClusterNode> node = service.node();

            if (node.isPresent() && !clusterManager.isLocal(node.get().name())) {
                clusterManager.sendTo(node.get().name(), packet);
                return;
            }

            serviceManager.execute(service, packet.command());
        });
    }
}
