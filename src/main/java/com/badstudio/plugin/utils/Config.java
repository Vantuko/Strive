package com.badstudio.plugin.utils;

import org.bukkit.plugin.java.JavaPlugin;

public class Config {

    private final JavaPlugin plugin;

    //Método para guardar la configuración
    public Config(JavaPlugin plugin) {
        this.plugin = plugin;

        plugin.saveDefaultConfig();
    }
    //Registro de configuraciones
    public int maximoJugadoresPurga() {
        return plugin.getConfig().getInt("purga.jugadores_maximos");
    }
    public int duracionFireworks() {
        return plugin.getConfig().getInt("purga.duracion_fireworks");
    }
}
