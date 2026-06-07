package net.potatocloud.node.service.listeners;

import lombok.RequiredArgsConstructor;
import net.potatocloud.api.service.Service;
import net.potatocloud.api.service.ServiceManager;
import net.potatocloud.network.packet.PacketContext;
import net.potatocloud.network.packet.PacketListener;
import net.potatocloud.network.packet.packets.service.ServiceExecuteCommandPacket;
import net.potatocloud.node.cluster.ClusterManagerImpl;

@RequiredArgsConstructor
public class ServiceExecuteCommandListener implements PacketListener<ServiceExecuteCommandPacket> {

    private final ServiceManager serviceManager;
    private final ClusterManagerImpl clusterManager;

    @Override
    public void handle(PacketContext<ServiceExecuteCommandPacket> ctx) {
        final Service service = serviceManager.getService(ctx.packet().serviceName());
        if (service == null) {
            return;
        }

        final String nodeName = service.nodeName();
        if (!clusterManager.isLocal(nodeName)) {
            clusterManager.sendTo(nodeName, ctx.packet());
            return;
        }

        service.executeCommand(ctx.packet().command());
    }
}
