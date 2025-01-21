package com.badstudio.plugin.minigames.spleef;

import com.badstudio.plugin.Main;
import com.badstudio.plugin.utils.GuardarMapa;
import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

public class Spleef implements CommandExecutor {
    private final Main plugin;
    private final GuardarMapa guardarMapa = new GuardarMapa();
    private static boolean juegoActivo = false; // Estado compartido del juego

    public Spleef(Main plugin) {
        this.plugin = plugin;
    }

    public static boolean isJuegoActivo() {
        return juegoActivo;
    }

    public static void setJuegoActivo(boolean estado) {
        juegoActivo = estado;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        World mundo = Bukkit.getWorld("Spleef");

        if (Bukkit.getWorlds().contains(mundo)) {
            if (!(sender instanceof Player)) {
                sender.sendMessage(ChatColor.RED + "Este comando solo puede ser ejecutado por un jugador.");
                return true;
            }
            Player player = (Player) sender;

            if (juegoActivo) {
                player.sendMessage(ChatColor.RED + "¡El juego ya está activo!");
                return true;
            }

            // Cambia el estado del juego a activo
            setJuegoActivo(true);
            player.sendMessage(ChatColor.GREEN + "¡El juego de Spleef ha comenzado!");

            // Cargar posiciones desde la configuración
            int x1 = plugin.getConfig().getInt("spleef.pos1.x1");
            int y1 = plugin.getConfig().getInt("spleef.pos1.y1");
            int z1 = plugin.getConfig().getInt("spleef.pos1.z1");
            int x2 = plugin.getConfig().getInt("spleef.pos2.x2");
            int y2 = plugin.getConfig().getInt("spleef.pos2.y2");
            int z2 = plugin.getConfig().getInt("spleef.pos2.z2");

            if (mundo != null) {
                Inicio(mundo, x1, y1, z1, x2, y2, z2);
            } else {
                Bukkit.getLogger().warning("¡El mundo especificado no existe!");
            }
        } else {
            sender.sendMessage(ChatColor.RED + "Error al encontrar el mundo!");
        }
        return true;
    }

    private void Inicio(World mundo, int x1, int y1, int z1, int x2, int y2, int z2) {
        final int[] tiempoRestante = {plugin.getConfig().getInt("spleef.duracionInicio")};

        new BukkitRunnable() {
            @Override
            public void run() {
                tiempoRestante[0]--;

                if (tiempoRestante[0] > 0) {
                    for (Player jugador : mundo.getPlayers()) {
                        jugador.sendTitle(ChatColor.RED + "" + ChatColor.BOLD + tiempoRestante[0], "", 5, 10, 5);
                        jugador.playSound(jugador, Sound.BLOCK_NOTE_BLOCK_HAT, 1, 1);
                    }
                }
                if (tiempoRestante[0] == 0) {
                    for (Player jugador : mundo.getPlayers()) {
                        jugador.playSound(jugador, Sound.BLOCK_NOTE_BLOCK_BIT, 1, 2);
                        jugador.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_FALLING, 20 * 3, 0));
                    }

                    destruirBloques(mundo, x1, y1, z1, x2, y2, z2);

                    // Desactiva el juego después de 3 minutos
                    Bukkit.getScheduler().runTaskLater(plugin, () -> {
                        setJuegoActivo(false);
                        Bukkit.broadcastMessage(ChatColor.RED + "¡El juego de Spleef ha terminado!");
                    }, 20 * 180L); // 3 minutos en ticks

                    cancel();
                }
            }
        }.runTaskTimer(plugin, 0L, 20L);
    }

    private void destruirBloques(World mundo, int x1, int y1, int z1, int x2, int y2, int z2) {
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
