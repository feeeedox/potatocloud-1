package net.potatocloud.node.service.start.rule;

import net.potatocloud.api.group.ServiceGroup;

public interface ServiceStartRule {

    boolean allows(ServiceGroup group);

}
