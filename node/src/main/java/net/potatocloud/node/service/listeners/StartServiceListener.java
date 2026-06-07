package net.potatocloud.node.service.listeners;

import lombok.RequiredArgsConstructor;
import net.potatocloud.api.group.ServiceGroup;
import net.potatocloud.api.group.ServiceGroupManager;
import net.potatocloud.network.packet.PacketContext;
import net.potatocloud.network.packet.PacketListener;
import net.potatocloud.network.packet.packets.service.StartServicePacket;
import net.potatocloud.node.cluster.ClusterManagerImpl;
import net.potatocloud.node.service.ServiceManagerImpl;

@RequiredArgsConstructor
public class StartServiceListener implements PacketListener<StartServicePacket> {

    private final ServiceManagerImpl serviceManager;
    private final ServiceGroupManager groupManager;
    private final ClusterManagerImpl clusterManager;

    @Override
    public void handle(PacketContext<StartServicePacket> ctx) {
        final ServiceGroup group = groupManager.getServiceGroup(ctx.packet().groupName());
        if (group == null) {
            return;
        }

        final String nodeName = group.nodeName();
        if (!clusterManager.isLocal(nodeName)) {
            clusterManager.sendTo(nodeName, ctx.packet());
            return;
        }

        if (!serviceManager.hasEnoughMemory(group)) {
            serviceManager.logMemoryWarning(group);
            return;
        }

        serviceManager.startServiceInternal(group.getName(), ctx.packet().requestId());
    }
}
