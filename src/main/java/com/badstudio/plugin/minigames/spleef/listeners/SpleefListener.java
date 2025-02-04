package com.badstudio.plugin.minigames.spleef.listeners;

import com.badstudio.plugin.minigames.spleef.Spleef;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.ChatColor;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.Snowball;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.HashMap;
import java.util.UUID;


public class SpleefListener implements Listener {

    private final HashMap<UUID, Integer> bloquesDestruidos = new HashMap<>();
    private static final int finalBlock = 20;

    //Evento al romper la nieve
    @EventHandler
    public void onBreakSnow(BlockBreakEvent e) {
        //Verifica si el juego está activo
        if (!Spleef.isJuegoActivo()) {
            return;
        }
        //Verifica si está en el mundo correcto
        World mundo = Bukkit.getWorld("Spleef");
        if (mundo != null && e.getBlock().getWorld().equals(mundo)) {
            // Verifica que solo se pueda usar una pala para romper
            ItemStack herramientaEnMano = e.getPlayer().getInventory().getItemInMainHand();
            if (herramientaEnMano.getType() != Material.STONE_SHOVEL && herramientaEnMano.getType() != Material.DIAMOND_SHOVEL) {
                e.setCancelled(true);
                e.getPlayer().sendMessage(ChatColor.RED + "¡Usa una pala para romper los bloques!");
                return;
            }
            //Evita que tire items al romper la nieve
            if (e.getBlock().getType() == Material.SNOW_BLOCK) {
                e.setDropItems(false);

                Player jugador = e.getPlayer();
                UUID jugadorID = jugador.getUniqueId();

                // Incrementa el contador solo si se usa una pala válida
                bloquesDestruidos.put(jugadorID, bloquesDestruidos.getOrDefault(jugadorID, 0) + 1);
                int bloquesActuales = bloquesDestruidos.get(jugadorID);

                // Actualiza la action bar
                TextComponent mensaje;
                mensaje = new TextComponent(ChatColor.AQUA + "" + ChatColor.BOLD + "[" + bloquesActuales + "/" + finalBlock + "]");


                jugador.spigot().sendMessage(ChatMessageType.ACTION_BAR, mensaje);

                // Verifica si se alcanzó el límite de bloques para mejorar la pala
                if (bloquesActuales >= finalBlock) {
                    if (herramientaEnMano.getType() == Material.STONE_SHOVEL) {
                        mejorarPala(jugador);
                    } else if (herramientaEnMano.getType() == Material.DIAMOND_SHOVEL) {
                        jugador.getInventory().addItem(new ItemStack(Material.SNOWBALL, 1));
                        jugador.playSound(jugador.getLocation(), Sound.ENTITY_ITEM_PICKUP, 0.7F, 0.9F);
                    }
                    bloquesDestruidos.put(jugadorID, 0); // Reinicia el contador
                }
            }
        } else {
            Bukkit.getLogger().warning("El mundo Spleef no existe!");
        }
    }

    //Evento al tirar una bola de nieve
    @EventHandler
    public void onSnowballThrow(ProjectileHitEvent e) {
        Projectile snowBall = e.getEntity();
        if (snowBall instanceof Snowball) {
            if (Spleef.isJuegoActivo()) {
                World mundo = snowBall.getWorld();

                if (mundo.getName().equalsIgnoreCase("Spleef")) {
                    Block blockBreak = e.getHitBlock();
                    //Al tirar una bola de nieve a un bloque de nieve, este desaparece
                    if (blockBreak != null && blockBreak.getType() == Material.SNOW_BLOCK) {
                        blockBreak.setType(Material.AIR);
                    }
                } else {
                    Bukkit.getLogger().warning("No estas en el mundo de Spleef!");
                }
            }
        }
    }

    //Evento al craftear un bloque de nieve
    @EventHandler
    public void blockCraft(PrepareItemCraftEvent e) {
        if (Spleef.isJuegoActivo()) {
            //Evita que se pueda craftear un bloque de nieve
            if (e.getRecipe() != null && e.getRecipe().getResult().getType() == Material.SNOW_BLOCK) {
                e.getInventory().setResult(null);
            }
        }
    }
    //Método para mejorar la pala
    private void mejorarPala(Player jugador) {
        ItemStack palaActual = jugador.getInventory().getItemInMainHand();
        Material tipoPala = palaActual.getType();

        if (tipoPala == Material.STONE_SHOVEL) {
            ItemStack nuevaPala = new ItemStack(Material.DIAMOND_SHOVEL);
            ItemMeta meta = nuevaPala.getItemMeta();
            if (meta != null) {
                meta.setUnbreakable(true);
                nuevaPala.setItemMeta(meta);
            }
            jugador.getInventory().setItemInMainHand(nuevaPala);
            jugador.playSound(jugador.getLocation(), Sound.ENTITY_ITEM_BREAK, 0.27F, 1);
        } else if (tipoPala == Material.DIAMOND_SHOVEL) {
            int eficienciaActual = palaActual.getEnchantmentLevel(Enchantment.EFFICIENCY);
            if (eficienciaActual < 5) {
                palaActual.addUnsafeEnchantment(Enchantment.EFFICIENCY, eficienciaActual + 1);
                jugador.getInventory().setItemInMainHand(palaActual);
                jugador.playSound(jugador.getLocation(), Sound.ENTITY_ITEM_BREAK, 0.27F, 1);
            }
        }
    }
}
