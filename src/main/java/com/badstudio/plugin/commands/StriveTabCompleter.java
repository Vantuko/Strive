package com.badstudio.plugin.commands;

import com.badstudio.plugin.Main;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.ArrayList;
import java.util.List;

import org.jetbrains.annotations.NotNull;

public class StriveTabCompleter implements TabCompleter {

    private final Main plugin;

    public StriveTabCompleter(Main plugin) {
        this.plugin = plugin;
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, Command command, @NotNull String alias, String[] args){

        
        List<String> suggestions = new ArrayList<>();
            if(command.getName().equalsIgnoreCase("strive")){
                if(args.length == 1){
                    suggestions.addAll(plugin.getConfig().getStringList("strive-commands"));
                    suggestions.removeIf(s -> !s.startsWith(args[0].toLowerCase()));
                }
            }


        return suggestions;
    }
}
