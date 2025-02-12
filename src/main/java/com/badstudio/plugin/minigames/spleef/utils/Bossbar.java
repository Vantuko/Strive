package com.badstudio.plugin.minigames.spleef.utils;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;

public class Bossbar {
    private final Map<Player, BossBar> bossbars = new HashMap<>();
    private final Map<Player, BossBar> bossbars2 = new HashMap<>();
    private final JavaPlugin plugin;
    private final int tiempoInicial;
    private int tiempoRestante;
    private BukkitRunnable task;

    public Bossbar(JavaPlugin plugin, int tiempoInicial) {
        this.plugin = plugin;
        this.tiempoInicial = tiempoInicial;
        this.tiempoRestante = tiempoInicial;
    }
    //Método para añadir la bossbar
    public void agregarBossbar(Player jugador) {
        if (!bossbars.containsKey(jugador)) {
            BossBar bossBar = Bukkit.createBossBar(
                    ChatColor.AQUA + "Tiempo restante: " + Tiempo(tiempoRestante),
                     BarColor.BLUE,
                     BarStyle.SOLID
            );
            bossBar.addPlayer(jugador);
            bossbars.put(jugador, bossBar);
        }
    }
    public void agregarBossbar2(Player jugador) {
        if (!bossbars2.containsKey(jugador)) {
            BossBar bossBar2 = Bukkit.createBossBar(
              ChatColor.AQUA + "Tiempo restate: 25 segundos",
                    BarColor.BLUE,
                    BarStyle.SOLID
            );
            bossBar2.addPlayer(jugador);
            bossbars2.put(jugador, bossBar2);
        }
    }   
    public void Inicio2() {
        new BukkitRunnable() {
            private int tiempoEspera = 25;

            @Override
            public void run() {
                if (tiempoEspera <= 0) {
                    eliminarBossbar2(); // Eliminar la bossbar de espera
                    cancel();
                    return;
                }

                for (Map.Entry<Player, BossBar> entry : bossbars2.entrySet()) {
                    BossBar bossBar2 = entry.getValue();
                    bossBar2.setTitle(ChatColor.AQUA + "Tiempo restante: " + tiempoEspera + " segundos");
                    bossBar2.setProgress((double) tiempoEspera / 25.0);
                }

                tiempoEspera--;
            }
        }.runTaskTimer(plugin, 0L, 20L);
    }
    //Configura al inicio la bossbar
    public void Inicio() {
        if (task != null && !task.isCancelled()) {
            return;
        }
        task = new BukkitRunnable() {
            @Override
            public void run() {
                if (tiempoRestante <= 0) {
                    Finalizacion();
                    return;
                }
                for (Map.Entry<Player, BossBar> entry : bossbars.entrySet()) {
                    BossBar bossBar = entry.getValue();
                    bossBar.setTitle(ChatColor.AQUA + "Tiempo restante: " + Tiempo(tiempoRestante));
                    double progress = Math.max(0.0, Math.min(1.0, (double) tiempoRestante / tiempoInicial));
                    bossBar.setProgress(progress);
                }
                tiempoRestante--;
            }
        };
        task.runTaskTimer(plugin, 0L, 20L);
    }
    //Método que elimina la segunda bossbar
    public void eliminarBossbar2() {
        for (BossBar bossBar2 : bossbars2.values()) {
            bossBar2.setVisible(false);
            bossBar2.removeAll();
        }
        bossbars2.clear();
    }
    //Remueve la bossbar cuando termine el tiempo
    public void Finalizacion() {
        if (task != null) {
            task.cancel();
        }
        for (BossBar bossBar : bossbars.values()) {
            bossBar.setVisible(false);
            bossBar.removeAll();
        }
        bossbars.clear();
    }
    //Método para configurar el tiempo de la bossbar
    private String Tiempo(int segundos) {
        int minutos = segundos / 60;
        int seg = segundos % 60;
        return String.format("%02d:%02d", minutos, seg);
    }
}
