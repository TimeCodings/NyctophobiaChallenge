package dev.timecoding.nyctophobia.challenge.updater;

import dev.timecoding.nyctophobia.challenge.NyctophobiaChallenge;
import dev.timecoding.nyctophobia.challenge.config.ConfigManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.util.Map;
import java.util.Scanner;

public class AutoUpdater {

    private NyctophobiaChallenge plugin;
    private String apibaseurl = "https://api.timecoding.de/nyctophobia/challenge/version.php?type=";
    private String filedownloadurl = "https://api.timecoding.de/nyctophobia/challenge/NyctoChallenge.jar";
    private String compatibleurl = "https://api.timecoding.de/nyctophobia/challenge/compatible.php?version=";
    private String pluginversion;
    private String configversion;
    private boolean autoupdaterenabled;

    //CHANGE THIS EVERY VERSION EVERY CONFIGUPDATE
    private String newconfigversion = "1.0";

    public AutoUpdater(NyctophobiaChallenge plugin){
        this.plugin = plugin;
        this.pluginversion = plugin.getDescription().getVersion();
        this.configversion = plugin.getPluginConfig().getString("config-version");
        System.out.println(configversion);
        this.autoupdaterenabled = this.plugin.getPluginConfig().getBoolean("AutoUpdater");
    }

    public String getNewestPluginVersion() throws IOException {
        URL url = new URL(apibaseurl+"plugin");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.connect();
        try(InputStream inputStream = connection.getInputStream(); Scanner scanner = new Scanner(inputStream)){
            return scanner.next();
        }
    }

    public boolean isCompatible(String version) throws IOException {
        URL url = new URL(compatibleurl+version);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.connect();
        try(InputStream inputStream = connection.getInputStream(); Scanner scanner = new Scanner(inputStream)){
            if(scanner.next().equalsIgnoreCase("TRUE")){
                return true;
            }else{
                return false;
            }
        }
    }

    public boolean pluginUpdateAvailable(){
        try {
            if(!getNewestPluginVersion().equalsIgnoreCase(pluginversion)){
                return true;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    public void checkForPluginUpdate(){
        Bukkit.getConsoleSender().sendMessage(ChatColor.YELLOW + "Checking for updates...");
        if(pluginUpdateAvailable()){
           if(autoupdaterenabled){
               try {
                   Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "Update found! (v"+getNewestPluginVersion()+") Trying to download the newest update...");
               } catch (IOException e) {
                   e.printStackTrace();
               }
               //Trying to download update
               Bukkit.getScheduler().runTaskAsynchronously(this.plugin, new Runnable() {
                   @Override
                   public void run() {
                       downloadUpdate();
                   }
               });
           }else{
               try {
                   Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "Update found! ("+getNewestPluginVersion()+") To guarantee the best plugin experience please update the plugin: "+ChatColor.YELLOW+"https://www.spigotmc.org/resources/nyctophobia.102177/");
               } catch (IOException e) {
                   e.printStackTrace();
               }
           }
        }else{
            Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "No update found! You're running the latest version (v"+pluginversion+")");
        }
    }

    private boolean downloadUpdate() {
        try {
            URL download = new URL(this.filedownloadurl);
            BufferedInputStream in = null;
            FileOutputStream fout = null;
            try {
                Bukkit.getConsoleSender().sendMessage(ChatColor.YELLOW + "Trying to download the new NyctophobiaChallenge update...");
                in = new BufferedInputStream(download.openStream());
                fout = new FileOutputStream("plugins//" + plugin.getName() + ".jar");
                final byte data[] = new byte[1024];
                int count;
                while ((count = in.read(data, 0, 1024)) != -1) {
                    fout.write(data, 0, count);
                }
            }catch (Exception e){
                Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "Failed to Download the new Version!");
                e.printStackTrace();
                return false;
            } finally {
                if (in != null) {
                    in.close();
                }
                if (fout != null) {
                    fout.close();
                }
            }
            Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "Successfully downloaded the NyctophobiaChallenge update!");
            Bukkit.getConsoleSender().sendMessage(ChatColor.AQUA + "To apply the changes please restart/reload the server!");
            return true;
        } catch (IOException e) {
            Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "Failed to Download the new Version!");
            e.printStackTrace();
            return false;
        }
    }

    public String getNewestConfigVersion(){
        return newconfigversion;
    }

    public boolean configUpdateAvailable(){
            if(!getNewestConfigVersion().equalsIgnoreCase(configversion)){
                return true;
            }
        return false;
    }

    public void checkForConfigUpdate(){
        Bukkit.getConsoleSender().sendMessage(ChatColor.YELLOW + "Checking for config updates...");
        if(configUpdateAvailable()){
            //Safe entrys
            Map<String, Object> quicksave = this.plugin.getPluginConfig().getValues(true);
            //Get file
            File file = new File("plugins//NyctoChallenge", "config.yml");
            if(file.exists()) {
                //Delete old config and create new
                try {
                    Files.delete(file.toPath());
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "Config Update found! ("+getNewestConfigVersion()+") Updating config...");
                Bukkit.getScheduler().runTaskLaterAsynchronously(this.plugin, new Runnable() {
                    private NyctophobiaChallenge plugin = AutoUpdater.this.plugin;
                    @Override
                    public void run() {
                        this.plugin.saveResource("config.yml", false);
                        //Get new config
                        ConfigManager config = this.plugin.getPluginConfig();
                        //Reload BUGFIX
                        config.reloadConfig();
                        //Copy datas
                        for (String save : quicksave.keySet()) {
                            if (config.keyExists(save) && !save.equalsIgnoreCase("config-version")) {
                                //Overwrite
                                config.set(save, quicksave.get(save));
                            }
                        }
                        //Replacing old settings with new
                        Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "Config got updated!");
                    }
                }, 20);
            }else{
                Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "No Config found! Creating a new one...");
                //Create new Config
                this.plugin.saveResource("config.yml", false);
            }
        }else{
            Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "No config update found!");
        }
    }

}
