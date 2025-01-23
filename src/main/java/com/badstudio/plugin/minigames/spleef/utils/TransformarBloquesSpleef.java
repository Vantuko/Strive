package com.badstudio.plugin.minigames.spleef.utils;

import com.badstudio.plugin.Main;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashSet;
import java.util.Set;

public class TransformarBloquesSpleef {

    private final Main plugin;

    public TransformarBloquesSpleef(Main plugin) {
        this.plugin = plugin;
    }

    public void transformarRegionCircular(World mundo, int centroX, int centroY, int centroZ, int radio) {
        Set<Location> bloques = new HashSet<>();

        // Recolecta los bloques en el radio especificado
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

        // Ejecuta el proceso de transformación y desaparición
        new BukkitRunnable() {
            @Override
            public void run() {
                if (bloques.isEmpty()) {
                    cancel();
                    return;
                }

                Set<Location> bloquesProcesados = new HashSet<>();

                for (Location loc : bloques) {
                    Block block = mundo.getBlockAt(loc);

                    // Cambia el bloque a hielo azul
                    if (block.getType() == Material.SNOW_BLOCK) {
                        block.setType(Material.BLUE_ICE);

                        // Activa la animación de "romper bloque"
                        Bukkit.getScheduler().runTaskTimer(plugin, () -> {
                            if (block.getType() == Material.BLUE_ICE) {
                                mundo.spawnParticle(Particle.BLOCK_CRUMBLE, loc, 10, Material.BLUE_ICE.createBlockData());
                            }
                        }, 0L, 5L);

                        // Borra el bloque después de 30 ticks (1.5 segundos)
                        Bukkit.getScheduler().runTaskLater(plugin, () -> {
                            if (block.getType() == Material.BLUE_ICE) {
                                block.setType(Material.AIR);
                            }
                        }, 30L);

                        bloquesProcesados.add(loc);
                    }
                }

                bloques.removeAll(bloquesProcesados);
            }
        }.runTaskTimer(plugin, 0L, 20L); // Repite cada segundo
    }
}
