package io.github.metaknowledge.stuntedtrees;

import org.bukkit.*;
import org.bukkit.block.Biome;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.StructureGrowEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;

public class StuntedTreesListener implements Listener {
    ConfigurationSection trees;

    public StuntedTreesListener(ConfigurationSection trees) {
        this.trees = trees;
    }

    public GrowthAbility checkGrowthAbility(Biome biome, @NotNull TreeType tree) {
        if (trees == null || trees.getConfigurationSection(tree.toString()) == null) {
            return GrowthAbility.FULL_GROWTH;
        }
        String tree_type = tree.toString();
        List<String> fullGrowthBiomes = (List<String>) trees.getConfigurationSection(tree_type).getList("full-growth");
        if (fullGrowthBiomes != null) {
            for (String b : fullGrowthBiomes) {
                if (biome.toString().equals(b)) {
                    return GrowthAbility.FULL_GROWTH;
                }
            }
        }
        List<String> stuntedBiome = (List<String>) trees.getConfigurationSection(tree.toString()).getList("stunted");
        if (stuntedBiome != null) {
            for (String b : stuntedBiome) {
                if (biome.toString().equals(b)) {
                    return GrowthAbility.STUNTED;
                }
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

        if (location.getWorld().getEnvironment().equals(World.Environment.NETHER)) {
            e.setCancelled(true);
            location.getBlock().setType(Material.DEAD_BUSH);
        }
        if (trees.contains(e.getSpecies().toString()) && trees.getConfigurationSection(e.getSpecies().toString()).getBoolean("enabled")) {
            GrowthAbility growthAbility = checkGrowthAbility(biome, e.getSpecies());
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
