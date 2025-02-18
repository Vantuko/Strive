package com.badstudio.plugin.commands;

import com.badstudio.plugin.Main;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import org.jetbrains.annotations.NotNull;

public class StriveComandos implements CommandExecutor {

    private final Main plugin;

    public StriveComandos(Main plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(ChatColor.RED + "Este comando solo puede ser ejecutado por un jugador.");
            return true;
        }

        if (args.length == 0) {
            helpMessage(player);
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "host":
                // Se abre el GUI con las dos opciones
                plugin.getHostGUI().openMainGUI(player);
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

    private void helpMessage(Player player) {
        player.sendMessage(ChatColor.RED + "-- Comandos --");
        player.sendMessage(ChatColor.GOLD + "/strive host");
        player.sendMessage(ChatColor.GOLD + "/strive reload");
    }
}
