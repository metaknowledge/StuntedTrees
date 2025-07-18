package io.github.metaknowledge.stuntedtrees;

import org.bukkit.*;
import org.bukkit.block.Biome;
import org.bukkit.block.BlockState;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.StructureGrowEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class StuntedTreesListener implements Listener {
    ConfigurationSection trees;

    public StuntedTreesListener(ConfigurationSection trees) {
        this.trees = trees;
    }

    public GrowthAbility checkGrowthAbility(Biome biome, @NotNull TreeType tree, World.Environment environment) {
        String tree_type = tree.toString();
        if (trees == null) {
            Bukkit.getLogger().severe("config null");
            return GrowthAbility.FULL_GROWTH;
        }
        ConfigurationSection tree_config = trees.getConfigurationSection(tree_type);
        if (tree_config == null) {
            Bukkit.getLogger().severe("could not find tree definition");
            return GrowthAbility.FULL_GROWTH;
        }
        String redirect = tree_config.getString("see");
        if (redirect != null) {
            tree_config = trees.getConfigurationSection(redirect);
            Bukkit.getLogger().info("changing definition to " + redirect);
            if (tree_config == null) {
                Bukkit.getLogger().severe("other config null");
                return GrowthAbility.FULL_GROWTH;
            }
        }
//        Bukkit.getLogger().info(tree_config.toString());
//        Bukkit.getLogger().info(tree_config.getString("world"));
//        Bukkit.getLogger().info(environment.toString());
        if (!environment.toString().equals(tree_config.getString("world"))) {
            return GrowthAbility.DEAD;
        }
        List<String> fullGrowthBiomes = tree_config.getStringList("full-growth");
        for (String b : fullGrowthBiomes) {
            if (biome.toString().equals(b)) {
                Bukkit.getLogger().severe("worked");

                return GrowthAbility.FULL_GROWTH;
            }
        }
        List<String> stuntedBiome = tree_config.getStringList("stunted");
        for (String b : stuntedBiome) {
            if (biome.toString().equals(b)) {
                return GrowthAbility.STUNTED;
            }
        }
        return GrowthAbility.DEAD;
    }

    @EventHandler
    public void onSaplingGrow(StructureGrowEvent e) {
        Location location = e.getLocation();
        Biome biome = location.getBlock().getBiome();
        if (trees == null) {
            return;
        }
        if (trees.isConfigurationSection(e.getSpecies().toString()) && trees.getConfigurationSection(e.getSpecies().toString()).getBoolean("enabled")) {
            GrowthAbility growthAbility = checkGrowthAbility(biome, e.getSpecies(), location.getWorld().getEnvironment());
            switch (growthAbility) {
                case DEAD -> {
                    e.setCancelled(true);
                    location.getBlock().setType(Material.DEAD_BUSH);
                }
                case STUNTED -> {
                    location.getBlock().setType(Material.AIR);
                    try {
                        Bukkit.dispatchCommand(Objects.requireNonNull(Bukkit.getPlayer("pap3rn")), "place feature stunted_trees_datapack:" + e.getSpecies().toString().toLowerCase() + " " + locationToString(location));
                    } catch (RuntimeException a) {
                        Bukkit.getLogger().severe("could not place feature after trying to use command:");
                        Bukkit.getLogger().info("place feature stunted_trees_datapack:" + e.getSpecies().toString().toLowerCase() + " " + locationToString(location));
                    }
                    e.setCancelled(true);
                }
            }
        }
    }

    public String locationToString(Location loc) {
        return ((int) loc.x()) + " " + ((int) loc.y()) + " " + ((int) loc.z());
    }

}
