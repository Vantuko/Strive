package com.badstudio.purga.minigames.spleef;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.UUID;


public class LaPala implements Listener {

    private final HashMap<UUID, Integer> bloquesDestruidos = new HashMap<>();
    private static final int finalBlock = 20;

    @EventHandler
    public void OnBreakSnow(BlockBreakEvent e) {
        World mundo = Bukkit.getWorld("Spleef");

        if (mundo != null && e.getBlock().getWorld().equals(mundo)) {
            if (e.getBlock().getType() == Material.SNOW_BLOCK) {
                e.setDropItems(false);

                Player jugador = e.getPlayer();
                UUID jugadorID = jugador.getUniqueId();

                bloquesDestruidos.put(jugadorID, bloquesDestruidos.getOrDefault(jugadorID, 0)+ 1);

                if (bloquesDestruidos.get(jugadorID) >= finalBlock) {
                    jugador.getInventory().addItem(new ItemStack(Material.SNOWBALL, 1));

                    bloquesDestruidos.put(jugadorID, 0);
                }

            }
        } else {
            Bukkit.getLogger().warning("El mundo Spleef no existe!");
        }
    }
}

