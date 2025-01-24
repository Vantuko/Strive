package com.badstudio.plugin.minigames.spleef.utils;

import com.badstudio.plugin.Main;
import org.bukkit.Bukkit;
import org.bukkit.World;
import java.util.List;

public class TransformacionRepetitiva {

    private final Main plugin;

    public TransformacionRepetitiva(Main plugin) {
        this.plugin = plugin;
    }

    public void ejecutarTransformaciones(World mundo, List<CoordenadaRadio> configuraciones) {
        ejecutarTransformacionPorCapa(mundo, configuraciones, 0);
    }

    private void ejecutarTransformacionPorCapa(World mundo, List<CoordenadaRadio> configuraciones, int index) {
        if (index >= configuraciones.size()) {
            return;
        }

        CoordenadaRadio config = configuraciones.get(index);
        TransformarBloquesSpleef transformarBloques = new TransformarBloquesSpleef(plugin);

        transformarBloques.transformarRegionCircular(mundo, config.getX(), config.getY(), config.getZ(), config.getRadio(), config.getDelayEntreAnillos(), config.getTiempoDesaparicion(), () -> {
            // Llama recursivamente para procesar la siguiente capa
            ejecutarTransformacionPorCapa(mundo, configuraciones, index + 1);
        });
    }

    public static class CoordenadaRadio {
        private final int x, y, z, radio;
        private final int delayEntreAnillos, tiempoDesaparicion;

        public CoordenadaRadio(int x, int y, int z, int radio, int delayEntreAnillos, int tiempoDesaparicion) {
            this.x = x;
            this.y = y;
            this.z = z;
            this.radio = radio;
            this.delayEntreAnillos = delayEntreAnillos;
            this.tiempoDesaparicion = tiempoDesaparicion;
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

        public int getDelayEntreAnillos() {
            return delayEntreAnillos;
        }

        public int getTiempoDesaparicion() {
            return tiempoDesaparicion;
        }
    }

}
