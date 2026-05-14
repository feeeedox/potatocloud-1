package net.potatocloud.core.networking.netty.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.epoll.Epoll;
import io.netty.channel.epoll.EpollSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import net.potatocloud.core.networking.ConnectionListener;
import net.potatocloud.core.networking.NetworkClient;
import net.potatocloud.core.networking.NetworkConnection;
import net.potatocloud.core.networking.netty.NettyNetworkConnection;
import net.potatocloud.core.networking.netty.NettyUtils;
import net.potatocloud.core.networking.packet.Packet;
import net.potatocloud.core.networking.packet.PacketListener;
import net.potatocloud.core.networking.packet.PacketManager;
import net.potatocloud.core.networking.packet.PacketRegistry;
import net.potatocloud.core.networking.packet.request.RequestPacket;
import net.potatocloud.core.networking.packet.request.ResponsePacket;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class NettyNetworkClient implements NetworkClient {

    private final PacketManager packetManager;
    private Channel channel;
    private EventLoopGroup group;
    private NetworkConnection connection;
    private final List<ConnectionListener> listeners = new ArrayList<>();

    public NettyNetworkClient(PacketManager packetManager) {
        this.packetManager = packetManager;
    }

    @Override
    public void connect(String host, int port) {
        PacketRegistry.registerPackets(packetManager);

        group = NettyUtils.createEventLoopGroup();

        final ChannelFuture future = new Bootstrap()
                .group(group)
                .channel(Epoll.isAvailable() ? EpollSocketChannel.class : NioSocketChannel.class)
                .handler(new NettyNetworkClientInitializer(packetManager, this))
                .connect(host, port).syncUninterruptibly();

        future.addListener(f -> {
            if (!f.isSuccess()) {
                return;
            }
            channel = future.channel();
            connection = new NettyNetworkConnection(channel);
            onConnected();
        });
    }

    @Override
    public void send(Packet packet) {
        channel.writeAndFlush(packet);
    }

    @Override
    public void addConnectionListener(ConnectionListener listener) {
        listeners.add(listener);
    }

    public void onConnected() {
        listeners.forEach(ConnectionListener::onConnected);
    }

    @Override
    public void close() {
        channel.close().syncUninterruptibly();
        group.shutdownGracefully().syncUninterruptibly();
    }

    @Override
    public <T extends Packet> void on(Class<T> packetClass, PacketListener<T> listener) {
        packetManager.on(packetClass, listener);
    }

    @Override
    public <T extends ResponsePacket> CompletableFuture<T> request(RequestPacket packet, Class<T> type) {
        return packetManager.request(connection, packet, type);
    }

    public NetworkConnection connection() {
        return connection;
    }

    public PacketManager packetManager() {
        return packetManager;
    }
}
