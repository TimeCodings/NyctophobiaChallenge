package dev.timecoding.nyctophobia.challenge.listener;

import dev.timecoding.nyctophobia.challenge.NyctophobiaChallenge;
import dev.timecoding.nyctophobia.challenge.api.ChallengeState;
import dev.timecoding.nyctophobia.challenge.api.ChallengeTimer;
import dev.timecoding.nyctophobia.challenge.config.ConfigManager;
import dev.timecoding.nyctophobia.event.DarknessEnterEvent;
import dev.timecoding.nyctophobia.event.DarknessLeaveEvent;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;

public class ChallangeListener implements Listener {

    private ConfigManager cfg = NyctophobiaChallenge.plugin.getPluginConfig();

    @EventHandler
    public void onDarknessEnter(DarknessEnterEvent e){
        Player p = e.getPlayer();
        ChallengeState state = NyctophobiaChallenge.plugin.getState();
        boolean enabled = cfg.getBoolean("Enabled");
        if(enabled){
            e.setCancelled(true);
        }
        if(state.equals(ChallengeState.STARTED) && enabled) {
            NyctophobiaChallenge.plugin.setState(ChallengeState.STOPPED);
            ChallengeTimer timer = NyctophobiaChallenge.plugin.timer;
            boolean clearInv = cfg.getBoolean("OnDeath.ClearInventory");
            String msgifend = cfg.getString("OnDeath.MessageIfEnd").replace("%player%", p.getName()).replace("%time%", timer.getFullFormat()).replace("%reason%", cfg.getString("OnDeath.DarknessReason"));
            String prmessage = cfg.getString("OnDeath.PrivateMessage").replace("%player%", p.getName()).replace("%time%", timer.getFullFormat());
            String gamemode = cfg.getString("OnDeath.GameMode");
            p.sendMessage(prmessage);
            for (Player all : Bukkit.getOnlinePlayers()) {
                all.sendMessage(msgifend);
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
        }
    }

    @EventHandler
    public void onDarknessLeave(DarknessLeaveEvent e){
        boolean enabled = cfg.getBoolean("Enabled");
        if(enabled) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e){
        Player p = e.getPlayer();

        //Get values
        boolean enabled = cfg.getBoolean("Enabled");
        boolean multiplayer = cfg.getBoolean("Multiplayer.Enabled");
        int minplayers = cfg.getInt("Multiplayer.MinPlayers");
        int maxplayers = cfg.getInt("Multiplayer.MaxPlayers");
        boolean endwhenonedies = cfg.getBoolean("Multiplayer.EndWhenOneDies");
        boolean duelmode = cfg.getBoolean("Multiplayer.DuelMode");
        boolean givecompass = cfg.getBoolean("Multiplayer.GiveCompassOnDeath");
        boolean startOnJoin = cfg.getBoolean("StartOnJoin");

        boolean clearInv = cfg.getBoolean("OnStart.ClearInventory");
        String gamemode = cfg.getString("OnStart.GameMode");
        String msg = cfg.getString("OnStart.Message");

        if(enabled){
            int players = Bukkit.getOnlinePlayers().size();
            if(multiplayer) {
                if (players >= minplayers && players <= maxplayers && startOnJoin) {
                    Bukkit.getScheduler().runTaskLaterAsynchronously(NyctophobiaChallenge.plugin, new Runnable() {
                        @Override
                        public void run() {
                            NyctophobiaChallenge.plugin.setState(ChallengeState.STARTED);
                        }
                    }, 40);
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
                }else{
                    NyctophobiaChallenge.plugin.setState(ChallengeState.WAITING);
                }
            }else if(startOnJoin){
                if(!NyctophobiaChallenge.plugin.TimerSchedulerRunning()){
                    NyctophobiaChallenge.plugin.startTimerScheduler();
                }
                Bukkit.getScheduler().runTaskLaterAsynchronously(NyctophobiaChallenge.plugin, new Runnable() {
                    @Override
                    public void run() {
                        NyctophobiaChallenge.plugin.setState(ChallengeState.STARTED);
                    }
                }, 40);
                p.sendMessage(msg);
                if (clearInv) {
                    p.getInventory().clear();
                }
                switch (gamemode) {
                    case "SURVIVAL":
                        p.setGameMode(GameMode.SURVIVAL);
                        break;
                    case "ADVENTURE":
                        p.setGameMode(GameMode.ADVENTURE);
                        break;
                    case "CREATIVE":
                        p.setGameMode(GameMode.CREATIVE);
                        break;
                    case "SPECTATOR":
                        p.setGameMode(GameMode.SPECTATOR);
                        break;
                }
            }
        }
    }

    @EventHandler
    public void onConnect(PlayerLoginEvent e){
        //Get values
        boolean enabled = cfg.getBoolean("Enabled");
        boolean multiplayer = cfg.getBoolean("Multiplayer.Enabled");
        int minplayers = cfg.getInt("Multiplayer.MinPlayers");
        int maxplayers = cfg.getInt("Multiplayer.MaxPlayers");
        boolean endwhenonedies = cfg.getBoolean("Multiplayer.EndWhenOneDies");
        boolean duelmode = cfg.getBoolean("Multiplayer.DuelMode");
        boolean givecompass = cfg.getBoolean("Multiplayer.GiveCompassOnDeath");
        boolean startOnJoin = cfg.getBoolean("StartOnJoin");
        String kickmsg = cfg.getString("Multiplayer.KickMessage");
        String maxmsg = cfg.getString("Multiplayer.MaxMessage");

        if(enabled){
            int players = Bukkit.getOnlinePlayers().size();
            if(multiplayer){
                if(players > maxplayers){
                    e.disallow(PlayerLoginEvent.Result.KICK_OTHER, maxmsg);
                }else if(players < minplayers){
                    if(startOnJoin) {
                        NyctophobiaChallenge.plugin.setState(ChallengeState.WAITING);
                    }
                    if(!NyctophobiaChallenge.plugin.TimerSchedulerRunning()){
                        NyctophobiaChallenge.plugin.startTimerScheduler();
                    }
                }
            }else if(players >= 1){
                e.disallow(PlayerLoginEvent.Result.KICK_OTHER, kickmsg);
            }
        }
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent e){
        Player p = e.getEntity().getPlayer();
        ChallengeTimer timer = NyctophobiaChallenge.plugin.timer;
        boolean enabled = cfg.getBoolean("Enabled");
        boolean multiplayer = cfg.getBoolean("Multiplayer.Enabled");
        int minplayers = cfg.getInt("Multiplayer.MinPlayers");
        int maxplayers = cfg.getInt("Multiplayer.MaxPlayers");
        boolean endwhenonedies = cfg.getBoolean("Multiplayer.EndWhenOneDies");
        boolean duelmode = cfg.getBoolean("Multiplayer.DuelMode");
        boolean givecompass = cfg.getBoolean("Multiplayer.GiveCompassOnDeath");
        boolean startOnJoin = cfg.getBoolean("StartOnJoin");
        boolean clearInv = cfg.getBoolean("OnDeath.ClearInventory");
        String msgifend = cfg.getString("OnDeath.MessageIfEnd").replace("%player%", p.getName()).replace("%time%", timer.getFullFormat()).replace("%reason%",e.getDeathMessage());
        String pmessage = cfg.getString("OnDeath.PublicMessage").replace("%player%", p.getName()).replace("%time%", timer.getFullFormat());
        String prmessage = cfg.getString("OnDeath.PrivateMessage").replace("%player%", p.getName()).replace("%time%", timer.getFullFormat());
        String gamemode = cfg.getString("OnDeath.GameMode");
        if(enabled && canPlay()){
            e.setDeathMessage("");
            p.sendMessage(prmessage);
            sendPublicMessage(pmessage);
            if(multiplayer){
                if(endwhenonedies){
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
                    NyctophobiaChallenge.plugin.setState(ChallengeState.STOPPED);
                    NyctophobiaChallenge.plugin.tryToRestart();
                }else{
                    p.sendMessage(msgifend);
                    if(clearInv){
                        p.getInventory().clear();
                    }
                    switch(gamemode){
                        case "SURVIVAL":
                            p.setGameMode(GameMode.SURVIVAL);
                            break;
                        case "ADVENTURE":
                            p.setGameMode(GameMode.ADVENTURE);
                            break;
                        case "CREATIVE":
                            p.setGameMode(GameMode.CREATIVE);
                            break;
                        case "SPECTATOR":
                            p.setGameMode(GameMode.SPECTATOR);
                            break;
                    }
                }
            }else{
                p.sendMessage(msgifend);
                if(clearInv){
                    p.getInventory().clear();
                }
                switch(gamemode){
                    case "SURVIVAL":
                        p.setGameMode(GameMode.SURVIVAL);
                        break;
                    case "ADVENTURE":
                        p.setGameMode(GameMode.ADVENTURE);
                        break;
                    case "CREATIVE":
                        p.setGameMode(GameMode.CREATIVE);
                        break;
                    case "SPECTATOR":
                        p.setGameMode(GameMode.SPECTATOR);
                        break;
                }
                NyctophobiaChallenge.plugin.setState(ChallengeState.STOPPED);
                NyctophobiaChallenge.plugin.tryToRestart();
            }
        }
    }

    @EventHandler
    public void onEntityKill(EntityDeathEvent e){
        ChallengeTimer timer = NyctophobiaChallenge.plugin.timer;
        boolean enabled = cfg.getBoolean("Enabled");
        boolean clearInv = cfg.getBoolean("OnEnd.ClearInventory");
        String goaltype = cfg.getString("GoalType");
        String gamemode = cfg.getString("OnEnd.GameMode");
        if(enabled && canPlay()) {
            if (e.getEntity().getKiller() != null && EntityType.valueOf(goaltype) != null) {
                String endmsg = cfg.getString("OnEnd.Message").replace("%player%", e.getEntity().getKiller().getName()).replace("%time%", timer.getFullFormat());
                EntityType type = EntityType.valueOf(goaltype);
                Player killer = e.getEntity().getKiller();
                if(e.getEntity().getType().equals(type)){
                        for(Player all : Bukkit.getOnlinePlayers()){
                            all.sendMessage(endmsg);
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
                    NyctophobiaChallenge.plugin.setState(ChallengeState.STOPPED);
                    NyctophobiaChallenge.plugin.tryToRestart();
                }
            }
        }
    }

    public boolean canPlay(){
        switch(NyctophobiaChallenge.plugin.getState()){
            case PAUSED:
            case STOPPED:
            case WAITING:
                return false;
            case STARTED:
                return true;
        }
        return false;
    }

    public void sendPublicMessage(String msg){
        for(Player all : Bukkit.getOnlinePlayers()){
            all.sendMessage(msg);
        }
    }

}
