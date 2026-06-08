package net.potatocloud.webinterface.service;

import lombok.RequiredArgsConstructor;
import net.potatocloud.api.CloudAPI;
import net.potatocloud.webinterface.WebInterfaceModule;
import net.potatocloud.webinterface.dto.service.ServerOverviewDto;

import java.util.List;

@RequiredArgsConstructor
public class ServerService {

    private final CloudAPI cloudAPI;

    public List<ServerOverviewDto> getServices() {
        return cloudAPI.getServiceManager().getAllServices().stream()
                .map(ServerOverviewDto::from)
                .toList();
    }

    public void stopService(String serviceId) {
        cloudAPI.getServiceManager().stopService(serviceId);

        WebInterfaceModule.getInstance().

    }

    public ServerOverviewDto getService(String name) {
        return ServerOverviewDto.from(cloudAPI.getServiceManager().getService(name));
    }
}

