package com.badstudio.plugin.minigames.spleef.utils;

import com.badstudio.plugin.Main;
import org.bukkit.World;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;

public class TransformacionRepetitiva {

    private final Main plugin;

    public TransformacionRepetitiva(Main plugin) {
        this.plugin = plugin;
    }

    public void ejecutarTransformaciones(World mundo, List<CoordenadaRadio> configuraciones, int intervaloSegundos) {
        new BukkitRunnable() {
            private int index = 0;

            @Override
            public void run() {
                if (index >= configuraciones.size()) {
                    cancel();
                    return;
                }

                CoordenadaRadio config = configuraciones.get(index);
                TransformarBloquesSpleef transformarBloques = new TransformarBloquesSpleef(plugin);
                transformarBloques.transformarRegionCircular(mundo, config.getX(), config.getY(), config.getZ(), config.getRadio());

                index++;
            }
        }.runTaskTimer(plugin, 0L, intervaloSegundos * 20L);
    }

    public static class CoordenadaRadio {
        private final int x, y, z, radio;

        public CoordenadaRadio(int x, int y, int z, int radio) {
            this.x = x;
            this.y = y;
            this.z = z;
            this.radio = radio;
        }

        public int getX() {
            return x;
        }

        public int getY() {
            return y;
        }

        public int getZ() {
            return z;
        }

        public int getRadio() {
            return radio;
        }
    }
}
