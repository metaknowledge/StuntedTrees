package io.github.metaknowledge.stuntedtrees;

import org.bukkit.*;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;

public final class StuntedTrees extends JavaPlugin {
    @Override
    public void onEnable() {
        // Plugin startup logic
        StuntedTreesConfig config = new StuntedTreesConfig();
        Bukkit.getPluginManager().registerEvents(new StuntedTreesListener(config), this);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
