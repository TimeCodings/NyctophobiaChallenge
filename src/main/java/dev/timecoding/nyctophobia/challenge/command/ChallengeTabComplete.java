package dev.timecoding.nyctophobia.challenge.command;

import dev.timecoding.nyctophobia.challenge.NyctophobiaChallenge;
import dev.timecoding.nyctophobia.challenge.config.ConfigManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.ArrayList;
import java.util.List;

public class ChallengeTabComplete implements TabCompleter {
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        if(command.getName().equalsIgnoreCase("nycchallenge") || command.getName().equalsIgnoreCase("nycc") || command.getName().equalsIgnoreCase("nyctophobiachallenge")){
           if(args.length == 1) {
               ArrayList<String> list = new ArrayList<>();
               boolean b = false;
               if (sender.hasPermission(getPermission("OPPerm"))) {
                   b = true;
               }
               if (sender.hasPermission(getPermission("Toggle")) || b) {
                   list.add("toggle");
               }
               if (sender.hasPermission(getPermission("Start")) || b) {
                   list.add("start");
               }
               if (sender.hasPermission(getPermission("Pause")) || b) {
                   list.add("pause");
               }
               if (sender.hasPermission(getPermission("Stop")) || b) {
                   list.add("stop");
               }
               if (sender.hasPermission(getPermission("Reload")) || b) {
                   list.add("reload");
               }
               if (sender.hasPermission(getPermission("Restore")) || b) {
                   list.add("restore");
               }
               if (sender.hasPermission(getPermission("Save")) || b) {
                   list.add("save");
               }
               if (sender.hasPermission(getPermission("Help")) || b) {
                   list.add("help");
               }
               return list;
           }
        }
        return null;
    }
    private ConfigManager cfg = NyctophobiaChallenge.plugin.getPluginConfig();

    public String getPermission(String key){
        return cfg.getString("Command."+key+".Permission");
    }

    public String getMessage(String key){
        return cfg.getString("Command."+key+".Message");
    }

    public String getAlreadyMessage(String key){
        return cfg.getString("Command."+key+".AlreadyMessage");
    }
}
