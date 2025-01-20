package com.badstudio.purga.utils;
import com.badstudio.purga.Main;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.extent.clipboard.BlockArrayClipboard;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormat;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormats;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.session.ClipboardHolder;
import org.bukkit.Bukkit;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.FileOutputStream;

public class GuardarYCargarMapa {
    private final Main plugin;

    public GuardarYCargarMapa(Main plugin) {
        this.plugin = plugin;
    }

    public void guardarMapa(org.bukkit.World bukkitWorld, int x1, int y1, int z1, int x2, int y2, int z2, String nombreArchivo) {
        // Convierte el mundo de Bukkit a WorldEdit
        com.sk89q.worldedit.world.World weWorld = BukkitAdapter.adapt(bukkitWorld);

        // Define la región cúbica
        BlockVector3 pos1 = BlockVector3.at(x1, y1, z1);
        BlockVector3 pos2 = BlockVector3.at(x2, y2, z2);
        CuboidRegion region = new CuboidRegion(weWorld, pos1, pos2);

        // Crea el clipboard
        BlockArrayClipboard clipboard = new BlockArrayClipboard(region);

        // Define el archivo .schematic
        File archivo = new File(plugin.getDataFolder(), nombreArchivo + ".schematic");

        // Asegúrate de que la carpeta del plugin existe
        if (!plugin.getDataFolder().exists()) {
            plugin.getDataFolder().mkdirs();
        }

        try (EditSession editSession = WorldEdit.getInstance().newEditSession(weWorld);
             FileOutputStream fos = new FileOutputStream(archivo)) {

            // Fuerza el uso del formato SCHEMATIC
            ClipboardFormat formato = ClipboardFormats.findByAlias("schematic");

            // Verifica si el formato es válido
            if (formato == null) {
                Bukkit.getLogger().severe("El formato SCHEMATIC no está soportado en esta versión de WorldEdit.");
                return;
            }

            // Escribe el clipboard en el archivo
            formato.getWriter(fos).write(clipboard);
            Bukkit.getLogger().info("El mapa se guardó correctamente como " + archivo.getAbsolutePath());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void cargarMapa(org.bukkit.World bukkitWorld, int x, int y, int z, String nombreArchivo) {
        // Define el archivo .schematic
        File archivo = new File(plugin.getDataFolder(), nombreArchivo + ".schematic");

        // Comprueba si el archivo existe
        if (!archivo.exists()) {
            Bukkit.getLogger().warning("El archivo " + nombreArchivo + ".schematic no existe.");
            return;
        }

        try (FileInputStream fis = new FileInputStream(archivo);
             EditSession editSession = WorldEdit.getInstance().newEditSession(BukkitAdapter.adapt(bukkitWorld))) {

            // Obtén el formato y crea un Clipboard desde el archivo
            ClipboardFormat formato = ClipboardFormats.findByFile(archivo);

            if (formato == null) {
                Bukkit.getLogger().severe("El archivo no tiene un formato válido: " + archivo.getName());
                return;
            }

            Clipboard clipboard = formato.getReader(fis).read();
            ClipboardHolder holder = new ClipboardHolder(clipboard);

            // Pega el contenido del clipboard en la posición especificada
            holder.createPaste(editSession)
                    .to(BlockVector3.at(x, y, z))
                    .ignoreAirBlocks(false) // Cambia a true si no quieres reemplazar bloques de aire
                    .build();

            Bukkit.getLogger().info("El mapa " + nombreArchivo + " se cargó correctamente.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
