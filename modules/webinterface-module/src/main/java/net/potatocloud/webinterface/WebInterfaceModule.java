package net.potatocloud.webinterface;

import io.javalin.Javalin;
import io.javalin.json.JavalinJackson;
import lombok.Getter;
import net.potatocloud.api.CloudAPI;
import net.potatocloud.api.module.AbstractModule;
import net.potatocloud.node.Node;
import net.potatocloud.webinterface.api.rest.*;
import net.potatocloud.webinterface.api.websocket.handler.*;
import net.potatocloud.webinterface.api.websocket.handler.group.GroupsWebSocketHandler;
import net.potatocloud.webinterface.api.websocket.session.WebSocketSessionManager;
import net.potatocloud.webinterface.config.WebInterfaceConfig;
import net.potatocloud.webinterface.dto.event.ErrorDto;
import net.potatocloud.webinterface.security.AuthService;
import net.potatocloud.webinterface.service.*;
import net.potatocloud.webinterface.service.broadcast.PlayerBroadcastService;
import net.potatocloud.webinterface.service.broadcast.ScreenLogBroadcastService;
import net.potatocloud.webinterface.service.broadcast.ServerBroadcastService;
import net.potatocloud.webinterface.service.broadcast.group.GroupDetailsBroadcastService;
import net.potatocloud.webinterface.service.broadcast.group.GroupsBroadcastService;
import net.potatocloud.webinterface.service.broadcast.stats.StatsServicesBroadcastService;

public class WebInterfaceModule extends AbstractModule {

    @Getter
    public static WebInterfaceModule instance;
    @Getter
    public CloudAPI cloudAPI;
    private Javalin app;
    private WebSocketSessionManager sessionManager;

    private GroupDetailsBroadcastService groupDetailsBroadcastService;
    private ScreenLogBroadcastService screenLogBroadcastService;
    private PlayerBroadcastService playerBroadcastService;
    private GroupsBroadcastService groupsBroadcastService;
    private StatsServicesBroadcastService statsServicesBroadcastService;
    private ServerBroadcastService serverBroadcastService;

