package dev.timecoding.nyctophobia.challenge.command;

import dev.timecoding.nyctophobia.challenge.NyctophobiaChallenge;
import dev.timecoding.nyctophobia.challenge.api.ChallengeState;
import dev.timecoding.nyctophobia.challenge.api.ChallengeTimer;
import dev.timecoding.nyctophobia.challenge.config.ConfigManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

public class ChallengeCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
            String noperm = cfg.getString("NoPermission.Message");
            String opperm = cfg.getString("OPPerm.Permission");
            boolean enabled = cfg.getBoolean("Enabled");
            String m = ChatColor.RED+"The Plugin is disabled! Use /nycc toggle to enable it again!";
            if(args.length == 1){
                String name = args[0];
                if(name.equalsIgnoreCase("toggle")){
                    if(sender.hasPermission(getPermission("Toggle")) || sender.hasPermission(opperm)){
                        if(enabled){
                            sender.sendMessage(cfg.getString("Command.Toggle.MessageDisabled"));
                            cfg.set("Enabled", false);
                            cfg.reloadConfig();
                            NyctophobiaChallenge.plugin.setState(ChallengeState.STOPPED);
                        }else{
                            sender.sendMessage(cfg.getString("Command.Toggle.MessageEnabled"));
                            cfg.set("Enabled", true);
                            cfg.reloadConfig();
                        }
                    }else{
                        sender.sendMessage(noperm);
                    }
                }else if(name.equalsIgnoreCase("start") && sender instanceof Player){
                    if(enabled) {
                        if (sender.hasPermission(getPermission("Start")) || sender.hasPermission(opperm)) {
                            if (!NyctophobiaChallenge.plugin.getState().equals(ChallengeState.STARTED)) {
                                boolean clearInv = cfg.getBoolean("OnStart.ClearInventory");
                                String gamemode = cfg.getString("OnStart.GameMode");
                                String msg = cfg.getString("OnStart.Message");
                                sender.sendMessage(getMessage("Start"));
                                NyctophobiaChallenge.plugin.setState(ChallengeState.STARTED);
                                for (Player all : Bukkit.getOnlinePlayers()) {
                                    all.sendMessage(msg);
                                    if (clearInv) {
                                        all.getInventory().clear();
                                    }
                                    switch (gamemode) {
                                        case "SURVIVAL":
                                            all.setGameMode(GameMode.SURVIVAL);
                                            break;
                                        case "ADVENTURE":
                                            all.setGameMode(GameMode.ADVENTURE);
                                            break;
                                        case "CREATIVE":
                                            all.setGameMode(GameMode.CREATIVE);
                                            break;
                                        case "SPECTATOR":
                                            all.setGameMode(GameMode.SPECTATOR);
                                            break;
                                    }
                                }
                            } else {
                                sender.sendMessage(getAlreadyMessage("Start"));
                            }
                        } else {
                            sender.sendMessage(noperm);
                        }
                    }else{
                        sender.sendMessage(m);
                    }
                }else if(name.equalsIgnoreCase("pause") && sender instanceof Player){
                    if(sender.hasPermission(getPermission("Pause")) || sender.hasPermission(opperm)){
                        if(enabled){
                        if(!NyctophobiaChallenge.plugin.getState().equals(ChallengeState.PAUSED)) {
                            ChallengeTimer timer = NyctophobiaChallenge.plugin.timer;
                            sender.sendMessage(getMessage("Pause").replace("%player%", sender.getName()).replace("%time%", timer.getFullFormat()));
                            NyctophobiaChallenge.plugin.setState(ChallengeState.PAUSED);
                            String gamemode = cfg.getString("OnPause.GameMode");
                            String msg = cfg.getString("OnPause.Message");
                            sender.sendMessage(cfg.getString("OnPause.Message").replace("%player%", sender.getName()).replace("%time%", timer.getFullFormat()));
                            for (Player all : Bukkit.getOnlinePlayers()) {
                                switch (gamemode) {
                                    case "SURVIVAL":
                                        all.setGameMode(GameMode.SURVIVAL);
                                        break;
                                    case "ADVENTURE":
                                        all.setGameMode(GameMode.ADVENTURE);
                                        break;
                                    case "CREATIVE":
                                        all.setGameMode(GameMode.CREATIVE);
                                        break;
                                    case "SPECTATOR":
                                        all.setGameMode(GameMode.SPECTATOR);
                                        break;
                                }
                            }
                        }else{
                            sender.sendMessage(getAlreadyMessage("Pause"));
                        }
                        }else{
                            sender.sendMessage(m);
                        }
                    }else{
                        sender.sendMessage(noperm);
                    }
                }else if(name.equalsIgnoreCase("stop") && sender instanceof Player){
                    if(sender.hasPermission(getPermission("Stop")) || sender.hasPermission(opperm)){
                        if(enabled){
                        if(NyctophobiaChallenge.plugin.getState().equals(ChallengeState.STARTED) || NyctophobiaChallenge.plugin.getState().equals(ChallengeState.PAUSED)) {
                            sender.sendMessage(getMessage("Stop"));
                            ChallengeTimer timer = NyctophobiaChallenge.plugin.timer;
                            NyctophobiaChallenge.plugin.setState(ChallengeState.STOPPED);
                            boolean clearInv = cfg.getBoolean("OnDeath.ClearInventory");
                            String msgifend = cfg.getString("OnDeath.MessageIfEnd").replace("%player%", "?").replace("%time%", timer.getFullFormat()).replace("%reason%", cfg.getString("OnDeath.StoppedReason"));
                            String gamemode = cfg.getString("OnDeath.GameMode");
                            for(Player all : Bukkit.getOnlinePlayers()){
                                all.sendMessage(msgifend);
                                if(clearInv){
                                    all.getInventory().clear();
                                }
                                switch(gamemode){
                                    case "SURVIVAL":
                                        all.setGameMode(GameMode.SURVIVAL);
                                        break;
                                    case "ADVENTURE":
                                        all.setGameMode(GameMode.ADVENTURE);
                                        break;
                                    case "CREATIVE":
                                        all.setGameMode(GameMode.CREATIVE);
                                        break;
                                    case "SPECTATOR":
                                        all.setGameMode(GameMode.SPECTATOR);
                                        break;
                                }
                            }
                        }else{
                            sender.sendMessage(getAlreadyMessage("Stop"));
                        }
                        }else{
                            sender.sendMessage(m);
                        }
                    }else{
                        sender.sendMessage(noperm);
                    }
                }else if(name.equalsIgnoreCase("reload")){
                    if(sender.hasPermission(getPermission("Reload")) || sender.hasPermission(opperm)){
                        sender.sendMessage(getMessage("Reload"));
                        cfg.reloadConfig();
                    }else{
                        sender.sendMessage(noperm);
                    }
                }else if(name.equalsIgnoreCase("restore")){
                    if(sender.hasPermission(getPermission("Restore")) || sender.hasPermission(opperm)){
                        File f = new File("plugins//NyctoChallenge", "timerbackup.yml");
                        if(f.exists()) {
                            YamlConfiguration cfg = YamlConfiguration.loadConfiguration(f);
                            ArrayList<String> timesplit = new ArrayList<String>(Arrays.asList(cfg.getString("OldTimer").split(" , ")));
                            ChallengeTimer pretimer = new ChallengeTimer();
                            pretimer.setHours(Integer.valueOf(timesplit.get(0)));
                            pretimer.setMinutes(Integer.valueOf(timesplit.get(1)));
                            pretimer.setSeconds(Integer.valueOf(timesplit.get(2)));
                            NyctophobiaChallenge.plugin.timer = pretimer;
                            sender.sendMessage(getMessage("Restore").replace("%time%", pretimer.getFullFormat()));
                        }else{
                            sender.sendMessage(cfg.getString("Command.Restore.NotExists"));
                        }
                    }else{
                        sender.sendMessage(noperm);
                    }
                }else if(name.equalsIgnoreCase("save")){
                    if(sender.hasPermission(getPermission("Save")) || sender.hasPermission(opperm)){
                        ChallengeTimer timer = NyctophobiaChallenge.plugin.timer;
                        File file = new File("plugins//NyctoChallenge", "timerbackup.yml");
                        YamlConfiguration cfg = YamlConfiguration.loadConfiguration(file);
                        cfg.options().copyDefaults(true);
                        cfg.options().header("This file is NOT FOR POSTING! It is only available to save the timer (due to the SaveTimer function)! DO NOT CHANGE ANYTHING IN THIS FILE!");
                        cfg.set("OldTimer", timer.getHours()+" , "+timer.getMinutes()+" , "+timer.getSeconds());
                        try {
                            cfg.save(file);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        sender.sendMessage(getMessage("Save").replace("%time%", timer.getFullFormat()));
                    }else{
                        sender.sendMessage(noperm);
                    }
                }else if(name.equalsIgnoreCase("help")){
                    if(sender.hasPermission(getPermission("Help")) || sender.hasPermission(opperm)){
                        sender.sendMessage(getMessage("Help"));
                    }else{
                        sender.sendMessage(noperm);
                    }
                }else{
                    if(sender.hasPermission(getPermission("Help")) || sender.hasPermission(opperm)){
                        sender.sendMessage(getMessage("WrongSyntax"));
                    }else{
                        sender.sendMessage(noperm);
                    }
                }
        }else{
                if(sender.hasPermission(getPermission("Help")) || sender.hasPermission(opperm)){
                    sender.sendMessage(getMessage("WrongSyntax"));
                }else{
                    sender.sendMessage(noperm);
                }
            }
        return false;
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
