package com.badstudio.plugin.minigames.spleef.utils;

import com.badstudio.plugin.Main;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;

public class TransformarBloquesSpleef {

    private final Main plugin;

    public TransformarBloquesSpleef(Main plugin) {
        this.plugin = plugin;
    }

    public void transformarRegionCircular(World mundo, int centroX, int centroY, int centroZ, int radio) {
        List<List<Location>> anillos = new ArrayList<>();

        // Divide los bloques en "anillos" desde el más externo al más interno
        for (int r = radio; r >= 0; r--) {
            List<Location> anillo = new ArrayList<>();
            List<Location> esquinas = new ArrayList<>();
            for (int x = -r; x <= r; x++) {
                for (int z = -r; z <= r; z++) {
                    if (x * x + z * z <= r * r && x * x + z * z > (r - 1) * (r - 1)) {
                        Location loc = new Location(mundo, centroX + x, centroY, centroZ + z);
                        if (mundo.getBlockAt(loc).getType() == Material.SNOW_BLOCK) {
                            // Identifica las "esquinas" del anillo (norte, sur, este, oeste)
                            if ((x == 0 && Math.abs(z) == r) || (z == 0 && Math.abs(x) == r)) {
                                esquinas.add(loc);
                            } else {
                                anillo.add(loc);
                            }
                        }
                    }
                }
            }
            if (!esquinas.isEmpty()) {
                anillos.add(esquinas); // Agrega las esquinas como la primera parte del anillo
            }
            if (!anillo.isEmpty()) {
                anillos.add(anillo); // Agrega el resto del anillo
            }
        }

        // Procesa cada conjunto (esquinas y anillos) con un retraso
        int delay = 0;
        for (List<Location> conjunto : anillos) {
            new BukkitRunnable() {
                int progreso = 0;

                @Override
                public void run() {
                    if (progreso > 9) {
                        // Cuando la textura "finaliza", convierte los bloques a hielo azul y luego los elimina
                        for (Location loc : conjunto) {
                            loc.getBlock().setType(Material.BLUE_ICE);

                            // Después de 1 segundo, elimina el bloque
                            Bukkit.getScheduler().runTaskLater(plugin, () -> loc.getBlock().setType(Material.AIR), 20L);
                        }
                        cancel();
                        return;
                    }

                    progreso++;
                }
            }.runTaskTimer(plugin, delay, 2L); // Procesa cada conjunto con un intervalo de 2 ticks
            delay += 20; // Retrasa el siguiente conjunto en 1 segundo (20 ticks)
        }
    }


}
