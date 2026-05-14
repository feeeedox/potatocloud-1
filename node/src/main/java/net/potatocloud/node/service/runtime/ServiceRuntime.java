package net.potatocloud.node.service.runtime;

import net.potatocloud.node.service.AbstractService;

import java.nio.file.Path;

public interface ServiceRuntime {

    void start(Path directory, AbstractService service);

    void stop();

    boolean executeCommand(String command);

    boolean alive();

    int usedMemory();

}
