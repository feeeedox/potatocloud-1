package net.potatocloud.webinterface.service;

import lombok.RequiredArgsConstructor;
import net.potatocloud.api.CloudAPI;
import net.potatocloud.api.group.ServiceGroup;
import net.potatocloud.api.property.Property;
import net.potatocloud.api.service.Service;
import net.potatocloud.node.Node;
import net.potatocloud.webinterface.dto.group.CreateGroupRequestDto;
import net.potatocloud.webinterface.dto.group.GroupDto;
import net.potatocloud.webinterface.dto.group.PropertyDto;
import net.potatocloud.webinterface.dto.group.UpdateGroupRequestDto;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
public class GroupService {

    private final CloudAPI cloudAPI;
    private final Node node;

    public boolean exists(String name) {
        return cloudAPI.getServiceGroupManager().existsServiceGroup(name);
    }

    public List<GroupDto> getAllGroups() {
        return cloudAPI.getServiceGroupManager().getAllServiceGroups().stream()
                .map(this::toDto)
                .toList();
    }

    public GroupDto getGroupByName(String groupName) {
        ServiceGroup group = cloudAPI.getServiceGroupManager().getServiceGroup(groupName);
        return group == null ? null : toDto(group);
    }

    public boolean updateGroup(UpdateGroupRequestDto request) {
        if (request == null || request.getName() == null || request.getName().isBlank()) {
            Node.getInstance().getLogger().error(request.toString());
            return false;
        }

        ServiceGroup existingGroup = cloudAPI.getServiceGroupManager().getServiceGroup(request.getName());
        if (existingGroup == null) {
            Node.getInstance().getLogger().error("HALLO ERROR HILFE 2");
            return false;
        }

        existingGroup.getCustomJvmFlags().clear();
        if (request.getCustomJvmFlags() != null) {
            existingGroup.getCustomJvmFlags().addAll(request.getCustomJvmFlags());
        }

        existingGroup.setMaxPlayers(request.getMaxPlayerCount());
        existingGroup.setMaxMemory(request.getMaxMemory());
        existingGroup.setMinOnlineCount(request.getMinOnlineCount());
        existingGroup.setMaxOnlineCount(request.getMaxOnlineCount());
        existingGroup.setFallback(request.isFallback());
        existingGroup.setStartPriority(request.getStartPriority());
        existingGroup.setStartPercentage(request.getNewServicePercent());

        existingGroup.getServiceTemplates().clear();
        if (request.getServiceTemplates() != null) {
            existingGroup.getServiceTemplates().addAll(request.getServiceTemplates());
        }

        existingGroup.getPropertyMap().clear();

        if (request.getProperties() != null) {
            for (PropertyDto propertyDto : request.getProperties()) {
                existingGroup.getPropertyMap().put(
                        propertyDto.name(),
                        new Property<>(propertyDto.name(), propertyDto.defaultValue(), propertyDto.value())
                );
            }
        }

        if (request.isUseModernVelocityForwarding()) {
            existingGroup.getPropertyMap().put("velocityModernForwarding", new Property<>("velocityModernForwarding", false, true));
        }

        cloudAPI.getServiceGroupManager().updateServiceGroup(existingGroup);

        return true;
    }

    public boolean createGroup(CreateGroupRequestDto request) {
        if (request == null || request.getName() == null || request.getName().isBlank()) {
            return false;
        }

        if (cloudAPI.getServiceGroupManager().existsServiceGroup(request.getName())) {
            return false;
        }

        HashMap<String, Property<?>> generatedProperties = new HashMap<>();
        if (request.isUseModernVelocityForwarding()) {
            generatedProperties.put("velocityModernForwarding", new Property<>("velocityModernForwarding", false, true));
        }

        String startCommand = defaultIfBlank(request.getStartCommand(), "java");
        List<String> customJvmFlags = request.getCustomJvmFlags() == null ? new ArrayList<>() : new ArrayList<>(request.getCustomJvmFlags());

        Map<String, Property<?>> customProperties = new HashMap<>(generatedProperties);
        if (request.getProperties() != null) {
            for (PropertyDto propertyDto : request.getProperties()) {
                customProperties.put(
                        propertyDto.name(),
                        new Property<>(propertyDto.name(), propertyDto.defaultValue(), propertyDto.value())
                );
            }
        }

        cloudAPI.getServiceGroupManager().createServiceGroup(
                request.getName(),
                request.getPlatform(),
                request.getPlatformVersion(),
                request.getMinOnlineCount(),
                request.getMaxOnlineCount(),
                request.getMaxPlayerCount(),
                request.getMaxMemory(),
                request.isFallback(),
                request.isStatic(),
                request.getStartPriority(),
                request.getNewServicePercent(),
                startCommand,
                customJvmFlags,
                customProperties
        );

        return true;
    }

    public boolean startGroup(String groupName) {
        ServiceGroup group = cloudAPI.getServiceGroupManager().getServiceGroup(groupName);
        if (group == null) {
            return false;
        }
        cloudAPI.getServiceManager().startService(group);
        return true;
    }

    public boolean stopAllInGroup(String groupName) {
        ServiceGroup group = cloudAPI.getServiceGroupManager().getServiceGroup(groupName);
        if (group == null) {
            return false;
        }

        for (Service service : group.getOnlineServices()) {
            service.shutdown();
        }
        return true;
    }

    private GroupDto toDto(ServiceGroup group) {
        return GroupDto.from(group, node.isReady());
    }

    private String defaultIfBlank(String value, String fallback) {
        return value == null || value.isBlank() ? fallback : value;
    }
}

