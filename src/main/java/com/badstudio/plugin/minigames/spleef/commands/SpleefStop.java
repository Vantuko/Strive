package com.badstudio.plugin.minigames.spleef.commands;

import com.badstudio.plugin.minigames.spleef.Spleef;
import com.badstudio.plugin.minigames.spleef.utils.Bossbar;
import com.badstudio.plugin.utils.GuardarMapa;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import org.jetbrains.annotations.NotNull;

public class SpleefStop implements CommandExecutor {
    private final GuardarMapa guardarMapa = new GuardarMapa();
    private Bossbar bossbar;

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        guardarMapa.restaurarMapa(Bukkit.getWorld("Spleef"));
        guardarMapa.limpiarDatos();
        bossbar.Finalizacion();
        Spleef.setJuegoActivo(false);
        return false;
    }
}
