package com.badstudio.plugin.commands;

import com.badstudio.plugin.Main;
import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class StriveComandos implements CommandExecutor {

    private final Main plugin;

    public StriveComandos(Main plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        Player player = (Player) sender;

        if (args.length == 0) {
            helpMessage(player);
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "host":
                Host(player);
                break;
            case "reload":
                plugin.reloadConfig();
                player.sendMessage(ChatColor.GREEN + "Configuración reiniciada");
                break;
            default:
                player.sendMessage(ChatColor.RED + "Comando desconocido, utiliza /strive");
                break;
        }
        return true;
    }
    private void Host(Player player) {
        List<Player> Jugadores = new ArrayList(Bukkit.getOnlinePlayers());

        int maximoJugadores = (plugin.getConfigs().maximoJugadoresPurga());

        if (Jugadores.size() > maximoJugadores) {
            for (Player jugador : Jugadores) {
                jugador.addPotionEffect(new PotionEffect(PotionEffectType.LEVITATION, Integer.MAX_VALUE, 1, false, false));
            }
            Bukkit.getScheduler().runTaskLater(plugin, () -> {

            Collections.shuffle(Jugadores);
            List<Player> JugadoresEliminados = Jugadores.subList(maximoJugadores, Jugadores.size());

            int delay = 0;
            int intervalo = 15;

            for (Player Jugador : JugadoresEliminados) {
                Bukkit.getScheduler().runTaskLater(plugin, () -> {
                    Location ubicacionJugador = Jugador.getLocation();
                    World mundo = Jugador.getWorld();

                    launchFirework(mundo, ubicacionJugador);

                    Jugador.kickPlayer(ChatColor.RED + "Fuiste eliminado por la purga!");

                    for (Player jugador : Jugadores) {
                        jugador.sendTitle(ChatColor.GOLD+Jugador.getName(), ChatColor.RED+"Eliminado",5, 15, 3);
                    }

                }, delay);

                delay += intervalo;
            }
            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                for (Player jugador : Jugadores) {
                    if (!(JugadoresEliminados.contains(jugador))) {
                        if(jugador.hasPotionEffect(PotionEffectType.LEVITATION)){
                            jugador.removePotionEffect(PotionEffectType.LEVITATION);
                        }
                        if(jugador.isOnline()){
                            jugador.playSound(jugador, Sound.ENTITY_PLAYER_LEVELUP, 1, 1);
                        }
                    }
                }
                player.sendMessage(ChatColor.GREEN + "La purga se ha completado. " + maximoJugadores + " jugadores restantes.");
            }, delay + 20);

            }, 20 * 4);
        } else {
            player.sendMessage(ChatColor.YELLOW + "Actualmente hay " + Jugadores.size() + " jugadores en linea, no es necesario hacer purga.");
        }
    }

    public void launchFirework(World world, Location locPlayer) {
        int durationFireworks = (plugin.getConfigs().duracionFireworks());

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

        Bukkit.getScheduler().runTaskLater(plugin, firework::detonate, 1L);
    }

    private void helpMessage(Player player) {
        player.sendMessage(ChatColor.RED + "-- Comandos --");
        player.sendMessage(ChatColor.GOLD + "/strive host");
        player.sendMessage(ChatColor.GOLD + "/strive reload");
    }
}

