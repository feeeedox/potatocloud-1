package net.potatocloud.node.service.listeners;

import lombok.RequiredArgsConstructor;
import net.potatocloud.api.group.ServiceGroup;
import net.potatocloud.api.group.ServiceGroupManager;
import net.potatocloud.core.networking.packet.PacketContext;
import net.potatocloud.core.networking.packet.PacketListener;
import net.potatocloud.core.networking.packet.packets.service.StartServicePacket;
import net.potatocloud.node.service.ServiceManagerImpl;

@RequiredArgsConstructor
public class StartServiceListener implements PacketListener<StartServicePacket> {

    private final ServiceManagerImpl serviceManager;
    private final ServiceGroupManager groupManager;

    @Override
    public void handle(PacketContext<StartServicePacket> ctx) {
        final ServiceGroup group = groupManager.getServiceGroup(ctx.packet().getGroupName());
        if (group == null) {
            return;
        }

        if (!serviceManager.hasEnoughMemory(group)) {
            serviceManager.logMemoryWarning(group);
            return;
        }

        serviceManager.startServiceInternal(group.getName(), ctx.packet().getRequestId());
    }
}
