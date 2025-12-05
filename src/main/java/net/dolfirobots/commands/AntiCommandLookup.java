package net.dolfirobots.commands;

import net.dolfirobots.chat.Messanger;
import net.dolfirobots.utils.MainConfig;
import net.dolfirobots.utils.PermissionManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerCommandSendEvent;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class AntiCommandLookup implements Listener {
    public static final List<String> ALLOWED_COMMANDS = List.of("msg", "spawn", "clan", "pl", "plugins", "msg");

    @EventHandler
    public void onTabComplete(PlayerCommandSendEvent event) {
        Player player = event.getPlayer();
        if (PermissionManager.COMMAND_BLOCK_BYPASS.checkPlayer(player)) {
            return;
        }

        Set<String> allowed = ALLOWED_COMMANDS.stream().map(String::toLowerCase).collect(Collectors.toSet());

        if (PermissionManager.COMMAND_BLOCK_BYPASS_COREPROTECT.checkPlayer(player)) {
            allowed.addAll(List.of("co", "coreprotect"));
        }
        event.getCommands().removeIf(cmd -> !allowed.contains(cmd.toLowerCase()));
    }

    @EventHandler
    public void onCommand(PlayerCommandPreprocessEvent event) {
        Player player = event.getPlayer();
        if (PermissionManager.COMMAND_BLOCK_BYPASS.checkPlayer(player)) {
            return;
        }

        String command = event.getMessage().split(" ")[0].replace("/", "").toLowerCase();

        if (List.of("pl", "plugins").contains(command)) {
            player.sendMessage(Component.text("ℹ ", NamedTextColor.BLUE)
                    .append(Component.text("Server Plugins (3):", NamedTextColor.WHITE))
                    .appendNewline()
                    .append(Component.text("Paper Plugins (0):", NamedTextColor.BLUE))
                    .appendNewline()
                    .append(Component.text("Bukkit Plugins (3)", NamedTextColor.GOLD))
                    .appendNewline()
                    .append(LegacyComponentSerializer.legacySection().deserialize("§8- §aDolfiAttack§f, §aLuckPerms§f, §aProtocolLib§f, §aTAB"))
            );
            event.setCancelled(true);
            return;
        }

        if (PermissionManager.COMMAND_BLOCK_BYPASS_COREPROTECT.checkPlayer(player) && List.of("co", "coreprotect").contains(command)) {
            return;
        }

        if (!ALLOWED_COMMANDS.contains(command)) {
            Messanger.sendMessage(Component.text("Der Command ", NamedTextColor.RED)
                    .append(Component.text("\"" + event.getMessage() + "\" ", NamedTextColor.YELLOW))
                    .append(Component.text("existiert nicht!", NamedTextColor.RED)),
                    player
            );
            event.setCancelled(true);
        }
    }
}
