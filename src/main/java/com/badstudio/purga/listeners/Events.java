package com.badstudio.purga.listeners;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.ChatColor;
import org.bukkit.event.player.PlayerQuitEvent;

public class Events implements Listener{
    @EventHandler
    public void PlayerEnter(PlayerJoinEvent e){
        Player player = e.getPlayer();


        e.setJoinMessage(ChatColor.translateAlternateColorCodes('&', "&7[&a+&7] &a"+player.getName()));
    }

    @EventHandler
    public void PlayerDisconnect(PlayerQuitEvent e){
        Player player = e.getPlayer();

        e.setQuitMessage(ChatColor.translateAlternateColorCodes('&', "&7[&c-&7] &c"+player.getName()));

    }
}
