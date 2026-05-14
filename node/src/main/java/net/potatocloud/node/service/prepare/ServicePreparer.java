package net.potatocloud.node.service.prepare;

import java.nio.file.Path;

public interface ServicePreparer {

    void prepare(Path directory, String serviceName, int port);

}
