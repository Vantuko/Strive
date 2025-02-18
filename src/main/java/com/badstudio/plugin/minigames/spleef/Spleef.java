package com.badstudio.plugin.minigames.spleef;

import com.badstudio.plugin.Main;
import com.badstudio.plugin.minigames.spleef.utils.Bossbar;
import com.badstudio.plugin.utils.GuardarMapa;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Spleef implements CommandExecutor {
    private final Main plugin;
    private final GuardarMapa guardarMapa = new GuardarMapa();
    private Bossbar bossbar;
    private static boolean juegoActivo = false;

    public Spleef(Main plugin) {
        this.plugin = plugin;
        this.bossbar = new Bossbar(plugin, plugin.getConfig().getInt("spleef.duracionJuego"));
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

            tpAll();

            // Asegurarse de que todos los jugadores estén en modo Aventura
            for (Player jugador : mundo.getPlayers()) {
                jugador.setGameMode(GameMode.ADVENTURE);
            }
            for (Player jugador : mundo.getPlayers()) {
                bossbar.agregarBossbar2(jugador);
            }
            bossbar.Inicio2();

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
            Bukkit.getScheduler().runTaskLater(plugin, () -> inicio(mundo, x1, y1, z1, x2, y2, z2), 20 * 15L);
        } else {
            sender.sendMessage(ChatColor.RED + "Error al encontrar el mundo!");
        }
        return true;
    }
    public static void TP(List<Player> jugadores, String mundo, int X, int Y, int Z) {
        if (!Spleef.isJuegoActivo()) {
            return;
        }
        World world = Bukkit.getWorld(mundo);
        if (world == null) {
            Bukkit.getLogger().warning("El mundo " + mundo + " no existe.");
            return;
        }
        for (Player jugador : jugadores) {
            jugador.teleport(world.getBlockAt(X, Y, Z).getLocation());
        }
    }
    public static void tpAll() {
        List<Player> jugadores = new ArrayList<>(Bukkit.getOnlinePlayers());
        TP(jugadores, "Spleef", 0, 156, 0);
    }
    private void inicio(World mundo, int x1, int y1, int z1, int x2, int y2, int z2) {
        final int tiempoTotal = plugin.getConfig().getInt("spleef.duracionJuego");

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
                    //Asegura que todos los jugadores estén en survival
                    for (Player jugador : mundo.getPlayers()) {
                        jugador.setGameMode(GameMode.SURVIVAL);
                    }

                    for (Player jugador : mundo.getPlayers()) {
                        jugador.playSound(jugador, Sound.BLOCK_NOTE_BLOCK_BIT, 1, 2);
                        jugador.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_FALLING, 20 * 3, 0));

                        bossbar.agregarBossbar(jugador);

                        Bukkit.getScheduler().runTaskLater(plugin, () -> {
                            darPala(jugador);
                            bossbar.Inicio();
                            inicializarBorde(mundo);
                            Caida(mundo, 70);

                        }, 20 * 3L);

                        eliminarBloques(mundo);
                    }

                    destruirBloques(mundo, x1, y1, z1, x2, y2, z2);

                    Bukkit.getScheduler().runTaskLater(plugin, () -> convertirBloquesHielo(mundo, x1, y1, z1, x2, y2, z2), 20 * (plugin.getConfig().getLong("spleef.duracionJuego")));

                    Bukkit.getScheduler().runTaskLater(plugin, () -> {
                        setJuegoActivo(false);

                        guardarMapa.restaurarMapa(mundo);
                        bossbar.Finalizacion();
                        restaurarBorde(mundo);

                    }, 20 * (plugin.getConfig().getLong("spleef.duracionJuego")));

                    cancel();
                }
            }
        }.runTaskTimer(plugin, 0L, 20L);
    }
    private void convertirBloquesHielo(World mundo, int x1, int y1, int z1, int x2, int y2, int z2) {
        int minX = Math.min(x1, x2);
        int maxX = Math.max(x1, x2);
        int minY = Math.min(y1, y2);
        int maxY = Math.max(y1, y2);
        int minZ = Math.min(z1, z2);
        int maxZ = Math.max(z1, z2);
        for (int x = minX; x <= maxX; x++) {
            for (int y = minY; y <= maxY; y++) {
                for (int z = minZ; z <= maxZ; z++) {
                    Block block = mundo.getBlockAt(x, y, z);    
                    if (block.getType() == Material.SNOW_BLOCK || block.getType() == Material.SNOW) {
                        block.setType(Material.ICE);
                    }
                }
            }
        }
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            for (int x = minX; x <= maxX; x++) {
                for (int y = minY; y <= maxY; y++) {
                    for (int z = minZ; z <= maxZ; z++) {
                        Block block = mundo.getBlockAt(x, y, z);
                        if (block.getType() == Material.ICE) {
                            block.setType(Material.AIR);
                        }
                    }
                }
            }
            }, 20 * 30L);
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
    private void eliminarBloques(World mundo) {
        int[] tiempos = {240, 180, 120, 60};
        for (int tiempo : tiempos) {
            Bukkit.getScheduler().runTaskLater(plugin, () -> eliminarBloquesAleatorio(mundo), tiempo * 20L);
        }
    }
    public void restaurarBorde(World mundo) {
        WorldBorder border = mundo.getWorldBorder();
        border.setSize(100);
    }
    private void eliminarBloquesAleatorio(World mundo) {
        List<Block> bloquesValidos = new ArrayList<>();
        Random random = new Random();
        agregarBloques(bloquesValidos, mundo, -7, 139, -7, 7, 137, 7);              
        agregarBloques(bloquesValidos, mundo, -16, 129, -16, 16, 129, 16);  
        agregarBloques(bloquesValidos, mundo, -20, 121, -20, 20, 121, 20);  
        agregarBloques(bloquesValidos, mundo, -22, 116, -22, 22, 116, 22);  
        agregarBloques(bloquesValidos, mundo, -15, 111, -15, 15, 110, 15);  
        agregarBloques(bloquesValidos, mundo, -26, 102, -26, 26, 101, 26);  
        agregarBloques(bloquesValidos, mundo, -28, 95, -28, 28, 94, 28);     
        agregarBloques(bloquesValidos, mundo, -30, 86, -30, 30, 85, 30);

        List<List<Block>> capas = new ArrayList<>();
        capas.add(new ArrayList<>()); // Capa 1
        capas.add(new ArrayList<>()); // Capa 2
        capas.add(new ArrayList<>()); // Capa 3
        capas.add(new ArrayList<>()); // Capa 4
        capas.add(new ArrayList<>()); // Capa 5
        capas.add(new ArrayList<>()); // Capa 6
        capas.add(new ArrayList<>()); // Capa 7
        capas.add(new ArrayList<>()); // Capa 8

        for (Block block : bloquesValidos) {
            int y = block.getY();
            if (y >= 137 && y <= 139) {
                capas.getFirst().add(block);  // Primera capa
            } else if (y >= 129 && y <= 137) {
                capas.get(1).add(block);  // Segunda capa
            } else if (y >= 121 && y <= 129) {
                capas.get(2).add(block);  // Tercera capa
            } else if (y >= 116 && y <= 121) {
                capas.get(3).add(block);  // Cuarta capa
            } else if (y >= 110 && y <= 116) {
                capas.get(4).add(block);  // Quinta capa
            } else if (y >= 101 && y <= 110) {
                capas.get(5).add(block);  // Sexta capa
            } else if (y >= 94 && y <= 101) {
                capas.get(6).add(block);  // Séptima capa
            } else if (y >= 85 && y <= 94) {
                capas.get(7).add(block);  // Octava capa
            }
        }
        for (List<Block> capa : capas) {
            if (!capa.isEmpty()) {
                int toRemove = Math.min(plugin.getConfig().getInt("spleef.bloques_removidos"), capa.size());
                for (int i = 0; i < toRemove; i++) {
                    int randomIndex = random.nextInt(capa.size());
                    Block block = capa.remove(randomIndex);
                    block.setType(Material.ICE);

                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            block.setType(Material.AIR);
                        }
                    }.runTaskLater(plugin, 3 * 20L);
                }
            }
        }
    }
    private void agregarBloques(List<Block> lista, World mundo, int x1, int y1, int z1, int x2, int y2, int z2) {
        int minX = Math.min(x1, x2);
        int maxX = Math.max(x1, x2);
        int minY = Math.min(y1, y2);
        int maxY = Math.max(y1, y2);
        int minZ = Math.min(z1, z2);
        int maxZ = Math.max(z1, z2);

        for (int y = minY; y <= maxY; y++) {
            for (int x = minX; x <= maxX; x++) {
                for (int z = minZ; z <= maxZ; z++) {
                    Block block = mundo.getBlockAt(x, y, z);
                    if (block.getType() == Material.SNOW_BLOCK || block.getType() == Material.SNOW) {
                        lista.add(block);
                    }
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
    public void Caida(World mundo, int alturaMuerte) {
        new BukkitRunnable() {
            @Override
            public void run() {
                if (!Spleef.isJuegoActivo()) {
                    cancel();
                    return;
                }

                for (Player jugador : mundo.getPlayers()) {
                    if (jugador.getGameMode() == GameMode.SURVIVAL && jugador.getLocation().getY() <= alturaMuerte) {
                        jugador.damage(jugador.getHealth() + 1);
                        Bukkit.getScheduler().runTaskLater(plugin, () -> jugador.setGameMode(GameMode.SPECTATOR), 2L);
                    }
                }
            }
        }.runTaskTimer(plugin, 0L,5L);
    }
    private void mostrarMensajeSpleef() {
        String mensaje = ChatColor.translateAlternateColorCodes('&',
                "------------\n" +
                        net.md_5.bungee.api.ChatColor.of("#CAE3E6") + "[❄] " + net.md_5.bungee.api.ChatColor.of("#CAE3E6") + "Spleef &f[" + net.md_5.bungee.api.ChatColor.of("#CAE3E6") + "❄&f]\n" +
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
