package com.badstudio.plugin.utils;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.Map;

public class GuardarMapa {
    private final Map<Vector, Material> bloquesOriginales = new HashMap<>();

    public void guardarMapa(World mundo, int x1, int y1, int z1, int x2, int y2, int z2) {
        int minX = Math.min(x1, x2);
        int maxX = Math.max(x1, x2);
        int minY = Math.min(y1, y2);
        int maxY = Math.max(y1, y2);
        int minZ = Math.min(z1, z2);
        int maxZ = Math.max(z1, z2);

        for (int x = minX; x <= maxX; x++) {
            for (int y = minY; y <= maxY; y++) {
                for (int z = minZ; z <= maxZ; z++) {
                    Block bloque = mundo.getBlockAt(x, y, z);
                    bloquesOriginales.put(new Vector(x, y, z), bloque.getType());
                }
            }
        }
        Bukkit.getLogger().info(ChatColor.GREEN + "Mapa guardado en memoria. Total de bloques: " + bloquesOriginales.size());
    }
    public void restaurarMapa(World mundo) {
        if (bloquesOriginales.isEmpty()) {
            Bukkit.getLogger().info("No hay bloques guardados para restaurar.");
            return;
        }
        for (Map.Entry<Vector, Material> entrada : bloquesOriginales.entrySet()) {
            Vector pos = entrada.getKey();
            Material tipo = entrada.getValue();

            Block bloque = mundo.getBlockAt(pos.getBlockX(), pos.getBlockY(), pos.getBlockZ());
            bloque.setType(tipo);
        }
        Bukkit.getLogger().info(ChatColor.GREEN + "Mapa restaurado correctamente. Bloques restaurados: " + bloquesOriginales.size());
    }

    public void limpiarDatos() {
        bloquesOriginales.clear();
        Bukkit.getLogger().info("Datos de bloques en memoria limpiados.");
    }
}

