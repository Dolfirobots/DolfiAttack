package net.dolfirobots.events;

import me.clip.placeholderapi.PlaceholderAPI;
import net.dolfirobots.Main;
import net.dolfirobots.chat.Messanger;
import net.dolfirobots.utils.MainConfig;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.luckperms.api.model.user.User;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import static net.dolfirobots.chat.Messanger.getNameMC;
import static net.dolfirobots.chat.Messanger.getPlayerStats;

/**
 * Join and Leave events
 * @author Dolfirobots
 */
public class JoinLeaveListener implements Listener {
    private static final String SERVICE = "JoinLeaveEvent";

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        User user = Main.getLuckPerms().getPlayerAdapter(Player.class).getUser(player);
        String prefix = user.getCachedData().getMetaData().getPrefix();
        String clan = user.getPrimaryGroup();

        player.setGameMode(GameMode.SURVIVAL);

        if (!clan.startsWith("clan_")) {
            clan = null;
        } else {
            clan.replace("clan_", "").toUpperCase();
        } // TODO: Implement clan message

        if (!player.hasPlayedBefore()) {
            Messanger.sendMessage(Component.text("§7Willkommen zu §9DolfiAttack 1§7, §e" + player.getName() + "§7!"), player);
            Messanger.sendMessage(Component.text("Du kannst mit Doppel Sprung von der Spawn Insel springen.", NamedTextColor.GRAY), player);
            Messanger.sendMessage(Component.text("Viel Spaß beim spielen!", NamedTextColor.GRAY), player);
            player.teleportAsync(Main.SPAWN);
        }

        final Component message = Component.text("[", NamedTextColor.GRAY)
                .append(Component.text("+", NamedTextColor.GREEN))
                .append(Component.text("]", NamedTextColor.GRAY))
                .appendSpace()
                .append(LegacyComponentSerializer.legacySection().deserializeOr(prefix, Component.empty()))
                .append(getPlayerStats(player));
        event.joinMessage(message);
    }

    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        User user = Main.getLuckPerms().getPlayerAdapter(Player.class).getUser(player);
        String prefix = user.getCachedData().getMetaData().getPrefix();

        final Component message = Component.text("[", NamedTextColor.GRAY)
                .append(Component.text("-", NamedTextColor.RED))
                .append(Component.text("]", NamedTextColor.GRAY))
                .appendSpace()
                .append(LegacyComponentSerializer.legacySection().deserializeOr(prefix, Component.empty()))
                .append(getPlayerStats(player));
        event.quitMessage(message);
    }

    @EventHandler
    public void onPlayerEarlyConnect(PlayerLoginEvent event) {
        Player player = event.getPlayer();
        URL profileWebsite = getNameMC(player.getUniqueId());

        final List<String> whitelistedPlayers = MainConfig.getConfig().getMapList("whitelist.allowed_players")
                .stream()
                .map(p -> (String) p.get("uuid"))
                .filter(Objects::nonNull)
                .toList();

        Component playerInfo = Component.text(player.getName(), NamedTextColor.GRAY)
                .hoverEvent(HoverEvent.showText(Component.text("UUID: ", NamedTextColor.GRAY)
                        .append(Component.text(player.getUniqueId().toString(), NamedTextColor.YELLOW))
                        .appendNewline()
                        .append(Component.text("Sprache: ", NamedTextColor.GRAY))
                        .append(Component.text(player.locale().getLanguage(), NamedTextColor.YELLOW))
                ));

        if (profileWebsite != null) {
            playerInfo = playerInfo.clickEvent(ClickEvent.openUrl(profileWebsite));
        }

        if (MainConfig.getConfig().getBoolean("whitelist.enabled", false) && !whitelistedPlayers.contains(player.getUniqueId().toString())) {
            final Component message = Component.text("[", NamedTextColor.GRAY)
                    .append(Component.text("!", NamedTextColor.LIGHT_PURPLE))
                    .append(Component.text("]", NamedTextColor.GRAY))
                    .appendSpace()
                    .append(playerInfo);
            Messanger.broadcast(message, false);
            final Component kickMessage = Component.text(MainConfig.getPrefix())
                    .appendNewline()
                    .appendNewline()
                    .append(Component.text("Du darfst nicht auf den", NamedTextColor.RED))
                    .appendSpace()
                    .append(Component.text("DolfiAttack 1", NamedTextColor.BLUE))
                    .appendSpace()
                    .append(Component.text("Server!", NamedTextColor.RED));
            event.disallow(PlayerLoginEvent.Result.KICK_WHITELIST, kickMessage);
        }

        List<String> allowedMaintenancePlayers = MainConfig.getConfig().getMapList("maintenance.allowed_players")
                .stream()
                .map(p -> (String) p.get("uuid"))
                .filter(Objects::nonNull)
                .toList();

        if (MainConfig.getConfig().getBoolean("maintenance.enabled", false) && !allowedMaintenancePlayers.contains(player.getUniqueId().toString())) {
            final Component message = Component.text("[", NamedTextColor.GRAY)
                    .append(Component.text("!", NamedTextColor.LIGHT_PURPLE))
                    .append(Component.text("]", NamedTextColor.GRAY))
                    .appendSpace()
                    .append(playerInfo);
            Messanger.broadcast(message);

            final Component kickMessage = Component.text(MainConfig.getPrefix())
                    .appendNewline()
                    .appendNewline()
                    .append(Component.text("Der Server ist gerade in", NamedTextColor.GRAY))
                    .appendSpace()
                    .append(Component.text("Wartungsarbeiten", NamedTextColor.YELLOW))
                    .append(Component.text("!", NamedTextColor.GRAY));
            event.disallow(PlayerLoginEvent.Result.KICK_OTHER, kickMessage);
        }
    }
}
