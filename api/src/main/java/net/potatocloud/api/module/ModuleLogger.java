package net.potatocloud.api.module;

public interface ModuleLogger {

    void info(String message);

    void warn(String message);

    void error(String message);
    
}