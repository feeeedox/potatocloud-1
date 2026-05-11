package net.potatocloud.core.networking.packet.exception;

public class PacketToBigException extends Exception {

    public PacketToBigException(int length) {
        super("Received a packet that is too big! (" + length + ")");

    }
}
