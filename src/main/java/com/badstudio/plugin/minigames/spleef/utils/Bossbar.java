package com.badstudio.plugin.minigames.spleef.utils;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

public class Bossbar {
    private final BossBar bossbar;
    private final JavaPlugin plugin;
    private final int tiempoInicial;
    private int tiempoRestante;
    private BukkitRunnable task;

    public Bossbar(JavaPlugin plugin, String title, int tiempoInicial) {
        this.plugin = plugin;
        this.tiempoInicial = tiempoInicial;
        this.tiempoRestante = plugin.getConfig().getInt("spleef.duracionJuego");
        this.bossbar = Bukkit.createBossBar(
                ChatColor.AQUA + title + Tiempo(tiempoRestante),
                BarColor.BLUE,
                BarStyle.SOLID
        );

        for (Player player : Bukkit.getOnlinePlayers()) {
            if (player.getWorld().getName().equalsIgnoreCase("Spleef")){
                bossbar.addPlayer(player);
            }
        }
    }

    public void Inicio() {
        task = new BukkitRunnable() {
            @Override
            public void run() {
                if (tiempoRestante <= 0) {
                    Finalizacion();
                    return;
                }

                bossbar.setTitle(ChatColor.AQUA + "Tiempo restante: " + Tiempo(tiempoRestante));
                double progress = (double) tiempoRestante / tiempoInicial;
                bossbar.setProgress(progress);
                tiempoRestante--;
            }
        };

        task.runTaskTimer(plugin, 0L, 20L);
    }


    public void Finalizacion() {
        if (task != null) {
            task.cancel();
        }
        bossbar.setVisible(false);
        bossbar.removeAll();
    }

    private String Tiempo(int segundos) {
        int minutos = segundos / 60;
        int seg = segundos % 60;
        return String.format("%02d:%02d", minutos, seg);
    }
}
