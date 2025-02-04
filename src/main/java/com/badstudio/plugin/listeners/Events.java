package com.badstudio.plugin.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.ChatColor;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.potion.PotionEffectType;

public class Events implements Listener {

    //Evento al jugador unirse
    @EventHandler
    public void PlayerEnter(PlayerJoinEvent e) {
        Player player = e.getPlayer();
        //Quita el efecto de levitación al volver a unirse
        if (player.hasPotionEffect(PotionEffectType.LEVITATION)) {
            player.removePotionEffect(PotionEffectType.LEVITATION);
        }
        //Mensaje al unirse al servidor
        e.setJoinMessage(ChatColor.translateAlternateColorCodes('&', "&7[&a+&7] &a" + player.getName()));
    }

    //Evento al jugador desconectarse
    @EventHandler
    public void PlayerDisconnect(PlayerQuitEvent e) {
        Player player = e.getPlayer();
        //Mensaje al desconectarse del servidor
        e.setQuitMessage(ChatColor.translateAlternateColorCodes('&', "&7[&c-&7] &c" + player.getName()));
    }
}