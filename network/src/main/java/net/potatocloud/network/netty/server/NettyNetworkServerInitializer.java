package net.potatocloud.network.netty.server;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import net.potatocloud.network.netty.codec.NettyPacketDecoder;
import net.potatocloud.network.netty.codec.NettyPacketEncoder;
import net.potatocloud.network.packet.PacketManager;

public class NettyNetworkServerInitializer extends ChannelInitializer<SocketChannel> {

    private final PacketManager packetManager;
    private final NettyNetworkServer server;

    public NettyNetworkServerInitializer(PacketManager packetManager, NettyNetworkServer server) {
        this.packetManager = packetManager;
        this.server = server;
    }

    @Override
    protected void initChannel(SocketChannel channel) {
        final ChannelPipeline pipeline = channel.pipeline();
        pipeline.addLast(new NettyPacketDecoder(packetManager));
        pipeline.addLast(new NettyPacketEncoder(server.packetManager()));
        pipeline.addLast(new NettyServerHandler(server, packetManager));
    }
}
