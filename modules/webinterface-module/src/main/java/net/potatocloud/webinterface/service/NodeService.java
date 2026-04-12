package net.potatocloud.webinterface.service;

import lombok.RequiredArgsConstructor;
import net.potatocloud.api.CloudAPI;
import net.potatocloud.node.Node;
import net.potatocloud.node.screen.Screen;
import net.potatocloud.webinterface.dto.screen.ScreenLogsDto;

import java.util.List;

@RequiredArgsConstructor
public class NodeService {

    private final CloudAPI cloudAPI;
    private final Node node;

    public long getNodeUptime() {
        return node.getUptime();
    }

    public boolean isNodeReady() {
        return node.isReady();
    }

    public List<String> getScreens() {
        return node.getScreenManager().getScreens().keySet().stream().sorted().toList();
    }

    public ScreenLogsDto getScreenLogs(String screenName, int tail) {
        Screen screen = node.getScreenManager().get(screenName);
        if (screen == null) {
            return null;
        }

        List<String> logs = screen.cachedLogs();
        if (tail > 0 && tail < logs.size()) {
            logs = logs.subList(logs.size() - tail, logs.size());
        }

        return ScreenLogsDto.builder().screen(screenName).logs(logs).build();
    }

    public boolean executeCommandOnService(String serviceName, String command) {
        return cloudAPI.getServiceManager().getAllServices().stream()
                .filter(service -> service.getName().equals(serviceName))
                .findFirst()
                .map(service -> service.executeCommand(command))
                .orElse(false);
    }
}

