package net.potatocloud.network.netty;

import io.netty.buffer.ByteBuf;
import net.potatocloud.api.cluster.ClusterNode;
import net.potatocloud.api.cluster.impl.AbstractClusterNode;
import net.potatocloud.api.group.ServiceGroup;
import net.potatocloud.api.group.impl.ServiceGroupImpl;
import net.potatocloud.api.platform.Platform;
import net.potatocloud.api.platform.PlatformVersion;
import net.potatocloud.api.platform.impl.PlatformImpl;
import net.potatocloud.api.platform.impl.PlatformVersionImpl;
import net.potatocloud.api.player.CloudPlayer;
import net.potatocloud.api.player.impl.CloudPlayerImpl;
import net.potatocloud.api.property.Property;
import net.potatocloud.api.service.Service;
import net.potatocloud.api.service.ServiceStatus;
import net.potatocloud.api.service.impl.ServiceImpl;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class PacketBuffer {

    private final ByteBuf buf;

    public PacketBuffer(ByteBuf buf) {
        this.buf = buf;
    }

    public void writeString(String string) {
        if (string == null) {
            buf.writeInt(-1);
            return;
        }
        final byte[] bytes = string.getBytes(StandardCharsets.UTF_8);
        buf.writeInt(bytes.length);
        buf.writeBytes(bytes);
    }

    public String readString() {
        final int length = buf.readInt();
        if (length == -1) {
            return null;
        }
        final byte[] bytes = new byte[length];
        buf.readBytes(bytes);
        return new String(bytes, StandardCharsets.UTF_8);
    }

    public void writeInt(int value) {
        buf.writeInt(value);
    }

    public int readInt() {
        return buf.readInt();
    }

    public void writeBoolean(boolean bool) {
        buf.writeBoolean(bool);
    }

    public boolean readBoolean() {
        return buf.readBoolean();
    }

    public void writeStringList(List<String> list) {
        if (list == null) {
            writeInt(-1);
            return;
        }
        writeInt(list.size());
        for (String item : list) {
            writeString(item);
        }
    }

    public List<String> readStringList() {
        final int size = readInt();
        if (size == -1) {
            return new ArrayList<>();
        }
        final List<String> list = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            list.add(readString());
        }
        return list;
    }

    public void writeObject(Object object) {
        switch (object) {
            case String string -> {
                buf.writeByte(1);
                writeString(string);
            }
            case Integer integer -> {
                buf.writeByte(2);
                writeInt(integer);
            }
            case Boolean bool -> {
                buf.writeByte(3);
                writeBoolean(bool);
            }
            case Long l -> {
                buf.writeByte(4);
                writeLong(l);
            }
            case Float f -> {
                buf.writeByte(5);
                writeFloat(f);
            }
            case Double d -> {
                buf.writeByte(6);
                writeDouble(d);
            }
            case null, default -> throw new IllegalArgumentException("Unsupported object: " + object.getClass());
        }
    }

    public Object readObject() {
        final byte type = buf.readByte();
        return switch (type) {
            case 1 -> readString();
            case 2 -> readInt();
            case 3 -> readBoolean();
            case 4 -> readLong();
            case 5 -> readFloat();
            case 6 -> readDouble();
            default -> throw new IllegalArgumentException("Unknown object id: " + type);
        };
    }

    public <T> void writeProperty(Property<T> property) {
        writeString(property.getName());
        writeObject(property.getDefaultValue());
        writeObject(property.getValue());
    }

    public Property<?> readProperty() {
        final String name = readString();
        final Object defaultValue = readObject();
        final Object value = readObject();

        return Property.of(name, defaultValue, value);
    }

    public void writePropertyMap(Map<String, Property<?>> propertyMap) {
        if (propertyMap == null) {
            writeInt(-1);
            return;
        }
        writeInt(propertyMap.size());
        for (Property<?> prop : propertyMap.values()) {
            writeProperty(prop);
        }
    }

    public Map<String, Property<?>> readPropertyMap() {
        final int size = readInt();
        if (size == -1) {
            return new HashMap<>();
        }
        final Map<String, Property<?>> map = new HashMap<>(size);
        for (int i = 0; i < size; i++) {
            final Property<?> property = readProperty();

            map.put(property.getName(), property);
        }
        return map;
    }

    public void writeClusterNode(ClusterNode node) {
        writeUUID(node.id());
        writeString(node.name());
        writeString(node.host());
        writeInt(node.port());
        writeLong(node.startedAt());
    }

    public ClusterNode readClusterNode() {
        return new AbstractClusterNode(readUUID(), readString(), readString(), readInt(), readLong());
    }

    public void writeClusterNodeList(Collection<? extends ClusterNode> nodes) {
        writeInt(nodes.size());
        for (ClusterNode node : nodes) {
            writeClusterNode(node);
        }
    }

    public List<ClusterNode> readClusterNodeList() {
        final int size = readInt();
        final List<ClusterNode> list = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            list.add(readClusterNode());
        }
        return list;
    }

    public void writeUUID(UUID uuid) {
        buf.writeLong(uuid.getMostSignificantBits());
        buf.writeLong(uuid.getLeastSignificantBits());
    }

    public UUID readUUID() {
        return new UUID(buf.readLong(), buf.readLong());
    }

    public void writeLong(long value) {
        buf.writeLong(value);
    }

    public long readLong() {
        return buf.readLong();
    }

    public void writeFloat(float value) {
        buf.writeFloat(value);
    }

    public void writeDouble(double value) {
        buf.writeDouble(value);
    }

    public float readFloat() {
        return buf.readFloat();
    }

    public double readDouble() {
        return buf.readDouble();
    }

    public void writeServiceGroup(ServiceGroup group) {
        writeString(group.getName());
        writeString(group.getPlatformName());
        writeString(group.getPlatformVersionName());
        writeString(group.getJavaCommand());
        writeStringList(group.getCustomJvmFlags());
        writeInt(group.getMaxPlayers());
        writeInt(group.getMaxMemory());
        writeInt(group.getMinOnlineCount());
        writeInt(group.getMaxOnlineCount());
        writeBoolean(group.isStatic());
        writeBoolean(group.isFallback());
        writeInt(group.getStartPriority());
        writeInt(group.getStartPercentage());
        writeStringList(group.getServiceTemplates());
        writePropertyMap(group.getPropertyMap());
    }

    public ServiceGroup readServiceGroup() {
        return new ServiceGroupImpl(
                readString(),
                readString(),
                readString(),
                readString(),
                readStringList(),
                readInt(),
                readInt(),
                readInt(),
                readInt(),
                readBoolean(),
                readBoolean(),
                readInt(),
                readInt(),
                readStringList(),
                readPropertyMap()
        );
    }

    public void writeService(Service service) {
        writeInt(service.getServiceId());
        writeInt(service.getPort());
        writeString(service.getName());
        writeString(service.getServiceGroup().getName());
        writePropertyMap(service.getPropertyMap());
        writeLong(service.getStartTimestamp());
        writeString(service.getStatus().name());
        writeInt(service.getMaxPlayers());
        writeInt(service.getUsedMemory());
    }

    public Service readService() {
        return new ServiceImpl(
                readInt(),
                readInt(),
                readString(),
                readString(),
                readPropertyMap(),
                readLong(),
                ServiceStatus.valueOf(readString()),
                readInt(),
                readInt()
        );
    }

    public void writeCloudPlayer(CloudPlayer player) {
        writeString(player.getUsername());
        writeUUID(player.getUniqueId());
        writeString(player.getConnectedProxyName());
        writeString(player.getConnectedServiceName());
        writePropertyMap(player.getPropertyMap());
    }

    public CloudPlayer readCloudPlayer() {
        return new CloudPlayerImpl(
                readString(),
                readUUID(),
                readString(),
                readString(),
                readPropertyMap()
        );
    }

    public void writePlatform(Platform platform) {
        writeString(platform.getName());
        writeString(platform.getDownloadUrl());
        writeBoolean(platform.isCustom());
        writeBoolean(platform.isProxy());
        writeString(platform.getBase());
        writeString(platform.getPreCacheBuilder());
        writeString(platform.getParser());
        writeString(platform.getHashType());
        writeStringList(platform.getPrepareSteps());

        writeInt(platform.getVersions().size());
        for (PlatformVersion version : platform.getVersions()) {
            writeString(version.getPlatformName());
            writeString(version.getName());
            writeBoolean(version.isLocal());
            writeString(version.getDownloadUrl());
            writeString(version.getFileHash());
            writeBoolean(version.isLegacy());
        }
    }

    public Platform readPlatform() {
        final String name = readString();
        final String downloadUrl = readString();
        final boolean custom = readBoolean();
        final boolean isProxy = readBoolean();
        final String base = readString();
        final String preCacheBuilder = readString();
        final String parser = readString();
        final String hashType = readString();
        final List<String> prepareSteps = readStringList();

        final PlatformImpl platform = new PlatformImpl(
                name, downloadUrl, custom, isProxy, base, preCacheBuilder, parser, hashType, prepareSteps);

        final int versionCount = readInt();
        for (int i = 0; i < versionCount; i++) {
            final String platformName = readString();
            final String versionName = readString();
            final boolean local = readBoolean();
            final String versionDownloadUrl = readString();
            final String fileHash = readString();
            final boolean legacy = readBoolean();

            final PlatformVersion version = new PlatformVersionImpl(
                    platformName, versionName, local, versionDownloadUrl, fileHash, legacy);
            platform.getVersions().add(version);
        }

        return platform;
    }
}
