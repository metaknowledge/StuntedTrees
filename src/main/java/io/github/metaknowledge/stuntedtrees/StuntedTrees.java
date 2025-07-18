package io.github.metaknowledge.stuntedtrees;

import org.bukkit.*;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.java.JavaPlugin;

public final class StuntedTrees extends JavaPlugin {
    public final ConfigurationSection trees = getConfig().getConfigurationSection("trees");

    @Override
    public void onEnable() {
        // Plugin startup logic
        Bukkit.getPluginManager().registerEvents(new StuntedTreesListener(trees), this);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
