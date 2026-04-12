package net.potatocloud.webinterface.dto.player;

import lombok.Builder;
import lombok.Value;
import net.potatocloud.api.group.ServiceGroup;
import net.potatocloud.api.player.CloudPlayer;
import net.potatocloud.api.service.Service;
import net.potatocloud.webinterface.dto.group.GroupDto;
import net.potatocloud.webinterface.dto.group.PropertyDto;

import java.util.List;

@Value
@Builder
public class PlayerDto {
    String username;
    String uniqueId;
    int connectedProxyId;
    int connectedServerId;
    String connectedProxyName;
    String connectedServerName;
    GroupDto serverGroup;
    GroupDto proxyGroup;
    List<PropertyDto> properties;

    public static PlayerDto from(CloudPlayer player, boolean localNodeReady) {
        Service connectedServer = player.getConnectedService();
        Service connectedProxy = player.getConnectedProxy();

        if (connectedServer == null || connectedProxy == null) {
            return PlayerDto.builder()
                    .username(player.getUsername())
                    .uniqueId(player.getUniqueId().toString())
                    .connectedProxyId(-1)
                    .connectedServerId(-1)
                    .properties(player.getProperties().stream().map(PropertyDto::from).toList())
                    .build();
        }

        ServiceGroup serverGroup = connectedServer.getServiceGroup();
        ServiceGroup proxyGroup = connectedProxy.getServiceGroup();

        return PlayerDto.builder()
                .username(player.getUsername())
                .uniqueId(player.getUniqueId().toString())
                .connectedProxyId(connectedProxy.getServiceId())
                .connectedServerId(connectedServer.getServiceId())
                .connectedProxyName(connectedProxy.getName())
                .connectedServerName(connectedServer.getName())
                .serverGroup(GroupDto.from(serverGroup, localNodeReady))
                .proxyGroup(GroupDto.from(proxyGroup, localNodeReady))
                .properties(player.getProperties().stream().map(PropertyDto::from).toList())
                .build();
    }
}

