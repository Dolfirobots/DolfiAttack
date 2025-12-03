package net.dolfirobots.commands;

import net.dolfirobots.Main;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class ClanCommand implements CommandExecutor, TabCompleter {

    public static Map<UUID, List<String>> invites = new HashMap<>();

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        sender.sendMessage("§cFunktioniert gerade noch nicht");
//        if (!(sender instanceof Player player)) return true;
//
//        if (args.length == 0) {
//            sendUsage(sender);
//            return true;
//        }
//
//        switch (args[0].toLowerCase()) {
//            case "create" -> {
//                // /clan create <name> <color>
//                if (args.length < 3) {
//                    player.sendMessage("§cBenutzung: /clan create <Name> <Farbe>");
//                    return true;
//                }
//
//                String clanName = args[1];
//                String colorStr = args[2].toUpperCase();
//
//                ChatColor color;
//                try {
//                    color = ChatColor.valueOf(colorStr);
//                } catch (IllegalArgumentException e) {
//                    sendMessage("§cUngültige Farbe. Beispiele: RED, BLUE, GREEN, GOLD...", player);
//                    return true;
//                }
//
//                if (Clan.exists(clanName)) {
//                    sendMessage("§cDer Clan Name ist bereits vergeben!", player);
//                    return true;
//                }
//                Clan clan = new Clan(clanName, color, player.getUniqueId());
//                clan.createGroup();
//
//                sendMessage("§aClan " + clan.getPrefix() + " wurde erfolgreich erstellt!", player);
//            }
//            case "delete" -> {
//                if (args.length < 2) {
//                    player.sendMessage("§cBenutzung: /clan delete <Name>");
//                    return true;
//                }
//                String clanName = args[1];
//
//                if (!Clan.exists(clanName)) {
//                    player.sendMessage("§cDieser Clan existiert nicht!");
//                    return true;
//                }
//                Clan clan = new Clan(clanName);
//                if (!clan.isPlayerAdmin(player.getUniqueId())) {
//                    player.sendMessage("§cNur Admins können diesen Clan löschen!");
//                    return true;
//                }
//
//                clan.deleteClan();
//                player.sendMessage("§cClan §e" + clanName + " §cwurde erfolgreich gelöscht!");
//            }
//            case "invite" -> {
//                if (args.length < 3) { // /clan invite <ClanName> <Spieler>
//                    player.sendMessage("§cBenutzung: /clan invite <ClanName> <Spieler>");
//                    return true;
//                }
//
//                String clanName = args[1];
//                String targetName = args[2];
//
//                if (!Clan.exists(clanName)) {
//                    player.sendMessage("§cDieser Clan existiert nicht!");
//                    return true;
//                }
//
//                Clan clan = new Clan(clanName);
//
//                Player target = Bukkit.getPlayerExact(targetName);
//                if (target == null) {
//                    player.sendMessage("§cDer Spieler §e" + targetName + " §cist nicht online!");
//                    return true;
//                }
//
//                if (clan.getPlayers().contains(target.getUniqueId())) {
//                    player.sendMessage("§cDer Spieler ist bereits im Clan!");
//                    return true;
//                }
//                ClanCommand.invites.computeIfAbsent(target.getUniqueId(), k -> new ArrayList<>()).add(clanName);
//
//                player.sendMessage("§aDu hast §e" + target.getName() + " §ain den Clan eingeladen.");
//                target.sendMessage("§aDu wurdest in den Clan §e" + clan.getPrefix() + " §aeingeladen!");
//                target.sendMessage("§7Benutze §e/clan accept " + clanName + " §7um die Einladung anzunehmen.");
//            }
//            case "accept" -> {
//                if (args.length < 2) { // /clan accept <ClanName>
//                    player.sendMessage("§cBenutzung: /clan accept <ClanName>");
//                    return true;
//                }
//                String clanName = args[1];
//
//                if (!Clan.exists(clanName)) {
//                    player.sendMessage("§cDieser Clan existiert nicht mehr!");
//                    return true;
//                }
//
//                List<String> invitesForPlayer = ClanCommand.invites.getOrDefault(player.getUniqueId(), new ArrayList<>());
//                if (!invitesForPlayer.contains(clanName)) {
//                    player.sendMessage("§cDu wurdest nicht in diesen Clan eingeladen!");
//                    return true;
//                }
//
//                Clan clan = new Clan(clanName);
//
//                clan.addPlayer(player.getUniqueId());
//
//                invitesForPlayer.remove(clanName);
//                if (invitesForPlayer.isEmpty()) {
//                    ClanCommand.invites.remove(player.getUniqueId());
//                } else {
//                    ClanCommand.invites.put(player.getUniqueId(), invitesForPlayer);
//                }
//                player.sendMessage("§aDu bist nun Mitglied des Clans " + clan.getPrefix() + "!");
//            }
//            case "decline" -> {
//                if (args.length < 2) { // /clan decline <ClanName>
//                    player.sendMessage("§cBenutzung: /clan decline <ClanName>");
//                    return true;
//                }
//                String clanName = args[1];
//                List<String> invitesForPlayer = ClanCommand.invites.getOrDefault(player.getUniqueId(), new ArrayList<>());
//                if (!invitesForPlayer.contains(clanName)) {
//                    player.sendMessage("§cDu wurdest nicht in diesen Clan eingeladen!");
//                    return true;
//                }
//                invitesForPlayer.remove(clanName);
//                if (invitesForPlayer.isEmpty()) {
//                    ClanCommand.invites.remove(player.getUniqueId());
//                } else {
//                    ClanCommand.invites.put(player.getUniqueId(), invitesForPlayer);
//                }
//
//                player.sendMessage("§cDu hast die Einladung in den Clan §e" + clanName + " §cabgelehnt.");
//            }
//            case "removeinvite" -> {
//                if (args.length < 3) { // /clan removeinvite <ClanName> <Spieler>
//                    player.sendMessage("§cBenutzung: /clan removeinvite <ClanName> <Spieler>");
//                    return true;
//                }
//
//                String clanName = args[1];
//                String targetName = args[2];
//
//                if (!Clan.exists(clanName)) {
//                    player.sendMessage("§cDieser Clan existiert nicht!");
//                    return true;
//                }
//                Clan clan = new Clan(clanName);
//                Player target = Bukkit.getPlayerExact(targetName);
//                if (target == null) {
//                    player.sendMessage("§cDer Spieler §e" + targetName + " §cist nicht online!");
//                    return true;
//                }
//                List<String> invitesForTarget = ClanCommand.invites.getOrDefault(target.getUniqueId(), new ArrayList<>());
//
//                if (!invitesForTarget.remove(clanName)) {
//                    player.sendMessage("§cDieser Spieler hat keine Einladung für diesen Clan.");
//                    return true;
//                }
//
//                if (invitesForTarget.isEmpty()) {
//                    ClanCommand.invites.remove(target.getUniqueId());
//                } else {
//                    ClanCommand.invites.put(target.getUniqueId(), invitesForTarget);
//                }
//
//                player.sendMessage("§aDie Einladung von §e" + target.getName() + " §awurde entfernt.");
//                target.sendMessage("§cDeine Einladung in den Clan §e" + clan.getPrefix() + " §cwurde entfernt.");
//            }
//            case "get" -> {
//                if (args.length == 1) {
//                    Clan playerClan = null;
//                    for (String clanName : ClanCommand.invites.values().stream().flatMap(List::stream).toList()) {
//                        if (new Clan(clanName).getPlayers().contains(player.getUniqueId())) {
//                            playerClan = new Clan(clanName);
//                            break;
//                        }
//                    }
//
//                    if (playerClan == null) {
//                        player.sendMessage("§cDu bist in keinem Clan!");
//                        return true;
//                    }
//
//                    player.sendMessage("§7--- §eClan Info §7---");
//                    player.sendMessage("§eName: §f" + playerClan.getName());
//                    player.sendMessage("§ePrefix: §f" + playerClan.getPrefix());
//                    player.sendMessage("§eMitglieder: §f" + playerClan.getPlayers().size());
//                    player.sendMessage("§eAdmin: §f" + (playerClan.isPlayerAdmin(player.getUniqueId()) ? "§aJa" : "§cNein"));
//
//                } else if (args.length == 2) {
//                    String clanName = args[1];
//
//                    if (!Clan.exists(clanName)) {
//                        player.sendMessage("§cDieser Clan existiert nicht!");
//                        return true;
//                    }
//
//                    Clan clan = new Clan(clanName);
//
//                    player.sendMessage("§7--- §eClan Mitglieder §7---");
//                    for (UUID memberUUID : clan.getPlayers()) {
//                        Player member = Bukkit.getPlayer(memberUUID);
//                        String memberName = member != null ? member.getName() : "Offline";
//                        if (clan.isPlayerAdmin(memberUUID)) {
//                            player.sendMessage("§c" + memberName);
//                        } else {
//                            player.sendMessage("§f" + memberName);
//                        }
//                    }
//                    player.sendMessage("§7Admins: §c" + clan.getAdmins().stream()
//                            .map(uuid -> {
//                                Player p = Bukkit.getPlayer(uuid);
//                                return p != null ? p.getName() : "Offline";
//                            }).collect(Collectors.joining(", ")));
//
//                } else {
//                    player.sendMessage("§cBenutzung: /clan get [ClanName]");
//                }
//            }
//            default -> sendUsage(sender);
//        }

        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player)) return List.of();
