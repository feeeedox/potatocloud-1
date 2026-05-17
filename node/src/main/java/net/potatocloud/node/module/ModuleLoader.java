package net.potatocloud.node.module;

import lombok.RequiredArgsConstructor;
import net.potatocloud.api.module.AbstractModule;
import net.potatocloud.api.module.Module;
import net.potatocloud.api.utils.version.Version;
import net.potatocloud.common.FileUtils;
import net.potatocloud.common.JacksonUtils;

import java.io.InputStream;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Path;

@RequiredArgsConstructor
public class ModuleLoader {

    private final ModuleManager moduleManager;

    public void load(Path modulesPath) {
        try {
            if (Files.notExists(modulesPath)) {
                Files.createDirectories(modulesPath);
                return;
            }

            FileUtils.list(modulesPath).stream()
                    .filter(Files::isRegularFile).filter(path -> path.toString().endsWith(".jar"))
                    .forEach(this::loadJar);

        } catch (Exception e) {
            throw new RuntimeException("Error loading modules: " + e.getMessage());
        }
    }

    private void loadJar(Path jar) {
        try {
            final URLClassLoader loader = new URLClassLoader(new URL[]{jar.toUri().toURL()}, getClass().getClassLoader());

            try (InputStream stream = loader.getResourceAsStream("module.yml")) {
                if (stream == null) {
                    return;
                }

                final ModuleConfig config = JacksonUtils.JSON_MAPPER.readValue(stream, ModuleConfig.class);

                final Class<?> clazz = Class.forName(config.mainClass(), true, loader);
                final Module module = (Module) clazz.getDeclaredConstructor().newInstance();

                if (module instanceof AbstractModule abstractModule) {
                    abstractModule.setName(config.name());
                    abstractModule.setVersion(Version.fromString(config.version()));
                }

                module.onLoad();
                moduleManager.register(module);
            }
        } catch (Exception e) {
            throw new RuntimeException("Error loading module: " + e.getMessage());
        }
    }
}
