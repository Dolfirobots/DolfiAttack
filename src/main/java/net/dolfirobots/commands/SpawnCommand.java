package net.dolfirobots.commands;

import net.dolfirobots.Main;
import net.dolfirobots.chat.Messanger;
import net.dolfirobots.utils.MainConfig;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class SpawnCommand implements CommandExecutor, TabCompleter {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(MainConfig.getPrefix() + "§cOnly players can execute this command!");
            return true;
        }
        Messanger.sendMessage(Component.text("Du wurdest zum Spawn teleportiert!", NamedTextColor.GREEN), player);
        if (args.length == 0) {
            player.teleportAsync(new Location(Bukkit.getWorld("world"), 279, 113, -780, 0, 0));
        }
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        return List.of();
    }

    public static void updateCachedPlayers() {
        Bukkit.getScheduler().runTaskAsynchronously(Main.getInstance(), () -> {
            ConfigurationSection config = MainConfig.getConfig().getConfigurationSection("have-elytra");
            if (config == null) {
                MainConfig.getConfig().createSection("have-elytra");
            }

            cachedPlayers.addAll(new HashSet<>(MainConfig.getConfig().getStringList("have-elytra").stream().map(UUID::fromString).toList()));
        });
    }

    @EventHandler
    public void onArmorChange(PlayerArmorChangeEvent event) {
        Player player = event.getPlayer();
        ItemStack newArmor = event.getNewItem();

        if (!newArmor.isEmpty() && newArmor.getType() == Material.ELYTRA && cachedPlayers.contains(player.getUniqueId())) {
            updateCachedPlayers();
            Bukkit.getScheduler().runTaskAsynchronously(Main.getInstance(), () -> {
                cachedPlayers.add(player.getUniqueId());
                MainConfig.getConfig().set("have-elytra", cachedPlayers);
                MainConfig.saveConfig();
            });
        }
    }

    public static void register() {
        Bukkit.getScheduler().runTaskTimer(Main.getInstance(), new SpawnCommand(), 5L, 100L);
        Bukkit.getPluginManager().registerEvents(new SpawnCommand(), Main.getInstance());
        
        Main.getInstance().getCommand("spawn").setExecutor(new SpawnCommand());
        Main.getInstance().getCommand("spawn").setTabCompleter(new SpawnCommand());
    }

    @Override
    public void run() {
        updateCachedPlayers();
    }
}
