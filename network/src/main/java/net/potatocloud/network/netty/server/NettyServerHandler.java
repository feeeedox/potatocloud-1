package net.potatocloud.network.netty.server;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import net.potatocloud.network.netty.NettyNetworkConnection;
import net.potatocloud.network.packet.Packet;
import net.potatocloud.network.packet.PacketManager;

import java.net.SocketException;

public class NettyServerHandler extends ChannelInboundHandlerAdapter {

    private final NettyNetworkServer server;
    private final PacketManager packetManager;

    public NettyServerHandler(NettyNetworkServer server, PacketManager packetManager) {
        this.server = server;
        this.packetManager = packetManager;
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        if (cause instanceof SocketException) {
            return;
        }
        super.exceptionCaught(ctx, cause);
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        server.connectedSessions().add(new NettyNetworkConnection(ctx.channel()));
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        server.connectedSessions().removeIf(session -> ((NettyNetworkConnection) session).channel().equals(ctx.channel()));
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        if (msg instanceof Packet packet) {
            // Find the session the packet was sent to and handle it
            server.connectedSessions().stream()
                    .filter(conn -> conn instanceof NettyNetworkConnection nettyConn && nettyConn.channel().equals(ctx.channel()))
                    .findFirst()
                    .ifPresent(connection -> packetManager.dispatch(connection, packet));
        }
    }
}
