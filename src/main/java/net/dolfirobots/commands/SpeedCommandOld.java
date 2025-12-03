package net.dolfirobots.commands;

import com.google.common.primitives.Floats;
import me.clip.placeholderapi.PlaceholderAPI;
import net.dolfirobots.chat.Messanger;
import net.dolfirobots.utils.MainConfig;
import net.dolfirobots.utils.PermissionManager;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.plaf.basic.BasicButtonUI;
import java.util.ArrayList;
import java.util.List;

public class SpeedCommandOld implements CommandExecutor, TabCompleter {

    // /speed walk reset
    // /speed walk reset <Player> (Silent true or false)
    // /speed walk set 0.2
    // /speed walk set 0.2 <Player> (Silent true or false)
    // /speed walk get
    // /speed walk get <player>
    // /speed fly reset
    // /speed fly reset <Player> (Silent true or false)
    // /speed fly set 0.2
    // /speed fly set 0.2 <Player> (Silent true or false)
    // /speed fly get
    // /speed fly get <player>


    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(MainConfig.getPrefix() + "§cOnly players can execute this command!");
            return true;
        }

        if (!PermissionManager.COMMAND_SPEED.checkPlayer(player)) {
            Messanger.sendMessage(Component.text(PlaceholderAPI.setPlaceholders(player, ChatColor.translateAlternateColorCodes('&', MainConfig.getConfig().getString("command.noPermission", "&cYou do not have permission to execute this command.")))), player);
            return true;
        }

        if (args.length <= 1) {
            sendUsage(player);
            return true;
        }

        if (args[0].equalsIgnoreCase("fly")) {
            if (args[1].equalsIgnoreCase("set")) {
                if (args.length == 3) {
                    try {
                        float speed = Float.parseFloat(args[2]);
                        if (speed > 1 || speed < 0) {
                            Messanger.sendMessage(Component.text("§cInvalid speed value. Please enter a number between §e0§c and §e1§c"), player);
                            return true;
                        }
                        player.setFlySpeed(speed);
                        Messanger.sendMessage(Component.text("Your fly speed has been set to §e" + speed + "§7."), player);
                    } catch (NumberFormatException e) {
                        Messanger.sendMessage(Component.text("§cInvalid speed value. Please enter a number between §e0§c and §e1§c"), player);
                    }
                } else if (args.length == 4 || args.length == 5) {
                    try {
                        float speed = Float.parseFloat(args[2]);
                        if (speed > 1 || speed < 0) {
                            Messanger.sendMessage(Component.text("§cInvalid speed value. Please enter a number between §e0§c and §e1§c"), player);
                            return true;
                        }
                        Player targetPlayer = Bukkit.getPlayer(args[3]);
                        if (targetPlayer != null) {
                            targetPlayer.setFlySpeed(speed);
                            if (!(args.length == 5 && args[4].equalsIgnoreCase("true"))) {
                                Messanger.sendMessage(Component.text("You fly speed has been set to §e" + speed + "§7 by §e" + player.getName() + "§7."), player);
                            }
                            Messanger.sendMessage(Component.text("You have set §e" + targetPlayer.getName() + "§7's fly speed to §e" + speed + "§7."), player);
                        }
                    } catch (NumberFormatException e) {
                        Messanger.sendMessage(Component.text("§cInvalid speed value. Please enter a number between §e0§c and §e1§c"), player);
                    }
                } else {
                    sendUsage(player);
                    return true;
                }
            }
            if (args[1].equalsIgnoreCase("get")) {
                if (args.length == 2) {
                    Messanger.sendMessage(Component.text("You current fly speed is §e" + player.getFlySpeed()), player);
                } else if (args.length == 3) {
                    Player targetPlayer = Bukkit.getPlayer(args[2]);
                    if (targetPlayer != null) {
                        Messanger.sendMessage(Component.text("§e" + targetPlayer.getName() + "'s§7 current fly speed is §e" + player.getFlySpeed()), player);
                    }
                }
            }
            if (args[1].equalsIgnoreCase("reset")) {
                if (args.length == 2) {
                    Messanger.sendMessage(Component.text("You fly speed has been reset to default (§e0.05§7)."), player);
                } else if (args.length == 3 || args.length == 4) {
                    Player targetPlayer = Bukkit.getPlayer(args[2]);
                    if (targetPlayer != null) {
                        targetPlayer.setFlySpeed(0.05f);
                        Messanger.sendMessage(Component.text("You fly speed has been reset to default (§e0.05§7)."), targetPlayer);
                    }
                }
            }
        }
        if (args[0].equalsIgnoreCase("walk")) {
            if (args[1].equalsIgnoreCase("set")) {
                if (args.length == 3) {
                    try {
                        float speed = Float.parseFloat(args[2]);
                        if (speed > 1 || speed < 0) {
                            Messanger.sendMessage(Component.text("§cInvalid speed value. Please enter a number between §e0§c and §e1§c"), player);
                            return true;
                        }
                        player.setWalkSpeed(speed);
                        Messanger.sendMessage(Component.text("Your walk speed has been set to §e" + speed + "§7."), player);
                    } catch (NumberFormatException e) {
                        Messanger.sendMessage(Component.text("§cInvalid speed value. Please enter a number between §e0§c and §e1§c"), player);
                    }
                } else if (args.length == 4 || args.length == 5) {
                    try {
                        float speed = Float.parseFloat(args[2]);
                        if (speed > 1 || speed < 0) {
                            Messanger.sendMessage(Component.text("§cInvalid speed value. Please enter a number between §e0§c and §e1§c"), player);
                            return true;
                        }
                        Player targetPlayer = Bukkit.getPlayer(args[3]);
                        if (targetPlayer != null) {
                            targetPlayer.setWalkSpeed(speed);
                            Messanger.sendMessage(Component.text("You walk speed has been set to §e" + speed  + "§7 by §e" + player.getName() + "§7."), player);
                            Messanger.sendMessage(Component.text("You have set §e" + targetPlayer.getName() + "§7's walk speed to §e" + speed + "§7."), player);
                        }
                    } catch (NumberFormatException e) {
                        Messanger.sendMessage(Component.text("§cInvalid speed value. Please enter a number between §e0§c and §e1§c"), player);
                    }
                } else {
                    sendUsage(player);
                    return true;
                }
            }
            if (args[1].equalsIgnoreCase("get")) {
                if (args.length == 2) {
                    Messanger.sendMessage(Component.text("You current walk speed is §e" + player.getWalkSpeed()), player);
                } else if (args.length == 3) {
                    Player targetPlayer = Bukkit.getPlayer(args[2]);
                    if (targetPlayer != null) {
                        Messanger.sendMessage(Component.text("§e" + targetPlayer.getName() + "'s§7 current walk speed is §e" + player.getWalkSpeed()), player);
                    }
                }
            }
            if (args[1].equalsIgnoreCase("reset")) {
                if (args.length == 2) {
                    Messanger.sendMessage(Component.text("You fly speed has been reset to default (§e0.05§7)."), player);
                } else if (args.length == 3 || args.length == 4) {
                    Player targetPlayer = Bukkit.getPlayer(args[2]);
                    if (targetPlayer != null) {
                        targetPlayer.setFlySpeed(0.05f);
                        if ((args.length == 4 && args[3].equalsIgnoreCase("true")) || args.length == 3) {
                            Messanger.sendMessage(Component.text("You fly speed has been reset to default (§e0.05§7)."), targetPlayer);
                        }
                        Messanger.sendMessage(Component.text("You have reset §e" + targetPlayer.getName() + "§7's walk speed."), player);
                    }
                }
            }
        }
        return true;
    }

    public static void sendUsage(Player player) {
        Messanger.sendMessage(Component.text("§eUsage: §7/speed <fly|walk> <set|get> [speed in float] [player] [silent boolean]"), player);
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        List<String> completion = new ArrayList<>();
        if (args.length == 1) {
            completion.add("fly");
            completion.add("walk");
        }
        if (args.length == 2) {
            completion.add("set");
            completion.add("get");
            completion.add("reset");
        }
        if (args.length == 3) {
            if (args[2].equalsIgnoreCase("set")) {
                for (float f = 0.0f; f <= 1.0f; f += 0.05f) {
                    completion.add(String.format("%.2f", f));
                }
            }
            if (args[2].equalsIgnoreCase("get")) {
                Bukkit.getOnlinePlayers().forEach(player -> completion.add(player.getName()));
            }
            if (args[2].equalsIgnoreCase("reset")) {
                Bukkit.getOnlinePlayers().forEach(player -> completion.add(player.getName()));
            }
        }
        if (args.length == 4) {
            if (args[2].equalsIgnoreCase("set") || args[2].equalsIgnoreCase("reset")) {
                completion.addAll(List.of("true", "false"));
            }
        }
        return completion;
    }
}
