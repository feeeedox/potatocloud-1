package net.potatocloud.core.networking.netty.client;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import net.potatocloud.core.networking.netty.codec.NettyPacketDecoder;
import net.potatocloud.core.networking.netty.codec.NettyPacketEncoder;
import net.potatocloud.core.networking.packet.PacketManager;

public class NettyNetworkClientInitializer extends ChannelInitializer<SocketChannel> {

    private final PacketManager packetManager;
    private final NettyNetworkClient client;

    public NettyNetworkClientInitializer(PacketManager packetManager, NettyNetworkClient client) {
        this.packetManager = packetManager;
        this.client = client;
    }

    @Override
    protected void initChannel(SocketChannel channel) {
        final ChannelPipeline pipeline = channel.pipeline();
        pipeline.addLast(new NettyPacketDecoder(packetManager));
        pipeline.addLast(new NettyPacketEncoder(client.packetManager()));
        pipeline.addLast(new NettyClientHandler(packetManager, client));
    }
}
