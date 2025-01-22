package com.badstudio.plugin.minigames.spleef.listeners;

import com.badstudio.plugin.minigames.spleef.Spleef;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.Snowball;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.HashMap;
import java.util.UUID;


public class LaPala implements Listener {

    private final HashMap<UUID, Integer> bloquesDestruidos = new HashMap<>();
    private static final int finalBlock = 20;

    @EventHandler
    public void onBreakSnow(BlockBreakEvent e) {
        if(!Spleef.isJuegoActivo()){
            return;
        }

        World mundo = Bukkit.getWorld("Spleef");

        if (mundo != null && e.getBlock().getWorld().equals(mundo)) {
            if (e.getBlock().getType() == Material.SNOW_BLOCK) {
                e.setDropItems(false);

                Player jugador = e.getPlayer();
                UUID jugadorID = jugador.getUniqueId();

                bloquesDestruidos.put(jugadorID, bloquesDestruidos.getOrDefault(jugadorID, 0) + 1);

                if (bloquesDestruidos.get(jugadorID) >= finalBlock) {
                    jugador.getInventory().addItem(new ItemStack(Material.SNOWBALL, 1));
                    jugador.playSound(jugador, Sound.ITEM_BUCKET_EMPTY_POWDER_SNOW, 1, 2);
                    mejorarPala(jugador);
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
            if(Spleef.isJuegoActivo()){
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

    private void mejorarPala(Player jugador) {
        ItemStack palaActual = jugador.getInventory().getItemInMainHand();
        Material siguientePala = null;

        switch (palaActual.getType()) {
            case STONE_SHOVEL:
                siguientePala = Material.DIAMOND_SHOVEL;
                jugador.playSound(jugador.getLocation(), Sound.ENTITY_ITEM_BREAK, 0.27F, 1);
                break;
            case DIAMOND_SHOVEL:
                int eficienciaActual = palaActual.getEnchantmentLevel(Enchantment.EFFICIENCY);
                if (eficienciaActual < 5) {
                    agregarEficiencia(palaActual, eficienciaActual + 1);
                    jugador.playSound(jugador.getLocation(), Sound.ENTITY_ITEM_BREAK, 0.27F, 1);
                }
                return;
        }

        if (siguientePala != null) {
            ItemStack nuevaPala = new ItemStack(siguientePala);
            ItemMeta meta = nuevaPala.getItemMeta();
            if (meta != null) {
                meta.setUnbreakable(true);
                nuevaPala.setItemMeta(meta);
            }
            jugador.getInventory().setItemInMainHand(nuevaPala);
        }
    }

    private void agregarEficiencia(ItemStack pala, int nivelEficiencia) {
        ItemMeta meta = pala.getItemMeta();
        if (meta != null) {
            meta.addEnchant(Enchantment.EFFICIENCY, nivelEficiencia, true);
            pala.setItemMeta(meta);
        }
    }
}