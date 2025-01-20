package com.badstudio.purga.minigames;

import com.badstudio.purga.Main;
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


        int x1 = (plugin.getConfig().getInt("spleef.pos1.x1")), y1 = (plugin.getConfig().getInt("spleef.pos1.y1")), z1 = (plugin.getConfig().getInt("spleef.pos1.z1")); //Pos 1
        int x2 = (plugin.getConfig().getInt("spleef.pos2.x2")), y2 = (plugin.getConfig().getInt("spleef.pos2.y2")), z2 = (plugin.getConfig().getInt("spleef.pos2.z2")); //Pos 2

        World mundo = Bukkit.getWorld("Spleef");

        if (mundo != null) {
            Inicio(mundo, x1, y1, z1, x2, y2, z2);
        } else {
            Bukkit.getLogger().warning("¡El mundo especificado no existe!");

        }
        return true;
    }

    private void Inicio(World mundo, int x1, int y1, int z1, int x2, int y2, int z2) {
        // Define la duración de la cuenta regresiva (en segundos)
        final int[] tiempoRestante = {plugin.getConfig().getInt("spleef.duracionInicio")}; // Cambia a la duración deseada


        // Programa una tarea repetitiva para la cuenta regresiva
        new BukkitRunnable() {
            @Override
            public void run() {
                // Reduce el tiempo restante
                tiempoRestante[0]--;

                // Muestra la cuenta regresiva a todos los jugadores
                for (Player jugador : mundo.getPlayers()) {
                    jugador.sendTitle(ChatColor.RED + "" + ChatColor.BOLD +tiempoRestante[0],"", 5, 10, 5);
                    jugador.playSound(jugador, Sound.BLOCK_NOTE_BLOCK_HAT, 1, 1);
                }

                // Cuando el tiempo llegue a 0, destruye los bloques y cancela la tarea
                if (tiempoRestante[0] <= 0) {
                    for (Player jugador : mundo.getPlayers()){
                        jugador.playSound(jugador, Sound.BLOCK_NOTE_BLOCK_BIT, 1, 2);
                        jugador.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_FALLING, 20*3, 0));
                        Bukkit.getScheduler().runTaskLater(plugin, palaSpleef(jugador), 20*3L);
                    }

                    destruirBloques(mundo, x1, y1, z1, x2, y2, z2);

                    // Cancela la tarea
                    cancel();
                }
            }
        }.runTaskTimer(plugin, 0L, 20L); // Ejecuta cada 20 ticks (1 segundo)
    }

    // Método para destruir los bloques
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

    private Runnable palaSpleef(Player player){
        return new Runnable() {
            @Override
            public void run() {
                Inventory playerInventory = player.getInventory();
                ItemStack palaMadera = new ItemStack(Material.WOODEN_SHOVEL);
                ItemMeta metaPalaMadera = palaMadera.getItemMeta();


                palaMadera.setItemMeta(metaPalaMadera);
                playerInventory.setItem(0, palaMadera);
            }
        };
    }
}


