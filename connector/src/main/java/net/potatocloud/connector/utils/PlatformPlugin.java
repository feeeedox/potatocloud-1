package net.potatocloud.connector.utils;

import net.potatocloud.api.CloudAPI;
import net.potatocloud.api.service.Service;
import net.potatocloud.connector.ConnectorAPI;
import net.potatocloud.network.packet.packets.service.ServiceStartedPacket;

public interface PlatformPlugin {

    void runTaskLater(Runnable task, int delaySeconds);

    void onServiceReady(Service service);

    default void initCurrentService() {
        final CloudAPI api = CloudAPI.instance();

        if (api.serviceManager() == null) {
            runTaskLater(this::initCurrentService, 1);
            return;
        }

        final Service currentService = api.serviceManager().getCurrentService();

        if (currentService == null || currentService.getServiceGroup() == null) {
            runTaskLater(this::initCurrentService, 1);
            return;
        }

        ConnectorAPI.getInstance()
                .client()
                .send(new ServiceStartedPacket(currentService.getName()));

        onServiceReady(currentService);
    }
}
