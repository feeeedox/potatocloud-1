package net.potatocloud.webinterface.service;

import lombok.RequiredArgsConstructor;
import net.potatocloud.api.CloudAPI;
import net.potatocloud.api.group.Group;
import net.potatocloud.api.property.Property;
import net.potatocloud.api.service.Service;
import net.potatocloud.node.Node;
import net.potatocloud.webinterface.dto.group.CreateGroupRequestDto;
import net.potatocloud.webinterface.dto.group.GroupDto;
import net.potatocloud.webinterface.dto.group.PropertyDto;
import net.potatocloud.webinterface.dto.group.UpdateGroupRequestDto;

import java.util.*;

@RequiredArgsConstructor
public class GroupService {

    private final CloudAPI cloudAPI;
    private final Node node;

    public boolean exists(String name) {
        return cloudAPI.groupManager().exists(name);
    }

    public List<GroupDto> getAllGroups() {
        return cloudAPI.groupManager().groups().stream()
                .map(this::toDto)
                .toList();
    }

    public GroupDto getGroupByName(String groupName) {
        Optional<Group> group = cloudAPI.groupManager().find(groupName);
        return group.map(this::toDto).orElse(null);
    }

    public boolean updateGroup(UpdateGroupRequestDto request) {
        if (request == null || request.getName() == null || request.getName().isBlank()) {
            Node.getInstance().logger().error(request.toString());
            return false;
        }

        Optional<Group> existingGroup = cloudAPI.groupManager().find(request.getName());
        if (existingGroup.isEmpty()) {
            Node.getInstance().logger().error("HALLO ERROR HILFE 2");
            return false;
        }

        existingGroup.ifPresent(group -> {
            group.customJvmFlags().clear();
            if (request.getCustomJvmFlags() != null) {
                group.customJvmFlags().addAll(request.getCustomJvmFlags());
            }

            group.maxPlayers(request.getMaxPlayerCount());
            group.maxMemory(request.getMaxMemory());
            group.minServices(request.getMinOnlineCount());
            group.maxServices(request.getMaxOnlineCount());
            group.fallback(request.isFallback());
            group.startPriority(request.getStartPriority());
            group.startPercentage(request.getNewServicePercent());

            group.templates().clear();
            if (request.getServiceTemplates() != null) {
                group.templates().addAll(request.getServiceTemplates());
            }

            group.propertyMap().clear();

            if (request.getProperties() != null) {
                for (PropertyDto propertyDto : request.getProperties()) {
                    group.propertyMap().put(
                            propertyDto.name(),
                            new Property<>(propertyDto.name(), propertyDto.defaultValue(), propertyDto.value())
                    );
                }
            }

            if (request.isUseModernVelocityForwarding()) {
                group.propertyMap().put("velocityModernForwarding", new Property<>("velocityModernForwarding", false, true));
            }

            cloudAPI.groupManager().update(group);
        });

        return true;
    }

    public boolean createGroup(CreateGroupRequestDto request) {
        if (request == null || request.getName() == null || request.getName().isBlank()) {
            return false;
        }

        if (cloudAPI.groupManager().exists(request.getName())) {
            return false;
        }

        HashMap<String, Property<?>> generatedProperties = new HashMap<>();
        if (request.isUseModernVelocityForwarding()) {
            generatedProperties.put("velocityModernForwarding", new Property<>("velocityModernForwarding", false, true));
        }

        String startCommand = defaultIfBlank(request.getStartCommand(), "java");

        Set<String> customJvmFlags = new HashSet<>();
        if (request.getCustomJvmFlags() != null) {
            customJvmFlags.addAll(request.getCustomJvmFlags());
        }

        Map<String, Property<?>> customProperties = new HashMap<>(generatedProperties);
        if (request.getProperties() != null) {
            for (PropertyDto propertyDto : request.getProperties()) {
                customProperties.put(
                        propertyDto.name(),
                        new Property<>(propertyDto.name(), propertyDto.defaultValue(), propertyDto.value())
                );
            }
        }

        Group group = cloudAPI.groupManager().builder(request.getName())
                .node(Node.getInstance().config().cluster().name())
                .platform(request.getPlatform())
                .platformVersion(request.getPlatformVersion())
                .minServices(request.getMinOnlineCount())
                .maxServices(request.getMaxOnlineCount())
                .maxMemory(request.getMaxMemory())
                .fallback(request.isFallback())
                .staticServices(request.isStatic())
                .startPriority(request.getStartPriority())
                .startPercentage(request.getNewServicePercent())
                .javaCommand(startCommand)
                .properties(customProperties)
                .customJvmFlags(customJvmFlags)
                .build();


        cloudAPI.groupManager().create(
                group
        );

        return true;
    }

    public boolean startGroup(String groupName) {
        Optional<Group> group = cloudAPI.groupManager().find(groupName);
        if (group.isEmpty()) {
            return false;
        }
        cloudAPI.serviceManager().start(group.get());
        return true;
    }

    public boolean stopAllInGroup(String groupName) {
        Optional<Group> group = cloudAPI.groupManager().find(groupName);
        if (group.isEmpty()) {
            return false;
        }

        for (Service service : group.get().services()) {
            cloudAPI.serviceManager().stop(service);
        }
        return true;
    }

    private GroupDto toDto(Group group) {
        return GroupDto.from(group, node.ready());
    }

    private String defaultIfBlank(String value, String fallback) {
        return value == null || value.isBlank() ? fallback : value;
    }
}

