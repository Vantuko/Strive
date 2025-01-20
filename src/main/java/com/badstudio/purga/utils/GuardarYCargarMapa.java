package com.badstudio.purga.utils;
import com.badstudio.purga.Main;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.extent.clipboard.BlockArrayClipboard;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
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

        BlockVector3 pos1 = BlockVector3.at(x1, y1, z1);
        BlockVector3 pos2 = BlockVector3.at(x2, y2, z2);

        com.sk89q.worldedit.world.World weWorld = BukkitAdapter.adapt(bukkitWorld);


        CuboidRegion region = new CuboidRegion(weWorld, pos1, pos2);


        BlockArrayClipboard clipboard = new BlockArrayClipboard(region);


        File archivo = new File(plugin.getDataFolder(), nombreArchivo + ".schem");
        try (EditSession editSession = WorldEdit.getInstance().newEditSession(weWorld);
             FileOutputStream fos = new FileOutputStream(archivo)) {
            ClipboardFormats.findByFile(archivo).getWriter(fos).write(clipboard);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void cargarMapa(org.bukkit.World mundo, int x, int y, int z, String nombreArchivo) {
        File archivo = new File(plugin.getDataFolder(), nombreArchivo + ".schem");

        if (!archivo.exists()) {
            Bukkit.getLogger().warning("El archivo " + nombreArchivo + ".schem no existe.");
            return;
        }

        try (FileInputStream fis = new FileInputStream(archivo);
             EditSession editSession = WorldEdit.getInstance().newEditSession(BukkitAdapter.adapt(mundo))) {

            Clipboard clipboard = ClipboardFormats.findByFile(archivo).getReader(fis).read();
            ClipboardHolder holder = new ClipboardHolder(clipboard);

            holder.createPaste(editSession)
                    .to(BlockVector3.at(x, y, z))
                    .ignoreAirBlocks(false)
                    .build();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
