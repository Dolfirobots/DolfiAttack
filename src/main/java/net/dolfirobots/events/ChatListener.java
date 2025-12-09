package net.dolfirobots.events;

import io.papermc.paper.chat.ChatRenderer;
import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.TextReplacementConfig;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.model.user.User;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import static net.dolfirobots.chat.Messanger.getPlayerStats;

public class ChatListener implements Listener, ChatRenderer {

    @EventHandler
    public void onPlayerChat(AsyncChatEvent event) {
        Component message = event.message();
        Player player = event.getPlayer();

        if (message instanceof TextComponent textComponent) {
            TextReplacementConfig config = TextReplacementConfig.builder()
                    .match("(?i)\\blw\\b")
                    .replacement(Component.text("Liege wie").style(textComponent.style()))
                    .build();
            message = textComponent.replaceText(config);
        }

        message = itemPlaceholder(message, player);

        event.message(message);
        event.renderer(this);
    }

    public static Component inventoryPlaceholder(Component currentMessage, Player player) {
        Pattern pattern = Pattern.compile("\\[inventory]");
        Component message = currentMessage.replaceText(
                TextReplacementConfig.builder()
                        .match(pattern)
                        .replacement(
                                Component.text("[", NamedTextColor.GRAY)
                                        .append(Component.text("Inventar", NamedTextColor.YELLOW)
                                                .hoverEvent(HoverEvent.showText(Component.text(player.getName() + "'s Inventar")))
                                                .clickEvent(ClickEvent.callback(audience -> {
                                                    if (audience instanceof Player player1) {
                                                        player1.openInventory(player.getInventory());
                                                    }
                                                })))
                                        .append(Component.text("]", NamedTextColor.GRAY))
                        )
                        .build()
        );
        return message;
    }

    /**
     * Formates [item:...] to an item object in the chat
     * @param currentMessage Message with [item:...]
     * @param player player with the items
     * @return {@link Component}
     */
    public static Component itemPlaceholder(Component currentMessage, Player player) {
        Map<String, ItemStack> slotMap = new HashMap<>();
        slotMap.put("main", player.getInventory().getItemInMainHand());
        slotMap.put("off", player.getInventory().getItemInOffHand());
        slotMap.put("head", player.getInventory().getHelmet());
        slotMap.put("chest", player.getInventory().getChestplate());
        slotMap.put("legs", player.getInventory().getLeggings());
        slotMap.put("feet", player.getInventory().getBoots());
        slotMap.put("", slotMap.get("main"));
        Pattern pattern = Pattern.compile("\\[item(?::([a-zA-Z]+))?]");

        return currentMessage.replaceText(
                TextReplacementConfig.builder()
                        .match(pattern)
                        .times(5)
                        .replacement((match, builder) -> {
                            String slot = match.group(1);
                            if (slot == null) slot = "";
                            ItemStack item = slotMap.get(slot.toLowerCase());

                            if (item == null) {
                                return Component.text("[Ungültiger Slot]", NamedTextColor.RED);
                            }
                            if (item.getType().isAir()) {
                                return Component.text("[Leer]", NamedTextColor.GRAY);
                            }
                            return Component.text("[", NamedTextColor.GRAY)
                                    .append(Component.text(item.getAmount() + "x ", NamedTextColor.YELLOW))
                                    .append(Component.translatable(item.translationKey(), NamedTextColor.YELLOW)
                                            .hoverEvent(item.asHoverEvent())
                                    )
                                    .append(Component.text("]", NamedTextColor.GRAY));
                        }).build()
        );
    }

    public String getPrefix(Player player) {
        User user = LuckPermsProvider.get().getPlayerAdapter(Player.class).getUser(player);
        String prefix = user.getCachedData().getMetaData().getPrefix();
        return prefix != null ? prefix : "";
    }

    @Override
    public @NotNull Component render(@NotNull Player source, @NotNull Component sourceDisplayName, @NotNull Component message, @NotNull Audience viewer) {
        return LegacyComponentSerializer.legacySection().deserialize(getPrefix(source))
                .append(getPlayerStats(source.getPlayer()))
                .append(Component.text(": ", NamedTextColor.DARK_GRAY))
                .append(message);
    }
}
