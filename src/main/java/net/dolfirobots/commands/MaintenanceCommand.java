package net.dolfirobots.commands;

import me.clip.placeholderapi.PlaceholderAPI;
import net.dolfirobots.Main;
import net.dolfirobots.chat.Messanger;
import net.dolfirobots.utils.MainConfig;
import net.dolfirobots.utils.MojangWebAPI;
import net.dolfirobots.utils.PermissionManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.JoinConfiguration;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
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

import static net.dolfirobots.commands.WhitelistCommand.sendSender;

public class MaintenanceCommand implements CommandExecutor, TabCompleter {

    public static void handleToggle(boolean user, CommandSender sender) {
        boolean isEnable = MainConfig.getConfig().getBoolean("maintenance.enabled", false);
        if (isEnable == user) {
            sendSender(Component.text("Maintenance ", NamedTextColor.YELLOW)
                            .append(Component.text("is already ", NamedTextColor.GRAY))
                            .append(user ? Component.text("enabled", NamedTextColor.GREEN) : Component.text("disabled", NamedTextColor.RED))
                            .append(Component.text("!", NamedTextColor.GRAY)),
                    sender
            );
        } else {
            MainConfig.getConfig().set("maintenance.enabled", user);
            MainConfig.saveConfig();
            sendSender(Component.text("Maintenance ", NamedTextColor.YELLOW)
                            .append(Component.text("is now ", NamedTextColor.GRAY))
                            .append(user ? Component.text("enabled", NamedTextColor.GREEN) : Component.text("disabled", NamedTextColor.RED))
                            .append(Component.text("!", NamedTextColor.GRAY)),
                    sender
            );
        }
        Main.loadMaintenanceMode();
    }

    // /maintenance enable/disable <- General handling
    // /maintenance add/remove/list <- Player handling
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (sender instanceof Player player) {
            if (!PermissionManager.COMMAND_MAINTENANCE.checkPlayer(player)) {
                Messanger.sendMessage(Component.text("Du hast keine Berechtigung diesen Command auszuführen!", NamedTextColor.RED), player);
                return true;
            }
        }

        if (args.length == 0) {
            sendUsage(sender);
            return true;
        }

        List<Map<?, ?>> allowedPlayers = MainConfig.getConfig().getMapList("maintenance.allowed_players");


        switch (args[0].toLowerCase()) {
            case "enable":
                handleToggle(true, sender);
                break;
            case "disable":
                handleToggle(false, sender);
                break;
            case "add":
                if (args.length < 2) {
                    sendUsage(sender);
                    return true;
                }
                for (Map<?, ?> player : allowedPlayers) {
                    if (player.get("name").equals(args[1])) {
                        sendSender(Component.text(args[1], NamedTextColor.YELLOW)
                                .append(Component.text(" can already join when ", NamedTextColor.GRAY))
                                .append(Component.text("Maintenance mode ", NamedTextColor.YELLOW))
                                .append(Component.text("is ", NamedTextColor.GRAY))
                                .append(Component.text("enabled", NamedTextColor.GREEN))
                                .append(Component.text(".", NamedTextColor.GRAY)),
                                sender
                        );
                        return true;
                    }
                }

                MojangWebAPI.getUUIDAsync(args[1]).thenAccept(uuid -> {
                    if (uuid.isEmpty()) {
                        Bukkit.getScheduler().runTask(Main.getInstance(), () -> {
                            sendSender(Component.text("The player ", NamedTextColor.RED)
                                    .append(Component.text(args[1], NamedTextColor.YELLOW))
                                    .appendSpace()
                                    .append(Component.text("does not exists!", NamedTextColor.RED)),
                                    sender
                            );
                        });
                        return;
                    }

                    Map<String, String> newPlayer = Map.of("name", args[1], "uuid", uuid.get().toString());
                    // Saves data like this:
                    // allowed_players:
                    //   - name: Dolfirobots
                    //     uuid: 5694f474-cd40-42fb-b0bd-f630330176e5

                    allowedPlayers.add(newPlayer);
                    MainConfig.getConfig().set("maintenance.allowed_players", allowedPlayers);
                    MainConfig.saveConfig();

                    Bukkit.getScheduler().runTask(Main.getInstance(), () -> {
                        sendSender(Component.text(args[1], NamedTextColor.YELLOW)
                                .hoverEvent(HoverEvent.showText(Component.text("UUID: ", NamedTextColor.GRAY)
                                        .append(Component.text(uuid.get().toString(), NamedTextColor.YELLOW)
                                        ))
                                )
                                .appendSpace()
                                .append(Component.text("can now join when ", NamedTextColor.GRAY))
                                .append(Component.text("Maintenance mode", NamedTextColor.YELLOW))
                                .append(Component.text("is ", NamedTextColor.GRAY))
                                .append(Component.text("enabled", NamedTextColor.GREEN))
                                .append(Component.text(".", NamedTextColor.GRAY)),
                                sender
                        );
                    });
                });
                break;
            case "remove":
                if (args.length < 2) {
                    sendUsage(sender);
                    return true;
                }

                boolean removed = allowedPlayers.removeIf(player -> player.get("name").equals(args[1]) || player.get("uuid").equals(args[1]));
                if (removed) {
                    MainConfig.getConfig().set("maintenance.allowed_players", allowedPlayers);
                    MainConfig.saveConfig();
                    sendSender(Component.text(args[1], NamedTextColor.YELLOW)
                            .appendSpace()
                            .append(Component.text("now can't join when ", NamedTextColor.GRAY))
                            .append(Component.text("Maintenance mode ", NamedTextColor.YELLOW))
                            .append(Component.text("is ", NamedTextColor.GRAY))
                            .append(Component.text("enabled", NamedTextColor.GREEN))
                            .append(Component.text("!", NamedTextColor.GRAY)),
                            sender
                    );
                } else {
                    sendSender(Component.text(args[1], NamedTextColor.YELLOW)
                            .appendSpace()
                            .append(Component.text("already can't join the server when "))
                            .append(Component.text("Maintenance mode ", NamedTextColor.YELLOW))
                            .append(Component.text("is ", NamedTextColor.GRAY))
                            .append(Component.text("enabled", NamedTextColor.GREEN))
                            .append(Component.text("!", NamedTextColor.GRAY)),
                            sender
                    );
                }
                break;
            case "list":
                List<String> usernames = allowedPlayers.stream()
                        .map(p -> (String) p.get("name"))
                        .filter(Objects::nonNull)
                        .toList();

                List<Component> usernamesComponents = usernames.stream()
                        .map(name -> Component.text(name, NamedTextColor.YELLOW).asComponent())
                        .toList();

                sendSender(Component.text("There are currently ", NamedTextColor.GRAY)
                        .append(Component.text(usernames.size(), NamedTextColor.YELLOW))
                        .appendSpace()
                        .append(Component.text("users that can join when ", NamedTextColor.GRAY))
                        .append(Component.text("Maintenance mode ", NamedTextColor.YELLOW))
                        .append(Component.text("is ", NamedTextColor.GRAY))
                        .append(Component.text("enabled", NamedTextColor.GREEN))
                        .append(Component.text(":", NamedTextColor.GRAY))
                        .append(Component.join(JoinConfiguration.separator(Component.text(", ").color(NamedTextColor.GRAY)), usernamesComponents)),
                        sender
                );
                break;
            default:
                sendUsage(sender);
        }

