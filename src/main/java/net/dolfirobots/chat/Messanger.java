package net.dolfirobots.chat;

import com.comphenix.protocol.PacketType;
import net.dolfirobots.Main;
import net.dolfirobots.utils.MainConfig;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.luckperms.api.model.user.User;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Statistic;
import org.bukkit.entity.Player;
import org.eclipse.sisu.space.BundleClassSpace;

import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Timestamp;
import java.util.UUID;

import static net.dolfirobots.utils.MainConfig.getPrefix;


public class Messanger {
    // Basic messages

    /**
     * Send a message in the console with prefix
     * @param message
     */
    public static void sendConsole(String message) {
        Bukkit.getConsoleSender().sendMessage(getPrefix() + "§7" + message);
    }

    public static void sendConsole(String service, String message) {
        Bukkit.getConsoleSender().sendMessage(getPrefix() + "§8[§e" + service + "§8] §7" + message);
    }

    /**
     * Send a Player a message with prefix
     * @param player the Player
     * @param message Message you wanne send
     */
    public static void sendMessage(Component message, Player player) {
        Component prefix = LegacyComponentSerializer.legacySection().deserialize(MainConfig.getPrefix());
        player.sendMessage(prefix.append(message));
    }

    /**
     * Sends all players on the server a message
     * @param message Message as {@link Component}
     */
    public static void broadcast(Component message) {
        broadcast(message, true);
    }
    public static void broadcast(Component message, boolean sendPrefix) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (sendPrefix) {
                sendMessage(message, player);
            } else {
                player.sendMessage(message);
            }
        }
    }

    public static Component formatTime(int ticks) {
        long totalSeconds = ticks / 20;
        long hours = totalSeconds / 3600;
        long minutes = (totalSeconds % 3600) / 60;
        long seconds = totalSeconds % 60;

        Component component = Component.empty();

        if (hours > 0) component = component.append(Component.text(hours, NamedTextColor.YELLOW)).append(Component.text("h ", NamedTextColor.GRAY));
        if (minutes > 0) component = component.append(Component.text(minutes, NamedTextColor.YELLOW)).append(Component.text("m ", NamedTextColor.GRAY));
        if (seconds > 0 || (hours == 0 && minutes == 0)) component = component.append(Component.text(seconds, NamedTextColor.YELLOW)).append(Component.text("s", NamedTextColor.GRAY));

        return component;
    }

    public static URL getNameMC(UUID uuid) {
        try {
            return new URL("https://namemc.com/profile/" + uuid);
        } catch (MalformedURLException e) {
            Messanger.sendException("Test", "Invalid URL", e); // TODO:
            return null;
        }
    }

    public static Component getPlayerStats(Player player, Component targetText) {
        User user = Main.getLuckPerms().getPlayerAdapter(Player.class).getUser(player);
        String clan = user.getPrimaryGroup();
        if (!clan.startsWith("clan_")) {
            clan = null;
        } else {
            clan = clan.replace("clan_", "").toUpperCase();
        }
        if (targetText == null) {
            targetText = Component.text(player.getName(), NamedTextColor.GRAY);
        }
        return targetText
                .hoverEvent(HoverEvent.showText(
                        Component.text("UUID: ", NamedTextColor.GRAY)
                                .append(Component.text(player.getUniqueId().toString(), NamedTextColor.YELLOW))
                                .appendNewline()
                                .append(Component.text("Clan: ", NamedTextColor.GRAY))
                                .append(LegacyComponentSerializer.legacySection().deserializeOr(clan, Component.text("Kein Clan")).color(NamedTextColor.YELLOW).decorate(TextDecoration.BOLD))
                                .appendNewline()
                                .append(Component.text("Sprache: ", NamedTextColor.GRAY))
                                .append(Component.text(player.locale().getLanguage(), NamedTextColor.YELLOW))
                                .appendNewline()
                                .append(Component.text("Tode: ", NamedTextColor.GRAY))
                                .append(Component.text(player.getStatistic(Statistic.DEATHS), NamedTextColor.YELLOW))
                                .appendNewline()
                                .append(Component.text("Kills: ", NamedTextColor.GRAY))
                                .append(Component.text(player.getStatistic(Statistic.PLAYER_KILLS), NamedTextColor.YELLOW))
                                .appendNewline()
                                .append(Component.text("Spielzeit: ", NamedTextColor.GRAY))
                                .append(formatTime(player.getStatistic(Statistic.PLAY_ONE_MINUTE)))
                ))
                .clickEvent(ClickEvent.suggestCommand("/msg " + player.getName() + " "));
    }
    public static Component getPlayerStats(Player player) {
        return getPlayerStats(player, null);
    }

    /**
     * Centers the message to make it looks better
     * @param message
     * @param length
     * @return centered String
     */
    public static String centerMessage(String message, int length) {
        int spaces = (length - ChatColor.stripColor(message).length()) / 2;
        if (spaces < 0) spaces = 0;
        return " ".repeat(spaces) + message;
    }

    /**
     * Sends an error to the console
     * @param service The class where the error is
     * @param errorMessage The error description
     */
    public static void sendError(String service, String errorMessage) {
        if (service != null && errorMessage != null) {
            // PREFIX [Example Service] getMessanger was 0
            sendConsole("§8[§e" + service + "§8] §7" + errorMessage);
        }
    }

    /**
     * Sends an exception to the console
     * @param service The class where the exception is
     * @param context The context where the exception was thrown
     * @param exception The exception thrown
     */
    public static void sendException(String service, String context, Exception exception) {
        if (service != null && context != null && exception != null) {
            sendConsole("§8[§e" + service + "§8] §7" + context + " §8->§e " + exception.getClass().getSimpleName() + "§8: §c" + exception.getMessage());
        }
    }
}
