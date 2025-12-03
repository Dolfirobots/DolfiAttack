package net.dolfirobots.utils;

import net.dolfirobots.Main;
import org.bukkit.ChatColor;
import org.bukkit.configuration.Configuration;

public class MainConfig {
    public static void load() {
        Main.getInstance().saveDefaultConfig();
        Main.getInstance().reloadConfig();
    }
    public static Configuration getConfig() {
        return Main.getInstance().getConfig();
    }

    public static String getPrefix() {
        return ChatColor.translateAlternateColorCodes('&', getConfig().getString("prefix", "&8[&9DolfiAttack&8]")) + "§7 ";
    }
    public static void saveConfig() {
        Main.getInstance().saveConfig();
    }
}
