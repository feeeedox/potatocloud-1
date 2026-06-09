package net.potatocloud.node.service.runtime;

import net.potatocloud.api.group.Group;
import net.potatocloud.api.logging.Logger;
import net.potatocloud.node.config.NodeConfig;
import net.potatocloud.node.service.AbstractService;
import net.potatocloud.node.service.config.ServicePerformanceFlags;
import oshi.ffm.SystemInfo;
import oshi.software.os.OSProcess;

import java.io.*;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public final class LocalServiceRuntime implements ServiceRuntime {

    private final Group group;
    private final NodeConfig config;
    private final Logger logger;

    private Process process;
    private OSProcess osProcess;
    private BufferedWriter processWriter;
    private BufferedReader processReader;
    private String serviceName;

    public LocalServiceRuntime(Group group, NodeConfig config, Logger logger) {
        this.group = group;
        this.config = config;
        this.logger = logger;
    }

    @Override
    public void start(Path directory, AbstractService service) {
        this.serviceName = service.name();
        final List<String> args = buildArguments(directory, service.name());

        try {
            process = new ProcessBuilder(args)
                    .directory(directory.toFile())
                    .start();

        } catch (IOException e) {
            throw new RuntimeException("Failed to start server process for service " + service.name(), e);
        }

        osProcess = new SystemInfo().getOperatingSystem().getProcess((int) process.pid());

        processWriter = new BufferedWriter(new OutputStreamWriter(process.getOutputStream()));
        processReader = new BufferedReader(new InputStreamReader(process.getInputStream()));

        // Start reading process logs
        Thread.startVirtualThread(() -> {
            try {
                String line;
                while (process.isAlive() && (line = processReader.readLine()) != null) {
                    service.log(line);
                }
            } catch (IOException ignored) {
            }
        });
    }

    @Override
    public void stop() {
        if (process == null) {
            return;
        }

        executeCommand("stop");

        try {
            if (!process.waitFor(config.service().killTimeout(), TimeUnit.SECONDS)) {
                logger.debug("Service &a" + serviceName + " &7did not stop in time, destroying process&8...");
                process.destroyForcibly();
                process.waitFor();
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
            process = null;
            osProcess = null;
            processWriter = null;
        }
    }

    @Override
    public boolean executeCommand(String command) {
        if (!alive() || processWriter == null) {
            return false;
        }

        try {
            processWriter.write(command);
            processWriter.newLine();
            processWriter.flush();
        } catch (IOException e) {
            logger.error("Failed to send command to process " + e.getMessage());
            return false;
        }

        return true;
    }

    @Override
    public boolean alive() {
        return process != null && process.isAlive();
    }

    @Override
    public int usedMemory() {
        if (!alive() || osProcess == null) {
            return 0;
        }
        return (int) (osProcess.getResidentMemory() / 1024 / 1024);
    }

    private List<String> buildArguments(Path directory, String name) {
        final List<String> args = new ArrayList<>();
        args.add(group.javaCommand());
        args.add("-Xms" + group.maxMemory() + "M");
        args.add("-Xmx" + group.maxMemory() + "M");
        args.add("-Dpotatocloud.service.name=" + name);
        args.add("-Dpotatocloud.node.port=" + config.node().port());

        args.addAll(ServicePerformanceFlags.DEFAULT_FLAGS);

        if (group.customJvmFlags() != null) {
            args.addAll(group.customJvmFlags());
        }

        args.add("-jar");
        args.add(directory.resolve("server.jar").toAbsolutePath().toString());

        if (group.platform().bukkitBased() && !group.platformVersion().legacy()) {
            args.add("-nogui");
        }

        if (group.platform().limboBased()) {
            args.add("--nogui");
        }

        return args;
    }
}
