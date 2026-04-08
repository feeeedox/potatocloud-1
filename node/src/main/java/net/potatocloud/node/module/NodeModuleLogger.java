package net.potatocloud.node.module;

import net.potatocloud.api.module.ModuleLogger;
import net.potatocloud.node.console.Logger;

public class NodeModuleLogger implements ModuleLogger {

    private final Logger logger;

    public NodeModuleLogger(Logger logger) {
        this.logger = logger;
    }

    @Override
    public void info(String message) {
        logger.info(message);
    }

    @Override
    public void warn(String message) {
        logger.warn(message);
    }

    @Override
    public void error(String message) {
        logger.error(message);
    }
}