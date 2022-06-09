package dev.timecoding.nyctophobia.challenge.config;

import dev.timecoding.nyctophobia.challenge.NyctophobiaChallenge;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.Map;

public class ConfigManager {

    private NyctophobiaChallenge plugin;
    private File file;
    private YamlConfiguration cfg;

    public ConfigManager(NyctophobiaChallenge plugin, String filename) {
        this.plugin = plugin;
        this.file = new File("plugins//NyctoChallenge", filename + ".yml");
        this.cfg = YamlConfiguration.loadConfiguration(this.file);
    }

    public void init() {
        copyDefaults(true);
    }

    public void reloadConfig(){
        this.cfg = YamlConfiguration.loadConfiguration(this.file);
    }

    public void save() {
        try {
            cfg.save(this.file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void addDefaultString(String key, String value) {
        cfg.addDefault(key, value);
        save();
    }

    public void addDefaultInt(String key, Integer value) {
        cfg.addDefault(key, value);
        save();
    }

    public void addDefaultBoolean(String key, boolean value) {
        cfg.addDefault(key, value);
        save();
    }

    public void copyDefaults(boolean b){
        cfg.options().copyDefaults(b);
    }

    public void set(String key, Object object){
        cfg.set(key, object);
        save();
    }

    public String getString(String key) {
        if (keyExists(key)) {
            return cfg.getString(key).replace("&", "§");
        }
        return "NO VALUE";
    }

    public Map<String, Object> getValues(boolean deep) {
        return cfg.getValues(deep);
    }

    public boolean keyExists(String key) {
        if (cfg.get(key) != null) {
            return true;
        }
        return false;
    }

    public Integer getInt(String key) {
        if (keyExists(key)) {
            return cfg.getInt(key);
        }
        return 0;
    }

    public Boolean getBoolean(String key) {
        if (keyExists(key)) {
            return cfg.getBoolean(key);
        }else if(key.equalsIgnoreCase("AutoUpdater")){
            return true;
        }
        return false;
    }

}
