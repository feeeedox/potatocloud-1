package net.potatocloud.node.utils;

import net.potatocloud.api.group.ServiceGroup;
import net.potatocloud.api.property.DefaultProperties;
import net.potatocloud.api.property.Property;
import net.potatocloud.node.Node;

import java.util.List;

public final class ProxyUtils {

    private ProxyUtils() {
    }

    public static ServiceGroup getProxyGroup() {
        return getProxyGroups().stream().findFirst().orElse(null);
    }

    public static List<ServiceGroup> getProxyGroups() {
        return Node.getInstance().groupManager().groups().stream().filter(group -> group.platform().isProxy()).toList();
    }

    public static boolean isProxyModernForwarding() {
        if (getProxyGroup() == null) {
            return false;
        }

        final Property<Boolean> property = getProxyGroup().getProperty(DefaultProperties.VELOCITY_MODERN_FORWARDING);
        if (property == null) {
            return false;
        }

        return property.getValue();
    }
}
