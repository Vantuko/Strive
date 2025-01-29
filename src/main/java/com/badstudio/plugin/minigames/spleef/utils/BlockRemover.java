package com.badstudio.plugin.minigames.spleef.utils;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Random;

public class BlockRemover {
    private final JavaPlugin plugin;
    private final World world;
    private final int minX, maxX, minY, maxY, minZ, maxZ;

    public BlockRemover(JavaPlugin plugin, World world, int minX, int maxX, int minY, int maxY, int minZ, int maxZ) {
        this.plugin = plugin;
        this.world = world;
        this.minX = minX;
        this.maxX = maxX;
        this.minY = minY;
        this.maxY = maxY;
        this.minZ = minZ;
        this.maxZ = maxZ;
    }

    public void start() {
        new BukkitRunnable() {
            @Override
            public void run() {
                removeRandomBlocks();
            }
        }.runTaskTimer(plugin, 20 * 60L, 20 * 60L); // Ejecutar cada minuto
    }

    private void removeRandomBlocks() {
        Random random = new Random();
        for (int i = 0; i < 15; i++) {
            int x = random.nextInt(maxX - minX + 1) + minX;
            int y = random.nextInt(maxY - minY + 1) + minY;
            int z = random.nextInt(maxZ - minZ + 1) + minZ;

            Location location = new Location(world, x, y, z);
            if (location.getBlock().getType() == Material.SNOW_BLOCK) {
                location.getBlock().setType(Material.ICE);
                Bukkit.getScheduler().runTaskLater(plugin, () -> location.getBlock().setType(Material.AIR), 20L); // Esperar 1 segundo (20 ticks)
            }
        }
    }
}