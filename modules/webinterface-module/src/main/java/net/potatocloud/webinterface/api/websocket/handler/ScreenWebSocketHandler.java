package net.potatocloud.webinterface.api.websocket.handler;

import io.javalin.websocket.WsContext;
import net.potatocloud.webinterface.api.websocket.session.WebSocketSessionManager;
import net.potatocloud.webinterface.security.AuthService;
import net.potatocloud.webinterface.service.NodeService;
import net.potatocloud.webinterface.service.broadcast.ScreenLogBroadcastService;

public class ScreenWebSocketHandler extends BaseWebSocketHandler {

    private final NodeService nodeService;
    private final ScreenLogBroadcastService screenLogBroadcastService;

    public ScreenWebSocketHandler(
            NodeService nodeService,
            ScreenLogBroadcastService screenLogBroadcastService,
            WebSocketSessionManager sessionManager,
            AuthService authService,
            int pingIntervalSeconds
    ) {
        super(sessionManager, authService, pingIntervalSeconds);
        this.nodeService = nodeService;
        this.screenLogBroadcastService = screenLogBroadcastService;
    }

    @Override
    protected void onConnect(WsContext ctx) {
        String name = ctx.pathParam("name");
        if (nodeService.getScreenLogs(name, 1) == null) {
            ctx.closeSession(4404, "Screen not found: " + name);
            return;
        }

        screenLogBroadcastService.register(name, ctx);
    }

    @Override
    protected void onMessage(WsContext ctx, String message) {
        String screenName = ctx.pathParam("name");
        String command = message.trim();
        if (command.isEmpty()) {
            return;
        }

        nodeService.executeCommandOnService(screenName, command);
    }

    @Override
    protected void onClose(WsContext ctx) {
        screenLogBroadcastService.unregister(ctx.pathParam("name"), ctx);
    }

    @Override
    protected void onError(WsContext ctx, Throwable error) {
        screenLogBroadcastService.unregister(ctx.pathParam("name"), ctx);
    }
}

