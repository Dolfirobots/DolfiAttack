package net.dolfirobots.commands;

import net.dolfirobots.Main;
import net.dolfirobots.chat.Messanger;
import net.dolfirobots.events.SleepListener;
import net.dolfirobots.utils.MainConfig;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.UUID;

public class SettingsCommand implements CommandExecutor, TabCompleter {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(MainConfig.getPrefix() + "§cOnly players can execute this command!");
            return true;
        }

        if (args.length < 1) {
            return true;
        }

        final UUID uuid = player.getUniqueId();

        if (args[0].equalsIgnoreCase("bedmsg") && args.length == 2) {
            final boolean bool = Boolean.parseBoolean(args[1]);
            Bukkit.getScheduler().runTaskAsynchronously(Main.getInstance(), () -> {
                SleepListener.cachedPlayers.put(uuid, bool);

                ConfigurationSection config = MainConfig.getConfig().getConfigurationSection("settings.bedmsg");
                if (config == null) {
                    config = MainConfig.getConfig().createSection("settings.bedmsg");
                }

                config.set(uuid.toString(), bool);
                MainConfig.saveConfig();

                Messanger.sendMessage(Component.text("Gespeichert!", NamedTextColor.GRAY), player);
            });
        }
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length == 1) {
            return List.of("bedmsg");
        }
        if (args.length == 2) {
            return List.of("true", "false");
        }
        return List.of();
    }

    public static void register() {
        Bukkit.getPluginCommand("settings").setExecutor(new SettingsCommand());
        Bukkit.getPluginCommand("settings").setTabCompleter(new SettingsCommand());
    }
}
