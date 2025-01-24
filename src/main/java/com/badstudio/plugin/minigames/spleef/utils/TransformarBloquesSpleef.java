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

    public void transformarRegionCircular(World mundo, int centroX, int centroY, int centroZ, int radio, int delayEntreAnillos, int tiempoDesaparicion, Runnable onFinish) {
        List<List<Location>> anillos = new ArrayList<>();

        // Divide los bloques en "anillos" desde el más externo al más interno
        for (int r = radio; r > 0; r--) {
            List<Location> anillo = new ArrayList<>();
            for (int x = -r; x <= r; x++) {
                for (int z = -r; z <= r; z++) {
                    if (x * x + z * z <= r * r && x * x + z * z > (r - 1) * (r - 1)) {
                        Location loc = new Location(mundo, centroX + x, centroY, centroZ + z);
                        if (mundo.getBlockAt(loc).getType() == Material.SNOW_BLOCK) {
                            anillo.add(loc);
                        }
                    }
                }
            }
            if (!anillo.isEmpty()) {
                anillos.add(anillo);
            }
        }

        // Agrega el bloque central si existe
        Location centro = new Location(mundo, centroX, centroY, centroZ);
        if (mundo.getBlockAt(centro).getType() == Material.SNOW_BLOCK) {
            List<Location> bloqueCentral = new ArrayList<>();
            bloqueCentral.add(centro);
            anillos.add(bloqueCentral);
        }

        // Procesa cada anillo con el delay configurado
        new BukkitRunnable() {
            int anilloActual = 0;

            @Override
            public void run() {
                if (anilloActual >= anillos.size()) {
                    cancel();
                    // Llama al callback cuando todos los anillos hayan desaparecido
                    onFinish.run();
                    return;
                }

                List<Location> anillo = anillos.get(anilloActual);

                // Transforma el anillo actual
                transformarAnillo(anillo, tiempoDesaparicion);

                anilloActual++;
            }
        }.runTaskTimer(plugin, 0L, delayEntreAnillos * 20L); // Configura el delay entre anillos
    }

    private void transformarAnillo(List<Location> anillo, int tiempoDesaparicion) {
        for (Location loc : anillo) {
            loc.getBlock().setType(Material.BLUE_ICE);

            // Elimina el bloque después del tiempo configurado
            Bukkit.getScheduler().runTaskLater(plugin, () -> loc.getBlock().setType(Material.AIR), tiempoDesaparicion * 20L);
        }
    }

}
