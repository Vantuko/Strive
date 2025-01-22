package com.badstudio.plugin.minigames.spleef.utils;

import com.badstudio.plugin.Main;
import net.minecraft.network.protocol.game.PacketPlayOutBlockBreakAnimation;
import net.minecraft.core.BlockPosition;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
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

        // Crea los "anillos" desde afuera hacia adentro
        for (int r = radio; r >= 1; r--) {
            for (int x = -r; x <= r; x++) {
                for (int z = -r; z <= r; z++) {
                    if (x * x + z * z <= r * r && x * x + z * z > (r - 1) * (r - 1)) {
                        Location loc = new Location(mundo, centroX + x, centroY, centroZ + z);
                        if (mundo.getBlockAt(loc).getType() == Material.SNOW_BLOCK) {
                            bloques.add(loc);
                        }
                    }
                }
            }
        }
        int tiempoPorAnillo = 32 / radio; // Tiempo por cada "anillo"
        new BukkitRunnable() {
            int paso = 0; // Progreso de la textura

            @Override
            public void run() {
                if (bloques.isEmpty()) {
                    cancel();
                    return;
                }

                Set<Location> procesados = new HashSet<>();

                for (Location loc : bloques) {

                    // Cuando la textura llega a 9, convierte a hielo azul
                    if (paso >= 9) {
                        loc.getBlock().setType(Material.BLUE_ICE);

                        // Después de 1 segundo, elimina el bloque
                        Bukkit.getScheduler().runTaskLater(plugin, () -> loc.getBlock().setType(Material.AIR), 20L);
                        procesados.add(loc);
                    }
                }

                bloques.removeAll(procesados);
                paso++;
                if (paso > 9) {
                    paso = 0;
                }
            }
        }.runTaskTimer(plugin, 0L, tiempoPorAnillo * 20L);
    }

}
