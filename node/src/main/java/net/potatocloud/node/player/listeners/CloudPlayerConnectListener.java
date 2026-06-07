package net.potatocloud.node.player.listeners;

import net.potatocloud.network.NetworkServer;
import net.potatocloud.network.packet.PacketContext;
import net.potatocloud.network.packet.PacketListener;
import net.potatocloud.network.packet.packets.player.CloudPlayerConnectPacket;

public class CloudPlayerConnectListener implements PacketListener<CloudPlayerConnectPacket> {

    private final NetworkServer server;

    public CloudPlayerConnectListener(NetworkServer server) {
        this.server = server;
    }

    @Override
    public void handle(PacketContext<CloudPlayerConnectPacket> ctx) {
        server.broadcast().connectors().send(ctx.packet());
    }
}
