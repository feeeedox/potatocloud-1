package net.potatocloud.connector.service.listeners;

import lombok.RequiredArgsConstructor;
import net.potatocloud.api.service.Service;
import net.potatocloud.api.service.ServiceManager;
import net.potatocloud.api.service.impl.ServiceImpl;
import net.potatocloud.network.packet.PacketContext;
import net.potatocloud.network.packet.PacketListener;
import net.potatocloud.network.packet.packets.service.ServiceMemoryUpdatePacket;

@RequiredArgsConstructor
public class ServiceMemoryUpdateListener implements PacketListener<ServiceMemoryUpdatePacket> {

    private final ServiceManager serviceManager;

    @Override
    public void handle(PacketContext<ServiceMemoryUpdatePacket> ctx) {
        serviceManager.find(ctx.packet().serviceName()).ifPresent(service -> {
            if (service instanceof ServiceImpl serviceImpl) {
                serviceImpl.usedMemory(ctx.packet().usedMemory());
            }
        });
    }
}
