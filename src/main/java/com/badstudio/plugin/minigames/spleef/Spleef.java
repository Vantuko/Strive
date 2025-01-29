package com.badstudio.plugin.minigames.spleef;

import com.badstudio.plugin.Main;
import com.badstudio.plugin.minigames.spleef.listeners.GhostPlayerListener;
import com.badstudio.plugin.minigames.spleef.utils.BlockRemover;
import com.badstudio.plugin.minigames.spleef.utils.Bossbar;
import com.badstudio.plugin.minigames.spleef.utils.ScoreboardManager;
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

import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.UUID;

public class Spleef implements CommandExecutor {
    private final Main plugin;
    private final GuardarMapa guardarMapa = new GuardarMapa();
    private Bossbar bossbar;
    private static boolean juegoActivo = false;

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
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        World mundo = Bukkit.getWorld("Spleef");

        if (mundo != null && Bukkit.getWorlds().contains(mundo)) {
            if (!(sender instanceof Player)) {
                sender.sendMessage(ChatColor.RED + "Este comando solo puede ser ejecutado por un jugador.");
                return true;
            }

            setJuegoActivo(true);

            int x1 = plugin.getConfig().getInt("spleef.pos1.x1");
            int y1 = plugin.getConfig().getInt("spleef.pos1.y1");
            int z1 = plugin.getConfig().getInt("spleef.pos1.z1");
            int x2 = plugin.getConfig().getInt("spleef.pos2.x2");
            int y2 = plugin.getConfig().getInt("spleef.pos2.y2");
            int z2 = plugin.getConfig().getInt("spleef.pos2.z2");

            int x3 = plugin.getConfig().getInt("spleef.pos3.x1");
            int y3 = plugin.getConfig().getInt("spleef.pos3.y1");
            int z3 = plugin.getConfig().getInt("spleef.pos3.z1");
            int x4 = plugin.getConfig().getInt("spleef.pos4.x2");
            int y4 = plugin.getConfig().getInt("spleef.pos4.y2");
            int z4 = plugin.getConfig().getInt("spleef.pos4.z2");

            guardarMapa.guardarMapa(mundo, x3, y3, z3, x4, y4, z4);
            mostrarMensajeSpleef();
            Bukkit.getScheduler().runTaskLater(plugin, () -> inicio(mundo, x1, y1, z1, x2, y2, z2), 20 * 30L);
        } else {
            sender.sendMessage(ChatColor.RED + "Error al encontrar el mundo!");
        }
        return true;
    }
    private void inicio(World mundo, int x1, int y1, int z1, int x2, int y2, int z2) {
        final int tiempoTotal = plugin.getConfig().getInt("spleef.duracionJuego");
        ScoreboardManager ScoreManager = new ScoreboardManager();
        GhostPlayerListener manejoClaseGhost = new GhostPlayerListener(plugin, ScoreManager);

        // Asegurarse de que todos los jugadores estén en modo SURVIVAL
        for (Player jugador : mundo.getPlayers()) {
            jugador.setGameMode(GameMode.SURVIVAL);
        }

        // Iniciar el BlockRemover
        BlockRemover blockRemover = new BlockRemover(plugin, mundo, x1, x2, y1, y2, z1, z2);

        new BukkitRunnable() {
            private int tiempoRestante = plugin.getConfig().getInt("spleef.duracionInicio");

            @Override
            public void run() {
                tiempoRestante--;

                if (tiempoRestante > 0) {
                    for (Player jugador : mundo.getPlayers()) {
                        jugador.sendTitle(ChatColor.RED +"Comenzando en:", String.valueOf(tiempoRestante), 5, 10, 5);
                        jugador.playSound(jugador, Sound.BLOCK_NOTE_BLOCK_HAT, 1, 1);
                    }
                }
                if (tiempoRestante == 0) {
                    bossbar = new Bossbar(plugin, tiempoTotal);

                    for (Player jugador : mundo.getPlayers()) {
                        jugador.playSound(jugador, Sound.BLOCK_NOTE_BLOCK_BIT, 1, 2);
                        jugador.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_FALLING, 20 * 3, 0));

                        bossbar.agregarBossbar(jugador);

                        Bukkit.getScheduler().runTaskLater(plugin, () -> {
                            darPala(jugador);
                            bossbar.Inicio();
                            inicializarBorde(mundo);
                            blockRemover.start();
                        }, 20 * 3L);
                    }

                    destruirBloques(mundo, x1, y1, z1, x2, y2, z2);

                    Bukkit.getScheduler().runTaskLater(plugin, () -> {
                        setJuegoActivo(false);

                        manejoClaseGhost.reiniciarEspectadores(); // Restaurar espectadores

                        for (Player jugador : mundo.getPlayers()) {
                            Inventory inventory = jugador.getInventory();

                            for (ItemStack item : inventory.getContents()) {
                                if (item != null && (item.getType() == Material.STONE_SHOVEL || item.getType() == Material.DIAMOND_SHOVEL || item.getType() == Material.SNOWBALL)) {
                                    inventory.remove(item);
                                }
                            }
                        }
                        guardarMapa.restaurarMapa(mundo);
                        guardarMapa.limpiarDatos();
                        bossbar.Finalizacion();
                    }, 20 * (plugin.getConfig().getLong("spleef.duracionJuego")));

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
    public void inicializarBorde(World mundo) {
        int initialSize = plugin.getConfig().getInt("spleef.world_border.initial_size");
        int finalSize = plugin.getConfig().getInt("spleef.world_border.final_size");
        int shrinkTime = plugin.getConfig().getInt("spleef.world_border.shrink_time");
        double damage = plugin.getConfig().getDouble("spleef.world_border.damage_per_second");

        WorldBorder border = mundo.getWorldBorder();
        border.setSize(initialSize);
        border.setCenter(0, 0);
        border.setDamageAmount(damage);

        border.setSize(finalSize, shrinkTime); // Achicar el borde
    }
    private void darPala(Player player) {
        ItemStack palaPiedra = new ItemStack(Material.STONE_SHOVEL);
        ItemMeta palaPiedraMeta = palaPiedra.getItemMeta();
        if (palaPiedraMeta != null) {
            palaPiedraMeta.setUnbreakable(true);
        }
        palaPiedra.setItemMeta(palaPiedraMeta);
        player.getInventory().setItem(0, palaPiedra);
    }
    private void mostrarMensajeSpleef() {
        String mensaje = ChatColor.translateAlternateColorCodes('&',
                "------------\n" +
                        net.md_5.bungee.api.ChatColor.of("#CAE3E6") + "[❄&f] " + net.md_5.bungee.api.ChatColor.of("#CAE3E6") + "Spleef &f[" + net.md_5.bungee.api.ChatColor.of("#CAE3E6") + "❄&f]\n" +
                        net.md_5.bungee.api.ChatColor.of("#CAE3E6") + "Trabaja con tu equipo para\n" +
                        net.md_5.bungee.api.ChatColor.of("#CAE3E6") + "romper los bloques bajo tus\n" +
                        net.md_5.bungee.api.ChatColor.of("#CAE3E6") + "oponentes y quedar último en pie\n" +
                        "&f&l¡" + net.md_5.bungee.api.ChatColor.of("#CAE3E6") + "Demuestra que equipo es mejor&f&l!\n" +
                        "-------------------------");

        for (Player jugador : Bukkit.getOnlinePlayers()) {
            if (jugador.getWorld().getName().equalsIgnoreCase("Spleef")) {
                jugador.sendMessage(mensaje);
            }
        }
    }


}
