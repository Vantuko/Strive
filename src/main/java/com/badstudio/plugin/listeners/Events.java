package com.badstudio.plugin.listeners;

import com.badstudio.plugin.minigames.spleef.Spleef;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.ChatColor;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.potion.PotionEffectType;


import java.util.ArrayList;
import java.util.List;

public class Events implements Listener{
    @EventHandler
    public void PlayerEnter(PlayerJoinEvent e){
        Player player = e.getPlayer();

        if(player.hasPotionEffect(PotionEffectType.LEVITATION)){
            player.removePotionEffect(PotionEffectType.LEVITATION);
        }

        e.setJoinMessage(ChatColor.translateAlternateColorCodes('&', "&7[&a+&7] &a"+player.getName()));
    }

    @EventHandler
    public void PlayerDisconnect(PlayerQuitEvent e){
        Player player = e.getPlayer();

        e.setQuitMessage(ChatColor.translateAlternateColorCodes('&', "&7[&c-&7] &c"+player.getName()));

    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent e){
        if (!Spleef.isJuegoActivo()) {
            return;
        }

        if(e.getEntity() instanceof Player){
            Player player = e.getEntity();
            World mundo = player.getWorld();
            List<Player> jugadores = new ArrayList<>(Bukkit.getOnlinePlayers());
            e.setDeathMessage(null);

            if(mundo != null && mundo.getName().equalsIgnoreCase("Spleef")){
                for(Player jugador : jugadores){
                    if(jugador.getWorld().getName().equalsIgnoreCase("Spleef")){
                        jugador.sendMessage(ChatColor.translateAlternateColorCodes('&', "&c¡El jugador &f" + player.getName() + " &cha caido de la arena!"));
                    }
                }
            }
        }
    }

}
