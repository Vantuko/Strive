package com.badstudio.plugin.minigames.spleef;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.Snowball;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.UUID;


public class LaPala implements Listener {

    private final HashMap<UUID, Integer> bloquesDestruidos = new HashMap<>();
    private static final int finalBlock = 20;

    @EventHandler
    public void onBreakSnow(BlockBreakEvent e) {
        World mundo = Bukkit.getWorld("Spleef");

        if (mundo != null && e.getBlock().getWorld().equals(mundo)) {
            if (e.getBlock().getType() == Material.SNOW_BLOCK) {
                e.setDropItems(false);

                Player jugador = e.getPlayer();
                UUID jugadorID = jugador.getUniqueId();

                bloquesDestruidos.put(jugadorID, bloquesDestruidos.getOrDefault(jugadorID, 0) + 1);

                if (bloquesDestruidos.get(jugadorID) >= finalBlock) {
                    jugador.getInventory().addItem(new ItemStack(Material.SNOWBALL, 1));

                    bloquesDestruidos.put(jugadorID, 0);
                }

            }
        } else {
            Bukkit.getLogger().warning("El mundo Spleef no existe!");
        }
    }

    @EventHandler
    public void onSnowballThrow(ProjectileHitEvent e) {
        Projectile snowBall = e.getEntity();
        if (snowBall instanceof Snowball) {
            World mundo = snowBall.getWorld();

            if (mundo.getName().equalsIgnoreCase("Spleef")) {
                Block blockBreak = e.getHitBlock();

                if (blockBreak != null && blockBreak.getType() == Material.SNOW_BLOCK) {
                    blockBreak.setType(Material.AIR);
                }
            } else {
                Bukkit.getLogger().warning("No estas en el mundo de Spleef!");
            }
        }
    }
}