package net.dolfirobots.commands;

import net.dolfirobots.Main;
import net.dolfirobots.chat.Messanger;
import net.dolfirobots.utils.MainConfig;
import net.dolfirobots.utils.MojangWebAPI;
import net.dolfirobots.utils.PermissionManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.Objects;

public class WhitelistCommand implements CommandExecutor, TabCompleter {

    public static void sendUsage(CommandSender sender) {
        sendSender(Component.text("§eUsage: §7/whitelist <add|remove|list|enable|disable> [player]"), sender);
    }

    public static void sendSender(Component message, CommandSender sender) {
        if (sender instanceof Player) {
            Messanger.sendMessage(message, (Player) sender);
        } else {
            Messanger.sendConsole(LegacyComponentSerializer.legacySection().serialize(message));
        }
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (sender instanceof Player player) {
            if (!PermissionManager.COMMAND_WHITELIST.checkPlayer(player)) {
                Messanger.sendMessage(Component.text("Du hast keine Berechtigung diesen Command auszuführen!", NamedTextColor.RED), player);
                return true;
            }
        }

        if (args.length == 0) {
            sendSender(Component.text("Whitelist is currently " + (MainConfig.getConfig().getBoolean("whitelist.enabled", false) ? "§aenabled§7." : "§cdisabled§7.")), sender);
            return true;
        }

        if (args.length == 1) {
            if (args[0].equalsIgnoreCase("list")) {
                List<String> playerNames = MainConfig.getConfig().getMapList("whitelist.allowed_players")
                        .stream()
                        .map(p -> (String) p.get("name"))
                        .filter(Objects::nonNull)
                        .toList();

                sendSender(Component.text("There are §e" + playerNames.size() + "§7 players whitelisted: §e" + String.join("§7, §e", playerNames)), sender);
            } else if (args[0].equalsIgnoreCase("enable")) {
                if (MainConfig.getConfig().getBoolean("whitelist.enabled")) {
                    sendSender(Component.text("Whitelist is already ", NamedTextColor.GRAY)
                            .append(Component.text("enabled", NamedTextColor.GREEN))
                            .append(Component.text("!", NamedTextColor.GRAY)),
                            sender
                    );
                    return true;
                }

                MainConfig.getConfig().set("whitelist.enabled", true);
                MainConfig.saveConfig();
                sendSender(Component.text("§7Whitelist has been §aenabled§7."), sender);
            } else if (args[0].equalsIgnoreCase("disable")) {
                if (MainConfig.getConfig().getBoolean("whitelist.enabled")) {
                    sendSender(Component.text("Whitelist is already ", NamedTextColor.GRAY)
                            .append(Component.text("disabled", NamedTextColor.RED))
                            .append(Component.text("!", NamedTextColor.GRAY)),
                            sender
                    );
                }

                MainConfig.getConfig().set("whitelist.enabled", false);
                MainConfig.saveConfig();
                sendSender(Component.text("§7Whitelist has been §cdisabled§7."), sender);
            } else {
                sendUsage(sender);
            }
            return true;
        }
        if (args.length == 2) {
            String action = args[0];
            String targetPlayer = args[1];
            List<Map<?, ?>> whitelist = MainConfig.getConfig().getMapList("whitelist.allowed_players");

            if (action.equalsIgnoreCase("add")) {
                for (Map<?, ?> player : whitelist) {
                    if (player.get("name").equals(targetPlayer)) {
                        sendSender(Component.text("§e" + targetPlayer + "§7 is already whitelisted."), sender);
                        return true;
                    }
                }
                MojangWebAPI.getUUIDAsync(targetPlayer).thenAccept(uuid -> {
                    if (uuid.isEmpty()) {
                        Bukkit.getScheduler().runTask(Main.getInstance(), () -> {
                                sendSender(Component.text("§cCould not fetch UUID for player §e" + targetPlayer + "§c. Does this player exist?"), sender);
                        });
                        return;
                    }

                    Map<String, Object> newPlayer = Map.of("name", targetPlayer, "uuid", uuid.get().toString());

                    whitelist.add(newPlayer);
                    MainConfig.getConfig().set("whitelist.allowed_players", whitelist);
                    MainConfig.saveConfig();

                    Bukkit.getScheduler().runTask(Main.getPlugin(Main.class), () ->
                            sendSender(Component.text("§e" + targetPlayer + "§7 has been added to the whitelist."), sender));
                });
            } else if (action.equalsIgnoreCase("remove")) {
                boolean removed = whitelist.removeIf(player -> player.get("name").equals(targetPlayer) || player.get("uuid").equals(targetPlayer));
                if (removed) {
                    MainConfig.getConfig().set("whitelist.allowed_players", whitelist);
                    MainConfig.saveConfig();

                    sendSender(Component.text("§e" + targetPlayer + "§7 has been removed from the whitelist."), sender);
                } else {
                    sendSender(Component.text("§e" + targetPlayer + "§7 is not in the whitelist."), sender);
                }
            } else {
                sendUsage(sender);
            }
            return true;
        }
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (sender instanceof Player player) {
            if (!PermissionManager.COMMAND_WHITELIST.checkPlayer(player)) {
                return List.of();
            }
        }

        if (args.length == 1) {
            List<String> commands = List.of("enable", "disable", "add", "remove", "list");
            return commands.stream()
                    .filter(s -> s.startsWith(args[0].toLowerCase()))
                    .toList();
        }
        if (args.length == 2) {
            List<Map<?, ?>> allowedList = MainConfig.getConfig().getMapList("whitelist.allowed_players");
            if (args[0].equalsIgnoreCase("add")) {
                List<String> allowedPlayers = allowedList.stream()
                        .map(player -> (String) player.get("name"))
                        .toList();

                return Bukkit.getOnlinePlayers().stream()
                        .map(Player::getName)
                        .filter(name -> !allowedPlayers.contains(name))
                        .filter(s -> s.toLowerCase().startsWith(args[1].toLowerCase()))
                        .toList();
            }
            if (args[0].equalsIgnoreCase("remove")) {
                return allowedList.stream()
                        .map(player -> (String) player.get("name"))
                        .filter(s -> s.toLowerCase().startsWith(args[1].toLowerCase()))
                        .toList();
            }
        }
        return List.of();
    }

    public static void register() {
        Main.getInstance().getCommand("whitelist").setExecutor(new WhitelistCommand());
        Main.getInstance().getCommand("whitelist").setTabCompleter(new WhitelistCommand());
    }
}
