package com.badstudio.plugin.minigames.spleef.listeners;

import com.badstudio.plugin.minigames.spleef.Spleef;

import com.badstudio.plugin.minigames.spleef.utils.BrokenBlock;
import com.badstudio.plugin.minigames.spleef.utils.ScoreboardManager;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.ChatColor;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;

import static org.apache.commons.lang.exception.ExceptionUtils.getCause;

public class GhostPlayerListener implements Listener {

    public final Set<UUID> spectators = new HashSet<>();
    private static final int LAYER_Y = 54;
    private static final Location TELEPORT_LOCATION = new Location(Bukkit.getWorld("Spleef"), 0, 144, 0);
    private final JavaPlugin plugin;
    private final ScoreboardManager scoreManager;
    private final List<BrokenBlock> brokenBlocks = new ArrayList<>();

    public GhostPlayerListener(JavaPlugin plugin, ScoreboardManager scoreManager) {
        this.plugin = plugin;
        this.scoreManager = scoreManager;
    }

    @EventHandler
    public void onPlayerMovement(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        World world = player.getWorld();

        if (!Spleef.isJuegoActivo() || !world.getName().equalsIgnoreCase("Spleef")) {
            return;
        }

        // Si el jugador cae por debajo de la capa asignada
        if (player.getLocation().getY() < LAYER_Y && !spectators.contains(player.getUniqueId())) {
            ponerModoEspectador(player);
        }

        // Si el jugador está fuera del límite como espectador
        if (spectators.contains(player.getUniqueId())) {
            if (isOutOfSpectatorLimit(player)) {
                Location safeLocation = getNearestBoundary(player.getLocation());
                player.teleport(safeLocation);
            }
        }
    }

    @EventHandler
    public void onPlayerOutsideBorder(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        double damagePerSecond = plugin.getConfig().getDouble("spleef.world_border.damage_per_second");
        WorldBorder border = player.getWorld().getWorldBorder();
        double remainingHealth = player.getHealth() - damagePerSecond / 20.0;

        if (!border.isInside(player.getLocation()) && !spectators.contains(player.getUniqueId())) {
            if (remainingHealth > 0) {
                player.damage(damagePerSecond / 20.0);
                int timeLeft = (int) Math.ceil(remainingHealth / (damagePerSecond / 20.0));
                TextComponent advertencia = new TextComponent(
                        ChatColor.RED + "⚠ " + ChatColor.YELLOW + "Tiempo fuera del borde: " +
                                ChatColor.GOLD + timeLeft + "s" + ChatColor.RED + " ⚠");
                player.spigot().sendMessage(ChatMessageType.ACTION_BAR, advertencia);
            } else {
                ponerModoEspectador(player);
            }
        }
    }
    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        Block block = event.getBlock();
        brokenBlocks.add(new BrokenBlock(block.getLocation(), player));
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        World world = player.getWorld();

        if (!Spleef.isJuegoActivo() || !world.getName().equalsIgnoreCase("Spleef")) {
            return;
        }

        // Si el jugador cae por debajo de la capa asignada
        if (player.getLocation().getY() < LAYER_Y && !spectators.contains(player.getUniqueId())) {
            // Verificar si la caída fue causada por un bloque roto recientemente
            BrokenBlock causedBy = getCausedBy(player.getLocation());
            if (causedBy != null) {
                Player breaker = causedBy.getBreaker();
                if (breaker != null && !breaker.getUniqueId().equals(player.getUniqueId())) {
                    // Mostrar mensaje y sumar puntos
                    Bukkit.broadcastMessage(ChatColor.RED + "¡" + player.getName() + " fue eliminado por " + breaker.getName() + "!");
                    scoreManager.addScore(breaker, 5);
                    scoreManager.updateScore(breaker, scoreManager.getScore(breaker));
                }
            }
            ponerModoEspectador(player);
        }

