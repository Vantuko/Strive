package com.badstudio.plugin.utils;

import org.bukkit.plugin.java.JavaPlugin;

public class Config {

    private final JavaPlugin plugin;

    public Config(JavaPlugin plugin) {
        this.plugin = plugin;

        plugin.saveDefaultConfig();
    }


    public int maximoJugadoresPurga() {
        return plugin.getConfig().getInt("purga.jugadores_maximos");
    }
    
    public int duracionFireworks() {
        return plugin.getConfig().getInt("purga.duracion_fireworks");
    }
    
}
