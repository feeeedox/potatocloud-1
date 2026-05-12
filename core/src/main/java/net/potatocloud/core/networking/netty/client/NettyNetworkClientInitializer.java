package net.potatocloud.core.networking.netty.client;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import lombok.RequiredArgsConstructor;
import net.potatocloud.core.networking.netty.codec.NettyPacketDecoder;
import net.potatocloud.core.networking.netty.codec.NettyPacketEncoder;
import net.potatocloud.core.networking.packet.PacketManager;

@RequiredArgsConstructor
public class NettyNetworkClientInitializer extends ChannelInitializer<SocketChannel> {

    private final PacketManager packetManager;
    private final NettyNetworkClient client;

    @Override
    protected void initChannel(SocketChannel channel) {
        final ChannelPipeline pipeline = channel.pipeline();
        pipeline.addLast(new NettyPacketDecoder(packetManager));
        pipeline.addLast(new NettyPacketEncoder(client.getPacketManager()));
        pipeline.addLast(new NettyClientHandler(packetManager, client));
    }
}
