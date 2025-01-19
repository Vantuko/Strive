package com.badstudio.purga.commands;

import com.badstudio.purga.Main;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.ArrayList;
import java.util.List;

public class StriveTabCompleter implements TabCompleter {

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args){
        final Main plugin = null;
        
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
