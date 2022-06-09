package dev.timecoding.nyctophobia.challenge;

import dev.timecoding.nyctophobia.Nyctophobia;
import dev.timecoding.nyctophobia.challenge.api.ChallengeState;
import dev.timecoding.nyctophobia.challenge.api.ChallengeTimer;
import dev.timecoding.nyctophobia.challenge.command.ChallengeCommand;
import dev.timecoding.nyctophobia.challenge.config.ConfigManager;
import dev.timecoding.nyctophobia.challenge.listener.ChallangeListener;
import dev.timecoding.nyctophobia.challenge.updater.AutoUpdater;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;

public final class NyctophobiaChallenge extends JavaPlugin {

    public static NyctophobiaChallenge plugin;
    public ChallengeTimer timer = new ChallengeTimer();
    public ChallengeState state = ChallengeState.STOPPED;

    private ConfigManager cfg = new ConfigManager(this, "config");
    private AutoUpdater updater;
    private String mainplugin = "Nyctophobia";

    @Override
    public void onEnable() {
        Bukkit.getConsoleSender().sendMessage(ChatColor.RED+"NyctoChallenge"+ChatColor.GREEN+" up and running! "+ChatColor.GOLD+"(v"+getDescription().getVersion()+")");
        //Start AutoUpdater
        if(getServer().getPluginManager().isPluginEnabled(mainplugin)){
            String nyctoversion = getServer().getPluginManager().getPlugin(mainplugin).getDescription().getVersion();
            try {
                updater = new AutoUpdater(this);
                if(updater.isCompatible(nyctoversion)){
                    //Setup Instance
                    plugin = this;
                    //Init Plugin after checking
                    updater.checkForConfigUpdate();
                    updater.checkForPluginUpdate();
                    //Init Config
                    cfg.init();
                    //Register Listener
                    getServer().getPluginManager().registerEvents(new ChallangeListener(), this);
                    //Register Command
                    getCommand("nycchallenge").setExecutor(new ChallengeCommand());
                    //Start Async Scheduler (Timer)
                    startTimerScheduler();
                    //Delete Backup if SaveTimer is disabled
                    boolean saveTimer = cfg.getBoolean("SaveTimer");
                    File file = new File("plugins//NyctoChallenge", "timerbackup.yml");
                    if(file.exists() && !saveTimer){
                        file.delete();
                    }
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

        boolean saveTimer = cfg.getBoolean("SaveTimer");
        if(saveTimer){
            File file = new File("plugins//NyctoChallenge", "timerbackup.yml");
            YamlConfiguration cfg = YamlConfiguration.loadConfiguration(file);
            cfg.options().copyDefaults(true);
            cfg.options().header("This file is NOT FOR POSTING! It is only available to save the timer (due to the SaveTimer function)!");
            cfg.set("OldTimer", timer.getHours()+" , "+timer.getMinutes()+" , "+timer.getSeconds());
            try {
                cfg.save(file);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public ConfigManager getPluginConfig(){
        return this.cfg;
    }

    int timerid = 0;

    public void startTimerScheduler(){
        timerid = Bukkit.getScheduler().scheduleAsyncRepeatingTask(this, new Runnable() {
            @Override
            public void run() {
                boolean enabled = cfg.getBoolean("ActionBarTimer.Enabled");
                String waiting = cfg.getString("ActionBarTimer.Format.Waiting");
                String started = cfg.getString("ActionBarTimer.Format.Started");
                String paused = cfg.getString("ActionBarTimer.Format.Paused");
                String stopped = cfg.getString("ActionBarTimer.Format.Stopped");
                //TODO On Stop (if enabled) do not send ActionBarTimer
                if(Bukkit.getOnlinePlayers().size() == 0){
                    stopTimerScheduler();
                }
                if(enabled) {
                    for (Player all : Bukkit.getOnlinePlayers()) {
                        switch (state) {
                            case WAITING:
                                sendActionbar(all, getDefaultTimerReplace(waiting, all));
                                break;
                            case STARTED:
                                sendActionbar(all, getDefaultTimerReplace(started, all));
                                timer.addSeconds(1);
                                if (timer.getSeconds().equalsIgnoreCase("60")) {
                                    timer.setSeconds(0);
                                    timer.addMinutes(1);
                                }
                                if (timer.getMinutes().equalsIgnoreCase("60")) {
                                    timer.setMinutes(0);
                                    timer.addHours(1);
                                }
                                break;
                            case PAUSED:
                                sendActionbar(all, getDefaultTimerReplace(paused, all));
                                break;
                            case STOPPED:
                                sendActionbar(all, getDefaultTimerReplace(stopped, all));
                                break;
                        }
                    }
                }
            }
        },0, 20);
    }

    public String getDefaultTimerReplace(String text, Player all){
        int minplayers = cfg.getInt("Multiplayer.MinPlayers");
        int maxplayers = cfg.getInt("Multiplayer.MaxPlayers");
        String mlighting = Nyctophobia.plugin.getPluginConfig().getInt("MaxLighting").toString();
        text = text.replace("%hours%", timer.getHours().toString()).replace("%minutes%", timer.getMinutes().toString()).replace("%seconds%", timer.getSeconds().toString()).replace("%lighting%", String.valueOf(all.getLocation().getBlock().getLightLevel())).replace("%maxlighting%", mlighting).replace("%minplayers%", String.valueOf(minplayers)).replace("%maxplayers%", String.valueOf(maxplayers));
        return text;
    }

    public boolean TimerSchedulerRunning(){
        if(timerid == 0){
            return false;
        }else{
            return true;
        }
    }

    public void stopTimerScheduler(){
        if(timerid != 0){
            Bukkit.getScheduler().cancelTask(timerid);
            timerid = 0;
        }
    }

    public void sendActionbar(Player p, String text){
        p.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(text));
    }

    public ChallengeState getState() {
        return state;
    }

    public void setState(ChallengeState state) {
        this.state = state;
    }
}
