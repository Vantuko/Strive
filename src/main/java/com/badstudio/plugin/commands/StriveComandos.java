package com.badstudio.plugin.commands;

import com.badstudio.plugin.Main;
import com.badstudio.plugin.minigames.spleef.Spleef;

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

import org.jetbrains.annotations.NotNull;

public class StriveComandos implements CommandExecutor {

    private final Main plugin;

    public StriveComandos(Main plugin) {
        this.plugin = plugin;
    }
    //Método para registrar comandos
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {

        Player player = (Player) sender;
    //Mensaje de ayuda al poner /strive sin ningún argumento
        if (args.length == 0) {
            helpMessage(player);
            return true;
        }
    //Comando /strive Host
        switch (args[0].toLowerCase()) {
            case "host":
                Host(player);
                break;
    //Comando /strive reload
            case "reload":
                plugin.reloadConfig();
                player.sendMessage(ChatColor.GREEN + "Configuración reiniciada");
                break;
    //Si es que se añade un argumento no valido
            default:
                player.sendMessage(ChatColor.RED + "Comando desconocido, utiliza /strive");
                break;
        }
        return true;
    }
    //Método de /strive host
    private void Host(Player player) {
        List<Player> Jugadores = new ArrayList<>(Bukkit.getOnlinePlayers());
        //Detecta el maximo de jugadores en la purga
        int maximoJugadores = (plugin.getConfigs().maximoJugadoresPurga());

        if (Jugadores.size() > maximoJugadores) {
            for (Player jugador : Jugadores) {
                //Añade levitación a todos los jugadores al inicio de la purga
                jugador.addPotionEffect(new PotionEffect(PotionEffectType.LEVITATION,
                        Integer.MAX_VALUE,
                        1,
                        false,
                        false));
            }
            Bukkit.getScheduler().runTaskLater(plugin, () -> {

            Collections.shuffle(Jugadores);
            List<Player> JugadoresEliminados = Jugadores.subList(maximoJugadores, Jugadores.size());

            int delay = 0;
            int intervalo = 15;
            //Detecta a los jugadores eliminados
            for (Player Jugador : JugadoresEliminados) {
                Bukkit.getScheduler().runTaskLater(plugin, () -> {
                    Location ubicacionJugador = Jugador.getLocation();
                    World mundo = Jugador.getWorld();
                    //Lanza un cohete a los jugadores eliminados
                    launchFirework(mundo, ubicacionJugador);
                    //Kickea a los jugadores eliminados
                    Jugador.kickPlayer(ChatColor.RED + "Fuiste eliminado por la purga!");
                    //Envía un título a los jugadores de las personas eliminadas
                    for (Player jugador : Jugadores) {
                        jugador.sendTitle(ChatColor.GOLD+Jugador.getName(), ChatColor.RED+"Eliminado",5, 15, 3);
                    }

                }, delay);

                delay += intervalo;
            }
            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                for (Player jugador : Jugadores) {
                    //Remueve la levitación a los jugadores sobrevivientes
                    if (!(JugadoresEliminados.contains(jugador))) {
                        if(jugador.hasPotionEffect(PotionEffectType.LEVITATION)){
                            jugador.removePotionEffect(PotionEffectType.LEVITATION);
                        }
                        //Añade un sonido al terminar la purga
                        if(jugador.isOnline()){
                            jugador.playSound(jugador, Sound.ENTITY_PLAYER_LEVELUP, 1, 1);
                        }
                    }
                }
                //Envía un mensaje al jugador que ejecuto el comando que la purga se completó
                player.sendMessage(ChatColor.GREEN + "La purga se ha completado. " + maximoJugadores + " jugadores restantes.");
            }, delay + 20);

            }, 20 * 4);
        } else {
            player.sendMessage(ChatColor.YELLOW + "Actualmente hay " + Jugadores.size() + " jugadores en linea, no es necesario hacer purga.");
        }
    }
    //Método para modificar el cohete
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
        //Explota al momento de spawnear el cohete
        Bukkit.getScheduler().runTaskLater(plugin, firework::detonate, 1L);
    }
    //Método si no se utiliza ningún argumento
    private void helpMessage(Player player) {
        player.sendMessage(ChatColor.RED + "-- Comandos --");
        player.sendMessage(ChatColor.GOLD + "/strive host");
        player.sendMessage(ChatColor.GOLD + "/strive reload");
    }
    public static void TP(List<Player> jugadores, String mundo, int X, int Y, int Z) {
        for (Player jugador : jugadores ) {
            jugadores = new ArrayList<>(Bukkit.getOnlinePlayers());
            if (Spleef.isJuegoActivo()){
                StriveComandos.TP(jugadores, "Spleef", 0, 156, 0);
            }
        }
    }
}

