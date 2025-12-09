package net.dolfirobots;

import net.dolfirobots.commands.*;
import net.dolfirobots.events.ChatListener;
import net.dolfirobots.events.JoinLeaveListener;
import net.dolfirobots.events.ServerPingListener;
import net.dolfirobots.utils.MainConfig;
import net.dolfirobots.utils.MojangWebAPI;
import net.luckperms.api.LuckPerms;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import static net.dolfirobots.chat.Messanger.sendConsole;
import static net.dolfirobots.chat.Messanger.sendError;

public final class Main extends JavaPlugin {

    public static Location SPAWN;

    private static Main instance;
    private static LuckPerms luckPerms;
    private static boolean cachedMaintenanceMode;

    @Override
    public void onEnable() {
        instance = this;

        SPAWN = new Location(Bukkit.getWorld("world"), 279, 113, -780, 0, 0);

        loadMaintenanceMode();

        sendConsole("-".repeat(25));
        sendConsole("§aDolfiAttack §7is now §aenabled§7!");
        sendConsole("Version: §ev" + getDescription().getVersion());
        sendConsole("-".repeat(25));


        // PlaceholderAPI
        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") == null) {
            sendError("Plugins", "§aPlaceholderAPI§c was not found!");
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }
        sendConsole("Plugins", "§aPlaceholderAPI §7was found!");


        // ProtocolLib
        if (Bukkit.getPluginManager().getPlugin("ProtocolLib") == null) {
            sendError("Plugins", "§aProtocolLib§c was not found!");
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }
        sendConsole("Plugins", "§aProtocolLib§7 was found!");
        ServerPingListener.register();

        // LuckPerms
        RegisteredServiceProvider<LuckPerms> provider = Bukkit.getServicesManager().getRegistration(LuckPerms.class);
        if (provider == null) {
            sendError("Plugins", "§aLuckPerms§c was not found!");
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }
        sendConsole("Plugins", "§aLuckPerms§7 was found!");
        luckPerms = provider.getProvider();

        sendConsole("Commands", "Registering commands...");
        WhitelistCommand.register();
        SpeedCommand.register();
        ClanCommand.register();
        MaintenanceCommand.register();
        StatsCommand.register();
        SettingsCommand.register();

        sendConsole("Events", "Registering events...");
        getServer().getPluginManager().registerEvents(new JoinLeaveListener(), this);
        getServer().getPluginManager().registerEvents(new ChatListener(), this);
        getServer().getPluginManager().registerEvents(new AntiCommandLookup(), this);
        SleepListener.register();

        sendConsole("Configuration", "Loading configuration...");
        MainConfig.load();
    }

    @Override
    public void onDisable() {
        sendConsole("-".repeat(25));
        sendConsole("§aDolfiAttack §7is now §cdisabled§7!");
        sendConsole("Version: §ev" + getDescription().getVersion());
        sendConsole("-".repeat(25));
        MojangWebAPI.shutdownExecutor();
    }

    public static Main getInstance() {
        return instance;
    }

    public static LuckPerms getLuckPerms() {
        return luckPerms;
    }

    public static void loadMaintenanceMode() {
        cachedMaintenanceMode = MainConfig.getConfig().getBoolean("maintenance.enabled", false);
    }

    public static boolean isMaintenanceMode() {
        return cachedMaintenanceMode;
    }
}
