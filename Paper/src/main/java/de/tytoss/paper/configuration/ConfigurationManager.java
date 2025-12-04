package de.tytoss.paper.configuration;

import org.bukkit.plugin.java.JavaPlugin;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.loader.ConfigurationLoader;
import org.spongepowered.configurate.yaml.YamlConfigurationLoader;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

public class ConfigurationManager {

    private final JavaPlugin plugin;
    private final Path dataDirectory;
    private final Path configFile;

    private ConfigurationNode config;
    private ConfigurationLoader<?> configLoader;

    public ConfigurationManager(JavaPlugin plugin) {
        this.plugin = plugin;
        this.dataDirectory = plugin.getDataFolder().toPath();
        this.configFile = dataDirectory.resolve("config.yml");
    }

    public void load() throws IOException {
        if (!Files.exists(dataDirectory)) {
            Files.createDirectories(dataDirectory);
        }

        if (!Files.exists(configFile)) {
            try (InputStream in = plugin.getResource("config.yml")) {
                if (in == null) {
                    throw new IllegalStateException("Default config.yml not found in resources!");
                }
                Files.copy(in, configFile);
            }
        }

        this.configLoader = YamlConfigurationLoader.builder()
                .path(configFile)
                .build();

        this.config = configLoader.load();
    }

    public ConfigurationNode get() {
        return config;
    }
}

