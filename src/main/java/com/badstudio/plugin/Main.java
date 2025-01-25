package com.badstudio.plugin;

import com.badstudio.plugin.commands.StriveTabCompleter;
import com.badstudio.plugin.listeners.Events;
import com.badstudio.plugin.minigames.spleef.Spleef;
import com.badstudio.plugin.minigames.spleef.listeners.GhostPlayerListener;
import com.badstudio.plugin.minigames.spleef.listeners.SpleefListener;
import com.badstudio.plugin.minigames.spleef.utils.ScoreManager;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import com.badstudio.plugin.commands.StriveComandos;
import com.badstudio.plugin.utils.Config;
import org.bukkit.Bukkit;

import java.util.Objects;

public final class Main extends JavaPlugin {

    private Config config;

    @Override
    public void onEnable() {

        getLogger().info("El plugin ha sido habilitado!");

        // Registro de la configuración
        config = new Config(this);

        ScoreManager scoreManager = new ScoreManager();
        GhostPlayerListener ghostPlayerListener = new GhostPlayerListener(this, scoreManager);

        // Registro de comandos
        Objects.requireNonNull(getCommand("strive")).setExecutor(new StriveComandos(this));
        Objects.requireNonNull(getCommand("spleef")).setExecutor(new Spleef(this));
        Objects.requireNonNull(getCommand("strive")).setTabCompleter(new StriveTabCompleter(this));

        // Registro de eventos
        PluginManager pm = Bukkit.getServer().getPluginManager();
        pm.registerEvents(new Events(), this);
        pm.registerEvents(new SpleefListener(ghostPlayerListener, this), this);
        pm.registerEvents(ghostPlayerListener, this); // Usa la misma instancia
    }


    @Override
    public void onDisable() {
        getLogger().info("El plugin ha sido deshabilitado");
    }

    public Config getConfigs() {
        return config;
    }
}



