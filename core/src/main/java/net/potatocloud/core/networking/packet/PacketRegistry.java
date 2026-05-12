package net.potatocloud.core.networking.packet;

import net.potatocloud.core.networking.packet.packets.event.EventPacket;
import net.potatocloud.core.networking.packet.packets.group.GroupAddPacket;
import net.potatocloud.core.networking.packet.packets.group.GroupDeletePacket;
import net.potatocloud.core.networking.packet.packets.group.GroupUpdatePacket;
import net.potatocloud.core.networking.packet.packets.group.RequestGroupsPacket;
import net.potatocloud.core.networking.packet.packets.logging.LogMessagePacket;
import net.potatocloud.core.networking.packet.packets.platform.PlatformAddPacket;
import net.potatocloud.core.networking.packet.packets.platform.PlatformRemovePacket;
import net.potatocloud.core.networking.packet.packets.platform.PlatformUpdatePacket;
import net.potatocloud.core.networking.packet.packets.platform.RequestPlatformsPacket;
import net.potatocloud.core.networking.packet.packets.player.*;
import net.potatocloud.core.networking.packet.packets.property.PropertyAddPacket;
import net.potatocloud.core.networking.packet.packets.property.PropertyRemovePacket;
import net.potatocloud.core.networking.packet.packets.property.PropertyUpdatePacket;
import net.potatocloud.core.networking.packet.packets.property.RequestPropertiesPacket;
import net.potatocloud.core.networking.packet.packets.service.*;

public final class PacketRegistry {

    private PacketRegistry() {
    }

    public static void registerPackets(PacketManager manager) {
        manager.register(0, ServiceAddPacket.class, ServiceAddPacket::new);
        manager.register(1, ServiceRemovePacket.class, ServiceRemovePacket::new);
        manager.register(2, ServiceUpdatePacket.class, ServiceUpdatePacket::new);
        manager.register(3, ServiceStartedPacket.class, ServiceStartedPacket::new);
        manager.register(4, RequestServicesPacket.class, RequestServicesPacket::new);
        manager.register(5, StartServicePacket.class, StartServicePacket::new);
        manager.register(6, StopServicePacket.class, StopServicePacket::new);
        manager.register(7, ServiceExecuteCommandPacket.class, ServiceExecuteCommandPacket::new);
        manager.register(8, ServiceCopyPacket.class, ServiceCopyPacket::new);
        manager.register(9, ServiceMemoryUpdatePacket.class, ServiceMemoryUpdatePacket::new);

        manager.register(100, RequestGroupsPacket.class, RequestGroupsPacket::new);
        manager.register(101, GroupAddPacket.class, GroupAddPacket::new);
        manager.register(102, GroupUpdatePacket.class, GroupUpdatePacket::new);
        manager.register(104, GroupDeletePacket.class, GroupDeletePacket::new);

        manager.register(200, CloudPlayerAddPacket.class, CloudPlayerAddPacket::new);
        manager.register(201, CloudPlayerRemovePacket.class, CloudPlayerRemovePacket::new);
        manager.register(202, CloudPlayerUpdatePacket.class, CloudPlayerUpdatePacket::new);
        manager.register(203, CloudPlayerConnectPacket.class, CloudPlayerConnectPacket::new);
        manager.register(204, RequestCloudPlayersPacket.class, RequestCloudPlayersPacket::new);

        manager.register(300, EventPacket.class, EventPacket.CODEC);

        manager.register(400, PlatformAddPacket.class, PlatformAddPacket::new);
        manager.register(401, PlatformRemovePacket.class, PlatformRemovePacket::new);
        manager.register(402, RequestPlatformsPacket.class, RequestPlatformsPacket::new);
        manager.register(403, PlatformUpdatePacket.class, PlatformUpdatePacket::new);

        manager.register(500, RequestPropertiesPacket.class, RequestPropertiesPacket::new);
        manager.register(501, PropertyUpdatePacket.class, PropertyUpdatePacket::new);
        manager.register(502, PropertyRemovePacket.class, PropertyRemovePacket::new);
        manager.register(503, PropertyAddPacket.class, PropertyAddPacket::new);

        manager.register(600, LogMessagePacket.class, LogMessagePacket::new);
    }
}
