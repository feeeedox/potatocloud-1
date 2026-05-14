package net.potatocloud.network.netty.codec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import net.potatocloud.network.netty.PacketBuffer;
import net.potatocloud.network.packet.Packet;
import net.potatocloud.network.packet.PacketManager;
import net.potatocloud.network.packet.request.RequestPacket;
import net.potatocloud.network.packet.request.ResponsePacket;

public class NettyPacketEncoder extends MessageToByteEncoder<Packet> {

    private final PacketManager packetManager;

    public NettyPacketEncoder(PacketManager packetManager) {
        this.packetManager = packetManager;
    }

    @Override
    protected void encode(ChannelHandlerContext ctx, Packet packet, ByteBuf out) {
        // Create a new buffer for the packet with the id and packet data
        final ByteBuf buf = ctx.alloc().buffer();

        // Write the packet id and packet data into the buffer
        buf.writeInt(packetManager.packetId(packet));

        if (packet instanceof RequestPacket requestPacket) {
            buf.writeInt(packetManager.requestId(requestPacket));
        } else if (packet instanceof ResponsePacket responsePacket) {
            buf.writeInt(packetManager.requestId(responsePacket));
        } else {
            buf.writeInt(0);
        }

        packetManager.codec(packetManager.packetId(packet)).encode(packet, new PacketBuffer(buf));

        // Payload length
        out.writeInt(buf.readableBytes());

        // Write the payload
        out.writeBytes(buf);
        buf.release();
    }
}