        // Si el jugador está fuera del límite como espectador
        if (spectators.contains(player.getUniqueId())) {
            if (isOutOfSpectatorLimit(player)) {
                Location safeLocation = getNearestBoundary(player.getLocation());
                player.teleport(safeLocation);
            }
        }
    }

    private BrokenBlock getCausedBy(Location location) {
        long currentTime = System.currentTimeMillis();
        for (BrokenBlock brokenBlock : new ArrayList<>(brokenBlocks)) {
            if (brokenBlock.getLocation().distance(location) < 1.5 &&
                    currentTime - brokenBlock.getBreakTime() <= 2400) { // 2.4 segundos
                return brokenBlock;
            }
        }
        return null;
    }

    private void ponerModoEspectador(Player player) {
        // Cambiar al modo espectador
        player.setGameMode(GameMode.SPECTATOR);

        // Ocultar al jugador para los vivos
        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            if (!spectators.contains(onlinePlayer.getUniqueId())) {
                onlinePlayer.hidePlayer(plugin, player);
            }
        }

        // Agregar a la lista de espectadores
        spectators.add(player.getUniqueId());
        actualizarTabList();

        // Teletransportar al jugador a la ubicación asignada
        player.teleport(TELEPORT_LOCATION);

        // Crear una instancia de ScoreboardManager fuera del bucle
        ScoreboardManager scoreboardManager = new ScoreboardManager();

        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            onlinePlayer.sendMessage(ChatColor.translateAlternateColorCodes('&',
                    "&c¡El jugador &f" + player.getName() + " &cha sido eliminado!"));

            // Actualizar la puntuación del jugador que eliminó al jugador que cayó
            if (onlinePlayer != player && !spectators.contains(onlinePlayer.getUniqueId())) {
                // Añadir 1 punto al jugador
                scoreManager.addScore(onlinePlayer, 1);

                // Obtener la puntuación actualizada
                int newScore = scoreManager.getScore(onlinePlayer);

                // Actualizar el scoreboard
                scoreboardManager.updateScore(onlinePlayer, newScore);
                scoreboardManager.setScoreboard(onlinePlayer);
            }
        }
    }

    public void reiniciarEspectadores() {
        // Restaurar a todos los espectadores al estado normal
        for (UUID uuid : new HashSet<>(spectators)) {
            Player player = Bukkit.getPlayer(uuid);
            if (player != null) {
                player.setGameMode(GameMode.SURVIVAL);
                for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                    onlinePlayer.showPlayer(plugin, player); // Hacer visible nuevamente
                }
            }
        }
        spectators.clear();
        actualizarTabList();
    }

    private boolean isOutOfSpectatorLimit(Player player) {
        // Revisar si el jugador está fuera del límite definido para espectadores
        int limite = plugin.getConfig().getInt("spleef.spectator_limit");
        Location arenaCenter = new Location(player.getWorld(), 0, 0, 0); // Ajustar al centro real de la arena
        double distancia = player.getLocation().distance(arenaCenter);

        return distancia > limite;
    }

    private Location getNearestBoundary(Location currentLocation) {
        // Regresar la ubicación más cercana al límite para evitar que el espectador lo cruce
        int limite = plugin.getConfig().getInt("spleef.spectator_limit");
        Location arenaCenter = new Location(currentLocation.getWorld(), 0, currentLocation.getY(), 0);

        // Calcular la posición ajustada al límite
        double ratio = limite / currentLocation.distance(arenaCenter);
        double newX = arenaCenter.getX() + (currentLocation.getX() - arenaCenter.getX()) * ratio;
        double newZ = arenaCenter.getZ() + (currentLocation.getZ() - arenaCenter.getZ()) * ratio;

        return new Location(currentLocation.getWorld(), newX, currentLocation.getY(), newZ);
    }

    private void actualizarTabList() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (spectators.contains(player.getUniqueId())) {
                player.setPlayerListName(ChatColor.GRAY + "[Espectador] " + player.getName());
            } else {
                player.setPlayerListName(player.getName());
            }
        }
    }
}
