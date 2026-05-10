package net.potatocloud.node.service.runtime;

import net.potatocloud.node.service.ServiceImpl;

public interface ServiceRuntime {

    void prepare(ServiceImpl service);

    void start(ServiceImpl service);

    void stop(ServiceImpl service);

    boolean executeCommand(ServiceImpl service, String command);

    boolean alive(ServiceImpl service);

    int usedMemory(ServiceImpl service);

}
