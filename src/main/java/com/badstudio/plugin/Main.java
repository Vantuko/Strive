package com.badstudio.plugin;

import com.badstudio.plugin.commands.StriveTabCompleter;
import com.badstudio.plugin.listeners.Events;
import com.badstudio.plugin.minigames.spleef.Spleef;
import com.badstudio.plugin.minigames.spleef.LaPala;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import com.badstudio.plugin.commands.StriveComandos;
import com.badstudio.plugin.utils.Config;
import org.bukkit.Bukkit;

public final class Main extends JavaPlugin {

    private Config config;

    @Override
    public void onEnable() {

        getLogger().info("El plugin ha sido habilitado!");

        //Registro de la config
        config = new Config(this);


        //Registro de comandos
        getCommand("strive").setExecutor(new StriveComandos(this));
        getCommand("spleef").setExecutor(new Spleef(this));
        getCommand("strive").setTabCompleter(new StriveTabCompleter(this));

        //Registro de eventos
        PluginManager pm = Bukkit.getServer().getPluginManager();
        pm.registerEvents(new Events(), this);
        pm.registerEvents(new LaPala(this), this);
    }

    @Override
    public void onDisable() {
        getLogger().info("El plugin ha sido deshabilitado");
    }

    public Config getConfigs() {
        return config;
    }
}



