package net.dolfirobots.events;

import net.dolfirobots.Main;
import net.dolfirobots.chat.Messanger;
import net.dolfirobots.utils.MainConfig;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerBedEnterEvent;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class SleepListener implements Listener, Runnable {
    public static Map<UUID, Boolean> cachedPlayers = new ConcurrentHashMap<>();


    @EventHandler
    public void onBedEnter(PlayerBedEnterEvent event) {
        Player player = event.getPlayer();

        if (!cachedPlayers.containsKey(player.getUniqueId())) {
            final Component message = Component.text("Willst du automatisch ", NamedTextColor.GRAY)
                    .append(Component.text("\"Liege wie\" ", NamedTextColor.YELLOW))
                    .append(Component.text("in den Chat senden, wenn du ins Bett gehst?", NamedTextColor.GRAY));

            final Component yes = Component.text("[", NamedTextColor.DARK_GRAY)
                    .append(Component.text("✓", NamedTextColor.GREEN))
                    .append(Component.text("]", NamedTextColor.DARK_GRAY))
                    .hoverEvent(
                            HoverEvent.showText(Component.text("Ja", NamedTextColor.GREEN, TextDecoration.BOLD).appendNewline()
                                    .append(Component.text("/settings bedmsg true", NamedTextColor.DARK_GRAY, TextDecoration.ITALIC))
                            )
                    );

            final Component no = Component.text("[", NamedTextColor.DARK_GRAY)
                    .append(Component.text("X", NamedTextColor.RED))
                    .append(Component.text("]", NamedTextColor.DARK_GRAY))
                    .hoverEvent(
                            HoverEvent.showText(Component.text("Nein", NamedTextColor.RED, TextDecoration.BOLD).appendNewline()
                                    .append(Component.text("/settings bedmsg false", NamedTextColor.DARK_GRAY, TextDecoration.ITALIC))
                            )
                    );

            final Component question = yes.clickEvent(ClickEvent.runCommand("settings bedmsg true")).appendSpace()
                    .append(no.clickEvent(ClickEvent.runCommand("settings bedmsg false")));
            Messanger.sendMessage(message, player);
            Messanger.sendMessage(question, player);
        }

        if (cachedPlayers.getOrDefault(player.getUniqueId(), false)) {
            player.chat("Liege wie");
        }
    }

    @Override
    public void run() {
        syncPlayers();
    }

    public static void syncPlayers() {
        Bukkit.getScheduler().runTaskAsynchronously(Main.getInstance(), () -> {
            final ConfigurationSection config = MainConfig.getConfig().getConfigurationSection("settings.bedmsg");
            if (config == null) {
                MainConfig.getConfig().createSection("settings.bedmsg");
            } else {
                config.getKeys(false).forEach(key -> {
                    cachedPlayers.put(UUID.fromString(key), config.getBoolean(key, false));
                });
            }
        });
    }

    public static void register() {
        Bukkit.getPluginManager().registerEvents(new SleepListener(), Main.getInstance());
        Bukkit.getScheduler().runTaskTimer(Main.getInstance(), new SleepListener(), 20 * 3, 20 * 10);
    }
}
