package com.badstudio.plugin.utils;

import com.badstudio.plugin.Main;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.FireworkEffect;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PurgeUtil {

    private final Main plugin;

    public PurgeUtil(Main plugin) {
        this.plugin = plugin;
    }

    public void startPurge(Player player) {
        List<Player> jugadores = new ArrayList<>(Bukkit.getOnlinePlayers());
        int maximoJugadores = plugin.getConfigs().maximoJugadoresPurga();

        if (jugadores.size() > maximoJugadores) {
            // Aplica levitación a todos los jugadores
            for (Player jugador : jugadores) {
                jugador.addPotionEffect(new PotionEffect(PotionEffectType.LEVITATION,
                        Integer.MAX_VALUE,
                        1,
                        false,
                        false));
            }
            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                Collections.shuffle(jugadores);
                List<Player> jugadoresEliminados = jugadores.subList(maximoJugadores, jugadores.size());

                int delay = 0;
                int intervalo = 15;
                // Programamos la eliminación de cada jugador extra
                for (Player jugadorEliminado : jugadoresEliminados) {
                    Bukkit.getScheduler().runTaskLater(plugin, () -> {
                        Location ubicacion = jugadorEliminado.getLocation();
                        World mundo = jugadorEliminado.getWorld();
                        // Lanza el cohete al jugador eliminado
                        launchFirework(mundo, ubicacion);
                        // Kickea al jugador eliminado
                        jugadorEliminado.kickPlayer(ChatColor.RED + "Fuiste eliminado por la purga!");
                        // Envía un título a todos los jugadores anunciando la eliminación
                        for (Player j : jugadores) {
                            j.sendTitle(ChatColor.GOLD + jugadorEliminado.getName(), ChatColor.RED + "Eliminado", 5, 15, 3);
                        }
                    }, delay);
                    delay += intervalo;
                }
                // Luego, remueve la levitación de los jugadores sobrevivientes y reproduce un sonido
                Bukkit.getScheduler().runTaskLater(plugin, () -> {
                    for (Player jugador : jugadores) {
                        if (!jugadoresEliminados.contains(jugador)) {
                            if (jugador.hasPotionEffect(PotionEffectType.LEVITATION)) {
                                jugador.removePotionEffect(PotionEffectType.LEVITATION);
                            }
                            if (jugador.isOnline()) {
                                jugador.playSound(jugador, org.bukkit.Sound.ENTITY_PLAYER_LEVELUP, 1, 1);
                            }
                        }
                    }
                    player.sendMessage(ChatColor.GREEN + "La purga se ha completado. " + maximoJugadores + " jugadores restantes.");
                }, delay + 20);
            }, 20 * 4);
        } else {
            player.sendMessage(ChatColor.YELLOW + "Actualmente hay " + jugadores.size() + " jugadores en línea, no es necesario hacer purga.");
        }
    }

    public void launchFirework(World world, Location locPlayer) {
        int durationFireworks = plugin.getConfigs().duracionFireworks();
        Firework firework = (Firework) world.spawnEntity(locPlayer, EntityType.FIREWORK_ROCKET);
        FireworkMeta fireworkMeta = firework.getFireworkMeta();
        FireworkEffect efecto = FireworkEffect.builder()
                .with(FireworkEffect.Type.BALL_LARGE)
                .withColor(Color.fromRGB(11743532))
                .withFade(Color.fromRGB(11743532))
                .trail(true)
                .flicker(false)
                .build();
        fireworkMeta.addEffect(efecto);
        fireworkMeta.setPower(durationFireworks);
        firework.setFireworkMeta(fireworkMeta);
        // Hace que el cohete explote al instante (tras 1 tick)
        Bukkit.getScheduler().runTaskLater(plugin, firework::detonate, 1L);
    }
}

