package com.badstudio.purga.minigames;


import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;


public class LaPala implements Listener {

    @EventHandler
    public void OnBreakSnow(BlockBreakEvent e) {
        if (e.getBlock().getType() == Material.SNOW_BLOCK) {
            e.setDropItems(false);
        }
    }
}

