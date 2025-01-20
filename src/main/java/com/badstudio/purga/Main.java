package com.badstudio.purga;

import com.badstudio.purga.commands.StriveTabCompleter;
import com.badstudio.purga.listeners.Events;
import com.badstudio.purga.minigames.spleef.Spleef;
import com.badstudio.purga.minigames.spleef.LaPala;
import com.badstudio.purga.utils.GuardarYCargarMapa;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import com.badstudio.purga.commands.StriveComandos;
import com.badstudio.purga.utils.Config;
import org.bukkit.Bukkit;

public final class Main extends JavaPlugin {

    private Config config;
    private GuardarYCargarMapa mapaManager;

    @Override
    public void onEnable() {

        getLogger().info("El plugin ha sido habilitado!");

        //Registro de la config
        config = new Config(this);

        this.mapaManager = new GuardarYCargarMapa(this);

        //Registro de comandos
        getCommand("strive").setExecutor(new StriveComandos(this));
        getCommand("spleef").setExecutor(new Spleef(this));
        getCommand("strive").setTabCompleter(new StriveTabCompleter(this));

        //Registro de eventos
        PluginManager pm = Bukkit.getServer().getPluginManager();
        pm.registerEvents(new Events(), this);
        pm.registerEvents(new LaPala(), this);
    }

    @Override
    public void onDisable() {
        getLogger().info("El plugin ha sido deshabilitado");
    }

    public GuardarYCargarMapa getMapaManager() {
        return mapaManager;
    }

    public Config getConfigs() {
        return config;
    }
}



