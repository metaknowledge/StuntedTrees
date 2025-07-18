package io.github.metaknowledge.stuntedtrees;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.java.JavaPlugin;

public final class StuntedTreesConfig extends JavaPlugin {
    public final ConfigurationSection trees = getConfig().getConfigurationSection("trees");
}

