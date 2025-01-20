package com.badstudio.purga.minigames;

import com.badstudio.purga.Main;
import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;


public class Spleef implements CommandExecutor {


    private final Main plugin;

    public Spleef(Main plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {


        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Este comando solo puede ser ejecutado por un jugador.");
            return true;
        }
        Player player = (Player) sender;


        int x1 = -8, y1 = 149, z1 = 8; //Pos 1
        int x2 = 8, y2 = 153, z2 = -8; //Pos 2

        World mundo = Bukkit.getWorld("Spleef");

        if (mundo != null) {
            Inicio(mundo, x1, y1, z1, x2, y2, z2);
            player.sendMessage("Se a ejecutado correctamente!");
        } else {
            Bukkit.getLogger().warning("¡El mundo especificado no existe!");

        }
        return true;
    }

    private void Inicio(World mundo, int x1, int y1, int z1, int x2, int y2, int z2) {
        int minX = Math.min(x1, x2);
        int maxX = Math.max(x1, x2);
        int minY = Math.min(y1, y2);
        int maxY = Math.max(y1, y2);
        int minZ = Math.min(z1, z2);
        int maxZ = Math.max(z1, z2);

        for (int x = minX; x <= maxX; x++) {
            for (int y = minY; y <= maxY; y++) {
                for (int z = minZ; z <= maxZ; z++) {
                    Location bloque = new Location(mundo, x, y, z);
                    mundo.getBlockAt(bloque).setType(Material.AIR);
                }
            }
        }
    }
}


