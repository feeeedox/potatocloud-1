package net.potatocloud.webinterface.dto.group;

import lombok.Builder;
import lombok.Value;
import net.potatocloud.api.group.ServiceGroup;
import net.potatocloud.api.service.Service;
import net.potatocloud.webinterface.dto.platform.PlatformDto;
import net.potatocloud.webinterface.dto.platform.PlatformVersionDto;

import java.util.List;

@Value
@Builder
public class GroupDto {
    String name;
    String javaCommand;
    PlatformDto platform;
    PlatformVersionDto platformVersion;
    boolean isStatic;
    boolean isFallback;
    boolean localNodeReady;
    int onlineServicesCount;
    int onlinePlayerCount;
    int minOnlineCount;
    int maxOnlineCount;
    int maxPlayerCount;
    int maxMemory;
    int startPriority;
    int newServicePercent;
    List<String> customJvmFlags;
    List<String> serviceTemplates;
    List<PropertyDto> properties;
    boolean useModernVelocityForwarding;

    public static GroupDto from(ServiceGroup group, boolean localNodeReady) {
        return GroupDto.builder()
                .name(group.getName())
                .javaCommand(group.getJavaCommand())
                .platform(PlatformDto.from(group.getPlatform()))
                .platformVersion(PlatformVersionDto.from(group.getPlatformVersion()))
                .isStatic(group.isStatic())
                .isFallback(group.isFallback())
                .localNodeReady(localNodeReady)
                .onlineServicesCount(group.getOnlineServiceCount())
                .onlinePlayerCount(
                        group.getPlatform().isProxy() ? group.getOnlineServices().stream().mapToInt(Service::getOnlinePlayerCount).sum() : group.getOnlinePlayerCount()
                )
                .minOnlineCount(group.getMinOnlineCount())
                .maxOnlineCount(group.getMaxOnlineCount())
                .maxPlayerCount(group.getMaxPlayers())
                .maxMemory(group.getMaxMemory())
                .startPriority(group.getStartPriority())
                .newServicePercent(group.getStartPercentage())
                .customJvmFlags(group.getCustomJvmFlags())
                .serviceTemplates(group.getServiceTemplates())
                .properties(group.getProperties().stream()
                        .map(PropertyDto::from)
                        .toList())
                .useModernVelocityForwarding(group.getProperties().stream()
                        .anyMatch(property -> "velocityModernForwarding".equals(property.getName()) && Boolean.TRUE.equals(property.getValue())))
                .build();
    }
}

