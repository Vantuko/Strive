package com.badstudio.purga.minigames.spleef;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;


public class LaPala implements Listener {

    @EventHandler
    public void OnBreakSnow(BlockBreakEvent e) {
        World mundo = Bukkit.getWorld("Spleef");

        if (mundo != null && e.getBlock().getWorld().equals(mundo)) {
            if (e.getBlock().getType() == Material.SNOW_BLOCK) {
                e.setDropItems(false);
            }
        } else {
            Bukkit.getLogger().warning("El mundo Spleef no existe!");
        }
    }
}

