package net.dolfirobots.commands;

import me.clip.placeholderapi.PlaceholderAPI;
import net.dolfirobots.Main;
import net.dolfirobots.chat.Messanger;
import net.dolfirobots.utils.MainConfig;
import net.dolfirobots.utils.PermissionManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class SpeedCommand implements CommandExecutor, TabCompleter {

    private static final float DEFAULT_WALK = 0.2f;
    private static final float DEFAULT_FLY = 0.1f;

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(MainConfig.getPrefix() + "§cOnly players can execute this command!");
            return true;
        }
        if (!PermissionManager.COMMAND_SPEED.checkPlayer(player)) {
            Messanger.sendMessage(Component.text(PlaceholderAPI.setPlaceholders(player, ChatColor.translateAlternateColorCodes('&', MainConfig.getConfig().getString("command.noPermission", "&cYou do not have permission to execute this command.")))), player);
            return true;
        }
        if (args.length < 2) {
            sendUsage(player);
            return true;
        }

        boolean isFly = args[0].equalsIgnoreCase("fly");
        if (!isFly && !args[0].equalsIgnoreCase("walk")) {
            sendUsage(player);
            return true;
        }
        switch (args[1].toLowerCase()) {
            case "set":
                handleSet(player, args, isFly);
                break;
            case "get":
                handleGet(player, args, isFly);
                break;
            case "reset":
                handleReset(player, args, isFly);
                break;
            default:
                sendUsage(player);
        }
        return true;
    }

    private void handleSet(@NotNull Player player, String[] args, boolean fly) {
        if (args.length < 3) {
            sendUsage(player);
            return;
        }

        float speed;
        try {
            speed = Float.parseFloat(args[2]);
            if (speed < 0f || speed > 1f) {
                Messanger.sendMessage(Component.text("§cSpeed must be between §e0§c and §e1§c."), player);
                return;
            }
        } catch (NumberFormatException e) {
            Messanger.sendMessage(Component.text("Invalid speed value.", NamedTextColor.RED), player);
            return;
        }
        Player target = (args.length >= 4) ? Bukkit.getPlayer(args[3]) : player;
        if (target == null) {
            Messanger.sendMessage(Component.text("Player not found.", NamedTextColor.RED), player);
            return;
        }

        boolean silent = args.length >= 5 && args[4].equalsIgnoreCase("true");
        setSpeed(player, target, speed, fly, silent);
    }

    private void handleGet(@NotNull Player player, String[] args, boolean fly) {
        Player targetPlayer = (args.length >= 3) ? Bukkit.getPlayer(args[2]) : player;
        if (targetPlayer == null) {
            Messanger.sendMessage(Component.text("Player not found.", NamedTextColor.RED), player);
            return;
        }

        float value = fly ? targetPlayer.getFlySpeed() : targetPlayer.getWalkSpeed();
        Messanger.sendMessage(Component.text("§e" + targetPlayer.getName() + "§7's current " + (fly ? "fly" : "walk") + " speed: §e" + value), player);
    }

    private void handleReset(@NotNull Player player, String[] args, boolean fly) {
        Player target = (args.length >= 3) ? Bukkit.getPlayer(args[2]) : player;
        if (target == null) {
            Messanger.sendMessage(Component.text("Player not found.", NamedTextColor.RED), player);
            return;
        }

        boolean silent = args.length >= 4 && args[3].equalsIgnoreCase("true");

        float defaultSpeed = fly ? DEFAULT_FLY : DEFAULT_WALK;
        setSpeed(player, target, defaultSpeed, fly, silent);
    }

    private void setSpeed(@NotNull Player player, Player targetPlayer, float speed, boolean fly, boolean silent) {
        if (fly) {
            targetPlayer.setFlySpeed(speed);
        }
        else {
            targetPlayer.setWalkSpeed(speed);
        }
        String type = fly ? "fly" : "walk";
        if (!silent) {
            Messanger.sendMessage(Component.text("§aYour " + type + " speed is now §e" + speed + "§a."), targetPlayer);
        }
        if (!player.equals(targetPlayer)) {
            Messanger.sendMessage(Component.text("§aYou set §e" + targetPlayer.getName() + "§a's " + type + " speed to §e" + speed + "§a."), player);
        }
    }

    public static void sendUsage(Player player) {
        Messanger.sendMessage(Component.text("§eUsage: §7/speed <fly|walk> <set|get|reset> [speed] [player] [silent:true|false]"), player);
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, @NotNull String[] args) {
        List<String> list = new ArrayList<>();
        if (sender instanceof Player player) {
            if (!PermissionManager.COMMAND_SPEED.checkPlayer(player)) {
                return List.of();
            }
        }

        switch (args.length) {
            case 1 -> list.addAll(List.of("fly", "walk"));
            case 2 -> list.addAll(List.of("set", "get", "reset"));
            case 3 -> {
                if (args[1].equalsIgnoreCase("set")) {
                    for (float f = 0.0f; f <= 1.0f; f += 0.05f) {
                        list.add(String.format("%.2f", f));
                    }
                } else {
                    Bukkit.getOnlinePlayers().forEach(p -> list.add(p.getName()));
                }
            }
            case 4 -> {
                if (args[1].equalsIgnoreCase("set") || args[1].equalsIgnoreCase("reset")) {
                    Bukkit.getOnlinePlayers().forEach(p -> list.add(p.getName()));
                }
            }
            case 5 -> list.addAll(List.of("true", "false"));
        }
        return list;
    }

    public static void register() {
        Main.getInstance().getCommand("speed").setExecutor(new SpeedCommand());
        Main.getInstance().getCommand("speed").setTabCompleter(new SpeedCommand());
    }
}
