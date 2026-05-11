package net.potatocloud.core.networking.netty.codec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import lombok.RequiredArgsConstructor;
import net.potatocloud.core.networking.netty.PacketBuffer;
import net.potatocloud.core.networking.packet.Packet;
import net.potatocloud.core.networking.packet.PacketManager;
import net.potatocloud.core.networking.packet.exception.PacketToBigException;
import net.potatocloud.core.networking.packet.request.RequestPacket;
import net.potatocloud.core.networking.packet.request.ResponsePacket;

import java.util.List;

@RequiredArgsConstructor
public class NettyPacketDecoder extends ByteToMessageDecoder {

    private final PacketManager packetManager;

    private static final int MAX_PACKET_SIZE = 65536;

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        if (in.readableBytes() < 4) {
            return;
        }

        in.markReaderIndex();

        // Read packet length and stop if too big
        final int length = in.readInt();
        if (length > MAX_PACKET_SIZE) {
            ctx.close();
            throw new PacketToBigException(length);
        }

        // Wait until the full packet is received
        if (in.readableBytes() < length) {
            in.resetReaderIndex();
            return;
        }

        final int packetId = in.readInt();
        final int requestId = in.readInt();

        final Packet packet = packetManager.createPacket(packetId);
        if (packet == null) {
            in.skipBytes(length - 8);
            return;
        }

        if (packet instanceof RequestPacket req) {
            req.requestId(requestId);
        } else if (packet instanceof ResponsePacket res) {
            res.requestId(requestId);
        }

        // Let the packet read its content
        packet.read(new PacketBuffer(in));

        out.add(packet);
    }
}
