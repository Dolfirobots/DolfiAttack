package net.dolfirobots.commands;

import net.dolfirobots.Main;
import net.dolfirobots.chat.Messanger;
import net.dolfirobots.utils.MainConfig;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.Statistic;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static net.dolfirobots.Main.getLuckPerms;
import static net.dolfirobots.chat.Messanger.formatTime;
import static net.dolfirobots.chat.Messanger.formatTimestamp;
import static net.dolfirobots.commands.WhitelistCommand.sendSender;

public class StatsCommand implements CommandExecutor, TabCompleter, Runnable {

    public static Map<UUID, String> cachedOfflinePlayers = new ConcurrentHashMap<>();

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length == 0) {
            if (sender instanceof Player player) {
                getStatsMessageOffline(player).thenAcceptAsync(result -> {
                    Bukkit.getScheduler().runTask(Main.getInstance(),bukkitTask -> {
                        Messanger.sendMessage(
                                Component.text("Deine Stats:", NamedTextColor.GRAY)
                                        .appendNewline()
                                        .append(result),
                                player
                        );
                    });
                });
            } else {
                sender.sendMessage(MainConfig.getPrefix() + "§cOnly players can execute this command!");
            }
        }
        if (args.length == 1) {
            OfflinePlayer targetPlayer = Bukkit.getOfflinePlayer(args[0]);
            if (!targetPlayer.hasPlayedBefore() && targetPlayer.getPlayer() == null) {
                sendSender(Component.text("Der Spieler ", NamedTextColor.RED)
                        .append(Component.text(args[0], NamedTextColor.YELLOW)).appendSpace()
                        .append(Component.text("existiert nicht!", NamedTextColor.RED)),
                        sender
                );
            }
            getStatsMessageOffline(targetPlayer).thenAcceptAsync(result -> {
                Bukkit.getScheduler().runTask(Main.getInstance(),bukkitTask -> {
                    sendSender(
                            Component.text(targetPlayer.getName(), NamedTextColor.YELLOW)
                                    .append(Component.text("'s Stats: ", NamedTextColor.GRAY))
                                    .appendNewline()
                                    .append(result),
                            sender
                    );
                });
            });
        }
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length == 1) {
            return Bukkit.getOnlinePlayers().stream().map(Player::getName).toList();
        }
        return List.of();
    }

    public static CompletableFuture<Component> getStatsMessageOffline(OfflinePlayer player) {
        Component prefix = Component.text("* ", NamedTextColor.DARK_GRAY);

        return getLuckPerms().getUserManager().loadUser(player.getUniqueId())
                .thenApplyAsync(user -> {
                    String clan = user.getPrimaryGroup();
                    if (!clan.startsWith("clan_")) {
                        clan = null;
                    } else {
                        clan = clan.replace("clan_", "").toUpperCase();
                    }

                    return prefix.append(Component.text("UUID: ", NamedTextColor.GRAY))
                            .append(Component.text(player.getUniqueId().toString(), NamedTextColor.YELLOW))
                            .appendNewline()
                            .append(prefix).append(Component.text("Spielzeit: ", NamedTextColor.GRAY))
                            .append(formatTime(player.getStatistic(Statistic.PLAY_ONE_MINUTE)))
                            .appendNewline()
                            .append(prefix).append(Component.text("Clan: ", NamedTextColor.GRAY))
                            .append(LegacyComponentSerializer.legacySection().deserializeOr(clan, Component.text("Kein Clan")).color(NamedTextColor.YELLOW).decorate(TextDecoration.BOLD))
                            .appendNewline()
                            .append(prefix).append(Component.text("Tode: ", NamedTextColor.GRAY))
                            .append(Component.text(player.getStatistic(Statistic.DEATHS), NamedTextColor.YELLOW))
                            .appendNewline()
                            .append(prefix).append(Component.text("Letzter Login vor: ", NamedTextColor.GRAY))
                            .append(formatTimestamp(Timestamp.from(Instant.ofEpochMilli(System.currentTimeMillis() - player.getLastLogin()))))
                            .appendNewline()
                            .append(prefix).append(Component.text("Kills: ", NamedTextColor.GRAY))
                            .append(Component.text(player.getStatistic(Statistic.PLAYER_KILLS), NamedTextColor.YELLOW))
                            .appendNewline();
                });
    }

    public static void register() {
        Main.getInstance().getCommand("stats").setExecutor(new StatsCommand());
        Main.getInstance().getCommand("stats").setTabCompleter(new StatsCommand());
    }

    @Override
    public void run() {
        updatePlayersCache();
    }

    public static void updatePlayersCache() {
        Bukkit.getScheduler().runTaskAsynchronously(Main.getInstance(), () -> {
            Arrays.stream(Bukkit.getOfflinePlayers()).forEach(offlinePlayer -> cachedOfflinePlayers.put(offlinePlayer.getUniqueId(), offlinePlayer.getName()));
        });
    }
}
