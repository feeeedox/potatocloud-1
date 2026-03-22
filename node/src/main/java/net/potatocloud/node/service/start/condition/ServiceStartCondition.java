package net.potatocloud.node.service.start.condition;

import net.potatocloud.api.group.ServiceGroup;

public interface ServiceStartCondition {

    boolean shouldStart(ServiceGroup group);

}