        return true;
    }

    public static void sendUsage(CommandSender sender) {
        Component header = Component.text("⚙ Maintenance Command Help", NamedTextColor.GOLD).decorate(TextDecoration.BOLD);
        Component separator = Component.text("──────────────────────────", NamedTextColor.DARK_GRAY);
        Component general = Component.newline()
                .append(Component.text("General actions:", NamedTextColor.GRAY));

        Component enable = Component.text(" • ", NamedTextColor.DARK_GRAY)
                .append(Component.text("/maintenance enable", NamedTextColor.YELLOW))
                .hoverEvent(HoverEvent.showText(Component.text("Activate maintenance mode")))
                .clickEvent(ClickEvent.suggestCommand("/maintenance enable"));

        Component disable = Component.text(" • ", NamedTextColor.DARK_GRAY)
                .append(Component.text("/maintenance disable", NamedTextColor.YELLOW))
                .hoverEvent(HoverEvent.showText(Component.text("Deactivate maintenance mode")))
                .clickEvent(ClickEvent.suggestCommand("/maintenance disable"));

        Component player = Component.newline()
                .append(Component.text("Player handling:", NamedTextColor.GRAY));

        Component add = Component.text(" • ", NamedTextColor.DARK_GRAY)
                .append(Component.text("/maintenance add <player>", NamedTextColor.YELLOW))
                .hoverEvent(HoverEvent.showText(Component.text("Allow a player to join during maintenance")))
                .clickEvent(ClickEvent.suggestCommand("/maintenance add "));

        Component remove = Component.text(" • ", NamedTextColor.DARK_GRAY)
                .append(Component.text("/maintenance remove <player>", NamedTextColor.YELLOW))
                .hoverEvent(HoverEvent.showText(Component.text("Remove a player from the allowed-list")))
                .clickEvent(ClickEvent.suggestCommand("/maintenance remove "));

        Component list = Component.text(" • ", NamedTextColor.DARK_GRAY)
                .append(Component.text("/maintenance list", NamedTextColor.YELLOW))
                .hoverEvent(HoverEvent.showText(Component.text("List all players allowed to join")))
                .clickEvent(ClickEvent.runCommand("/maintenance list"));
        Component footer = Component.text("\n──────────────────────────", NamedTextColor.DARK_GRAY);

        Component finalMessage = Component.empty()
                .append(header).appendNewline()
                .append(separator).append(general).appendNewline()
                .append(enable).appendNewline()
                .append(disable).append(player).appendNewline()
                .append(add).appendNewline()
                .append(remove).appendNewline()
                .append(list).appendNewline()
                .append(footer);
        sendSender(finalMessage, sender);
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (sender instanceof Player player) {
            if (!PermissionManager.COMMAND_MAINTENANCE.checkPlayer(player)) {
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
            List<Map<?, ?>> allowedList = MainConfig.getConfig().getMapList("maintenance.allowed_players");
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
        Main.getInstance().getCommand("maintenance").setExecutor(new MaintenanceCommand());
        Main.getInstance().getCommand("maintenance").setTabCompleter(new MaintenanceCommand());
    }
}
