package io.github.metaknowledge.stuntedtrees;

import org.bukkit.*;
import org.bukkit.block.Biome;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.StructureGrowEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public final class StuntedTrees extends JavaPlugin implements Listener {
    private final ConfigurationSection structures = getConfig().getConfigurationSection("structures");


    @Override
    public void onEnable() {
        // Plugin startup logic
        Bukkit.getPluginManager().registerEvents(this, this);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public void spawnTree(World world, @NotNull Location location, String structureId) {
    }

    public GrowthAbility checkGrowthAbility(Biome biome, @NotNull TreeType tree) {
        getLogger().info("Tree grew " + tree + " in biome " + biome);
        if (structures == null || structures.getConfigurationSection(tree.toString()) == null) {
            getLogger().warning("could not find structures or config for" + tree);
            return GrowthAbility.FULL_GROWTH;
        }
        String tree_type = tree.toString();
        List<String> fullGrowthBiomes = (List<String>) structures.getConfigurationSection(tree_type).getList("full-growth");
        if (fullGrowthBiomes != null) {
            for (String b : fullGrowthBiomes) {
                if (biome.toString().equals(b)) {
                    return GrowthAbility.FULL_GROWTH;
                }
            }
        }
        List<String> stuntedBiome = (List<String>) structures.getConfigurationSection(tree.toString()).getList("stunted");
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
        if (structures == null) {
            getLogger().warning("could not find structures");
            return;
        }

        if (location.getWorld().getEnvironment().equals(World.Environment.NETHER)) {
            e.setCancelled(true);
            location.getBlock().setType(Material.DEAD_BUSH);
        }
        if (structures.contains(e.getSpecies().toString()) && structures.getConfigurationSection(e.getSpecies().toString()).getBoolean("enabled")) {
            GrowthAbility growthAbility = checkGrowthAbility(biome, e.getSpecies());
            switch (growthAbility) {
                case DEAD -> {
                    e.setCancelled(true);
                    location.getBlock().setType(Material.DEAD_BUSH);
                }
                case STUNTED -> {

                    e.setCancelled(true);
                }
            }
        }
    }
}
