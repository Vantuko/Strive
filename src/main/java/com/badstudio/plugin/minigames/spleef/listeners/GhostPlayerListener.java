package com.badstudio.plugin.minigames.spleef.listeners;

import com.badstudio.plugin.minigames.spleef.Spleef;

import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.Snowball;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerToggleFlightEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.projectiles.ProjectileSource;

import java.util.*;

public class GhostPlayerListener implements Listener {

    public final Set<UUID> ghostPlayers = new HashSet<>();
    public final HashMap<UUID, UUID> blockBreakers = new HashMap<>();
    private static final int LAYER_Y = 54; // Capa de caída
    private static final Location TELEPORT_LOCATION = new Location(Bukkit.getWorld("Spleef"), 0, 144, 0); // Configura la ubicación de teletransporte
    private final JavaPlugin plugin;

    public GhostPlayerListener(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        World world = player.getWorld();

        if (!Spleef.isJuegoActivo() || !world.getName().equalsIgnoreCase("Spleef")) {
            return;
        }

        if (player.getLocation().getY() < LAYER_Y) {
            if (!ghostPlayers.contains(player.getUniqueId())) {
                enableGhostMode(player);
                ghostPlayers.add(player.getUniqueId());
                player.getInventory().clear();
            }
            UUID victimId = player.getUniqueId();
            if (!blockBreakers.containsKey(victimId)) {
                for (Player jugador : Bukkit.getOnlinePlayers()) {
                    jugador.sendMessage(ChatColor.translateAlternateColorCodes('&',
                            "&c¡El jugador &f" + player.getName() + " &cha caído de la arena!"));
                }
            }

            if (blockBreakers.containsKey(victimId)) {
                UUID breakerId = blockBreakers.get(victimId);
                Player breaker = Bukkit.getPlayer(breakerId);

                if (breaker != null) {
                    for (Player jugador : Bukkit.getOnlinePlayers()) {
                        if (jugador.getWorld().getName().equalsIgnoreCase("Spleef")) {
                            jugador.sendMessage(ChatColor.translateAlternateColorCodes('&',
                                    "&c¡El jugador &f" + player.getName() + " &cha sido eliminado por &f" + breaker.getName() + "&c!"));
                        }
                    }
                }
            }

            player.teleport(TELEPORT_LOCATION);
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        if (!Spleef.isJuegoActivo()) {
            return;
        }

        Player breaker = event.getPlayer();
        Location blockLocation = event.getBlock().getLocation();

        if (event.getBlock().getType() == Material.SNOW_BLOCK && breaker.getWorld().getName().equalsIgnoreCase("Spleef")) {
            for (Player player : breaker.getWorld().getPlayers()) {
                Location playerLocation = player.getLocation();

                if (isStandingOnBlock(playerLocation, blockLocation)) {
                    blockBreakers.put(player.getUniqueId(), breaker.getUniqueId());
                    Bukkit.getLogger().info(player.getName() + " ha sido registrado como derribado por " + breaker.getName());
                    break;
                }
            }
        }
    }

    private boolean isStandingOnBlock(Location playerLocation, Location blockLocation) {
        return playerLocation.getBlockX() == blockLocation.getBlockX() &&
                playerLocation.getBlockZ() == blockLocation.getBlockZ() &&
                playerLocation.getBlockY() == blockLocation.getBlockY() + 1;
    }

    private void enableGhostMode(Player player) {
        player.setAllowFlight(true);
        player.setFlying(true);

        // Invisibles para otros jugadores, pero visibles para ellos mismos
        for (Player otherPlayer : Bukkit.getOnlinePlayers()) {
            if (!otherPlayer.equals(player)) {
                otherPlayer.hidePlayer(plugin, player);
            }
        }

        player.setMetadata("ghost", new FixedMetadataValue(plugin, true));
        ghostPlayers.add(player.getUniqueId());
    }

    public void disableGhostMode(Player player) {
        player.setFlying(false);
        player.setAllowFlight(false);

        for (Player otherPlayer : Bukkit.getOnlinePlayers()) {
            otherPlayer.showPlayer(plugin, player);
        }

        player.removeMetadata("ghost", plugin);
        ghostPlayers.remove(player.getUniqueId());
    }

    @EventHandler
    public void onPlayerToggleFlight(PlayerToggleFlightEvent event) {
        Player player = event.getPlayer();

        if (ghostPlayers.contains(player.getUniqueId())) {
            player.setFlying(true);
        }
    }

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (event.getEntity() instanceof Player victim) {
            if (victim.hasMetadata("ghost")) {
                event.setCancelled(true);
            }
        }

        if (event.getDamager() instanceof Projectile projectile) {
            ProjectileSource shooter = projectile.getShooter();

            if (shooter instanceof Player && ((Player) shooter).hasMetadata("ghost")) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onEntityDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player player) {
            if (player.hasMetadata("ghost")) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onProjectileHitPlayer(ProjectileHitEvent event) {
        if (event.getEntity() instanceof Snowball) {
            if (event.getEntity().getShooter() instanceof Player ghostPlayer && ghostPlayer.hasMetadata("ghost")) {
                event.getEntity().remove();

                Snowball nuevoProyectil = ghostPlayer.launchProjectile(Snowball.class);
                nuevoProyectil.setVelocity(event.getEntity().getVelocity());
            }
        }
    }



    public Set<UUID> getGhostPlayers() {
        return ghostPlayers;
    }
}
