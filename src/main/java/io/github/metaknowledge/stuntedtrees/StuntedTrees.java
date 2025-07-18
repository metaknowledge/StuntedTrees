package io.github.metaknowledge.stuntedtrees;

import io.papermc.paper.datapack.Datapack;
import org.bukkit.*;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.FileUtil;
import org.eclipse.aether.util.FileUtils;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public final class StuntedTrees extends JavaPlugin {
    public final ConfigurationSection trees = getConfig().getConfigurationSection("trees");

    @Override
    public void onEnable() {
        // Plugin startup logic
        getLogger().info(Bukkit.getDatapackManager().getPacks().toString());
        if (!checkForMinecraftDatapack("stunted_trees_datapack")) {
            getLogger().severe("Could not find stunted_trees_datapack, please added it to your datapacks folder");
        }
        Bukkit.getPluginManager().registerEvents(new StuntedTreesListener(trees), this);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    private boolean checkForMinecraftDatapack(String name) {
        boolean found_datapack = false;
        Bukkit.getWorlds().forEach(world -> getLogger().info(world.getWorldFolder().toString()));
        for (World world : Bukkit.getWorlds()) {
            if (Files.isDirectory(Paths.get(world.getWorldFolder() + "/datapacks/" + name))) {
                found_datapack = true;
            }
        }

        return found_datapack;
    }
}