//        LuckPerms lp = LuckPermsProvider.get();
//        if (args.length == 1) {
//            List<String> subcommands = List.of("create", "delete", "invite", "accept", "decline", "get", "removeinvite", "join", "settings");
//            return subcommands.stream()
//                    .filter(cmd -> cmd.toLowerCase().startsWith(args[0].toLowerCase()))
//                    .toList();
//        }
//        if (args.length == 2) {
//            switch (args[0].toLowerCase()) {
//                case "create":
//                    return List.of("<Name>");
//                case "delete":
//                case "get":
//                case "invite":
//                case "removeinvite":
//                case "join":
//                    lp.getGroupManager().getLoadedGroups().stream()
//                            .filter(g -> g.getName().startsWith("clan_"))
//                            .map(g -> g.getName().substring(5))
//                            .filter(name -> name.toLowerCase().startsWith(args[1].toLowerCase()))
//                            .toList();
//            }
//        }
//        if (args.length == 3) {
//            switch (args[0].toLowerCase()) {
//                case "create":
//                    return List.of("BLACK","DARK_BLUE","DARK_GREEN","DARK_AQUA","DARK_RED","DARK_PURPLE","GOLD",
//                                    "GRAY","DARK_GRAY","BLUE","GREEN","AQUA","RED","LIGHT_PURPLE","YELLOW","WHITE").stream()
//                            .filter(color -> color.toLowerCase().startsWith(args[2].toLowerCase()))
//                            .toList();
//                case "invite":
//                case "removeinvite":
//                    return Bukkit.getOnlinePlayers().stream()
//                            .map(Player::getName)
//                            .filter(name -> name.toLowerCase().startsWith(args[2].toLowerCase()))
//                            .toList();
//            }
//        }
        return List.of("§cCurrently not working... Please try it again later");
    }

    public static void sendUsage(CommandSender sender) {
        sender.sendMessage("§7--- §eClan Commands §7---");
        sender.sendMessage("§e/clan create <Name> <Farbe> §7- Erstellt einen neuen Clan");
        sender.sendMessage("§e/clan delete <Name> §7- Löscht einen Clan (nur Leader/Admin)");
        sender.sendMessage("§e/clan invite <Clan> <Spieler> §7- Lädt einen Spieler in einen Clan ein");
        sender.sendMessage("§e/clan accept <Clan> §7- Nimmt eine Clan-Einladung an");
        sender.sendMessage("§e/clan decline <Clan> §7- Lehnt eine Clan-Einladung ab");
        sender.sendMessage("§e/clan removeinvite <Clan> <Spieler> §7- Entfernt eine Einladung (nur Leader/Admin)");
        sender.sendMessage("§e/clan get [Clan] §7- Zeigt Infos über einen Clan oder deinen eigenen");
        sender.sendMessage("§e/clan join <Clan> §7- Tritt einem Clan bei, wenn du eingeladen wurdest");
        sender.sendMessage("§e/clan settings §7- Zeigt Clan-Einstellungen (coming soon)");
    }

    public static void register() {
        Main.getInstance().getCommand("clan").setExecutor(new ClanCommand());
        Main.getInstance().getCommand("clan").setTabCompleter(new ClanCommand());
    }
}