    @Override
    public void onEnable() {
        instance = this;
        cloudAPI = CloudAPI.getInstance();
        Node node = Node.getInstance();

        WebInterfaceConfig config = WebInterfaceConfig.load();
        AuthService authService = new AuthService(config);

        NodeService nodeService = new NodeService(cloudAPI, node);
        GroupService groupService = new GroupService(cloudAPI, node);
        PlayerService playerService = new PlayerService(cloudAPI, node);
        PlatformService platformService = new PlatformService(cloudAPI);
        StatsService statsService = new StatsService(cloudAPI);
        ServerService serviceService = new ServerService(cloudAPI);

        statsService.start();

        sessionManager = new WebSocketSessionManager();
        groupDetailsBroadcastService = new GroupDetailsBroadcastService(groupService, config.getWsUpdateIntervalSeconds());
        groupDetailsBroadcastService.start();

        screenLogBroadcastService = new ScreenLogBroadcastService(node);
        screenLogBroadcastService.start();

        playerBroadcastService = new PlayerBroadcastService(cloudAPI, playerService, config.getWsUpdateIntervalSeconds());
        playerBroadcastService.registerCloudListeners();
        playerBroadcastService.start();

        groupsBroadcastService = new GroupsBroadcastService(cloudAPI, groupService, config.getWsUpdateIntervalSeconds());
        groupsBroadcastService.registerCloudListeners();
        groupsBroadcastService.start();

        statsServicesBroadcastService = new StatsServicesBroadcastService(cloudAPI, statsService, config.getWsUpdateIntervalSeconds());
        statsServicesBroadcastService.registerCloudListeners();
        statsServicesBroadcastService.start();

        serverBroadcastService = new ServerBroadcastService(cloudAPI, serviceService, config.getWsUpdateIntervalSeconds());
        serverBroadcastService.registerCloudListeners();
        serverBroadcastService.start();

        GroupRestController groupRestController = new GroupRestController(groupService);
        PlatformRestController platformRestController = new PlatformRestController(platformService);
        PlayerRestController playerRestController = new PlayerRestController(playerService);
        StatsRestController statsRestController = new StatsRestController(statsService);
        ScreenRestController screenRestController = new ScreenRestController(nodeService);

        GroupWebSocketHandler groupWebSocketHandler = new GroupWebSocketHandler(
                groupService, sessionManager, authService, config.getWsPingIntervalSeconds()
        );
        PlayerWebSocketHandler playerWebSocketHandler = new PlayerWebSocketHandler(
                playerService, sessionManager, authService, config.getWsPingIntervalSeconds()
        );
        PlayerLiveWebSocketHandler playerLiveWebSocketHandler = new PlayerLiveWebSocketHandler(
                sessionManager, playerBroadcastService, authService, config.getWsPingIntervalSeconds()
        );
        GroupDetailsWebSocketHandler groupDetailsWebSocketHandler = new GroupDetailsWebSocketHandler(
                groupService, groupDetailsBroadcastService, sessionManager, authService, config.getWsPingIntervalSeconds()
        );
        GroupsWebSocketHandler groupsWebSocketHandler = new GroupsWebSocketHandler(
                sessionManager, groupsBroadcastService, authService, config.getWsPingIntervalSeconds()
        );
        ScreenWebSocketHandler screenWebSocketHandler = new ScreenWebSocketHandler(
                nodeService, screenLogBroadcastService, sessionManager, authService, config.getWsPingIntervalSeconds()
        );
        StatsServiceWebSocketHandler statsServiceWebSocketHandler = new StatsServiceWebSocketHandler(
                statsServicesBroadcastService, sessionManager, authService, config.getWsUpdateIntervalSeconds()
        );
        ServicesWebSocketHandler servicesWebSocketHandler = new ServicesWebSocketHandler(
                serverBroadcastService, sessionManager, authService, config.getWsUpdateIntervalSeconds()
        );

        app = Javalin.create(cfg -> {
            cfg.jsonMapper(new JavalinJackson());

            cfg.routes.before("/api/*", authService::authorizeHttp);

            cfg.routes.apiBuilder(() -> {
                groupRestController.register();
                platformRestController.register();
                playerRestController.register();
                statsRestController.register();
                screenRestController.register();
            });

            cfg.routes.ws("/ws/stats/services", statsServiceWebSocketHandler::configure);
            cfg.routes.ws("/ws/services", servicesWebSocketHandler::configure);
            cfg.routes.ws("/ws/group-details", groupDetailsWebSocketHandler::configure);
            cfg.routes.ws("/ws/groups", groupsWebSocketHandler::configure);
            cfg.routes.ws("/ws/players", playerWebSocketHandler::configure);
            cfg.routes.ws("/ws/players/live", playerLiveWebSocketHandler::configure);
            cfg.routes.ws("/api/screens/{name}/live", screenWebSocketHandler::configure);

            cfg.routes.exception(Exception.class, (exception, ctx) -> {
                cloudAPI.getLogger().exception(exception);
                ctx.status(500).json(ErrorDto.builder().error("Internal server error").build());
            });
        });

        app.start(config.getBindAddress(), config.getPort());

        cloudAPI.getLogger().info("WebInterface API running on " + config.getBindAddress() + ":" + config.getPort());
    }

    @Override
    public void onDisable() {
        if (groupDetailsBroadcastService != null) {
            groupDetailsBroadcastService.shutdown();
        }

        if (screenLogBroadcastService != null) {
            screenLogBroadcastService.shutdown();
        }

        if (playerBroadcastService != null) {
            playerBroadcastService.shutdown();
        }

        if (groupsBroadcastService != null) {
            groupsBroadcastService.shutdown();
        }

        if (statsServicesBroadcastService != null) {
            statsServicesBroadcastService.shutdown();
        }

        if (sessionManager != null) {
            sessionManager.closeAll("WebInterface shutdown");
        }

        if (app != null) {
            app.stop();
        }

        cloudAPI.getLogger().info("WebInterface module stopped.");
    }
}
