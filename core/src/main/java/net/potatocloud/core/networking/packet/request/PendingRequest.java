package net.potatocloud.core.networking.packet.request;

import java.util.concurrent.CompletableFuture;

public record PendingRequest<T extends ResponsePacket>(Class<T> responseType, CompletableFuture<T> future) {

}
