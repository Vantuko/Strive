package com.badstudio.plugin.minigames.spleef.utils;

import com.badstudio.plugin.minigames.spleef.Spleef;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashSet;
import java.util.Set;

public class TransformarBloquesSpleef{

    private final Spleef plugin;

    public TransformarBloquesSpleef(Spleef plugin) {
        this.plugin = plugin;
    }

    public void transformarRegionCircular(World mundo, int centroX, int centroY, int centroZ, int radio) {
        Set<Location> bloques = new HashSet<>();


        for (int x = -radio; x <= radio; x++) {
            for (int z = -radio; z <= radio; z++) {
                if (x * x + z * z <= radio * radio) {
                    Location loc = new Location(mundo, centroX + x, centroY, centroZ + z);
                    if (mundo.getBlockAt(loc).getType() == Material.SNOW_BLOCK) {
                        bloques.add(loc);
                    }
                }
            }
        }

        new BukkitRunnable() {
            int paso = 0;

            @Override
            public void run() {
                if (bloques.isEmpty() || paso >= 30) {
                    cancel();
                    return;
                }
                int radioActual = radio - paso;

                bloques.removeIf(loc -> {
                    if (loc.distance(new Location(mundo, centroX, centroY, centroZ)) >= radioActual) {

                        mundo.getBlockAt(loc).setType(Material.BLUE_ICE);
                        Bukkit.getScheduler().runTaskLater((Plugin) plugin, () -> {
                            if (mundo.getBlockAt(loc).getType() == Material.BLUE_ICE) {
                                mundo.getBlockAt(loc).setType(Material.AIR);
                            }
                        }, 20L);
                        return true;
                    }
                    return false;
                });

                paso++;
            }
        }.runTaskTimer((Plugin) plugin, 0L, 20L);
    }
}
