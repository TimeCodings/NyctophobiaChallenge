package dev.timecoding.nyctophobia.challenge;

import dev.timecoding.nyctophobia.challenge.command.ChallengeCommand;
import dev.timecoding.nyctophobia.challenge.config.ConfigManager;
import dev.timecoding.nyctophobia.challenge.listener.ChallangeListener;
import dev.timecoding.nyctophobia.challenge.updater.AutoUpdater;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;

public final class NyctophobiaChallenge extends JavaPlugin {

    private ConfigManager cfg = new ConfigManager(this, "config");
    private AutoUpdater updater = new AutoUpdater(this);
    private String mainplugin = "Nyctophobia";

    @Override
    public void onEnable() {
        Bukkit.getConsoleSender().sendMessage(ChatColor.RED+"NyctoChallenge"+ChatColor.GREEN+" up and running! "+ChatColor.GOLD+"(v"+getDescription().getVersion()+")");
        //Start AutoUpdater
        if(getServer().getPluginManager().isPluginEnabled(mainplugin)){
            String nyctoversion = getServer().getPluginManager().getPlugin(mainplugin).getDescription().getVersion();
            try {
                if(updater.isCompatible(nyctoversion)){
                    //Init Plugin after checking
                    updater.checkForConfigUpdate();
                    updater.checkForPluginUpdate();
                    //Init Config
                    cfg.init();
                    //Register Listener
                    getServer().getPluginManager().registerEvents(new ChallangeListener(), this);
                    //Register Command
                    getCommand("nycchallenge").setExecutor(new ChallengeCommand());
                }else{
                    Bukkit.getConsoleSender().sendMessage(ChatColor.RED+"NYCTOCHALLENGE GOT DISABLED! Reason: Nyctophobia(API) is OUTDATED! Download the NEWEST version here: "+ChatColor.YELLOW+"https://www.spigotmc.org/resources/nyctophobia.102177/");
                    getServer().getPluginManager().disablePlugin(this);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }else{
            Bukkit.getConsoleSender().sendMessage(ChatColor.RED+"NYCTOCHALLENGE GOT DISABLED! Reason: Nyctophobia(API) is MISSING! Download it here: "+ChatColor.YELLOW+"https://www.spigotmc.org/resources/nyctophobia.102177/");
            getServer().getPluginManager().disablePlugin(this);
        }
    }

    @Override
    public void onDisable() {
        Bukkit.getConsoleSender().sendMessage(ChatColor.RED+"NyctoChallenge "+ChatColor.GOLD+"(v"+getDescription().getVersion()+") "+ChatColor.RED+" got disabled!");
    }

    public ConfigManager getPluginConfig(){
        return this.cfg;
    }

}
