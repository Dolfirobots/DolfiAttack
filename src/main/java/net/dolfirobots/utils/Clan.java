package net.dolfirobots.utils;

import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.model.group.Group;
import net.luckperms.api.model.user.User;
import net.luckperms.api.node.Node;
import net.luckperms.api.node.types.InheritanceNode;
import net.luckperms.api.node.types.MetaNode;
import net.luckperms.api.node.types.PrefixNode;
import org.bukkit.ChatColor;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class Clan {

    private final String name;
    private ChatColor color;
    private final String groupName;

    private final LuckPerms lp = LuckPermsProvider.get();

    public Clan(String name) {
        this.name = name;
        this.groupName = "clan_" + name;
    }

    public String getClanName() {
        return name;
    }

    public ChatColor getClanColor() {
        return color;
    }

    public void setClanColor(ChatColor color) {
        this.color = color;
        savePrefix();
    }

    public String getPrefix() {
        return "§8[" + color + name + "§8] §7";
    }

    public String getLuckPermsName() {
        return groupName;
    }

    public List<UUID> getPlayers() {
        return lp.getUserManager().getLoadedUsers().stream()
                .filter(user -> user.getNodes().stream()
                        .anyMatch(node -> node instanceof InheritanceNode inheritanceNode &&
                                inheritanceNode.getGroupName().equals(groupName)))
                .map(User::getUniqueId)
                .collect(Collectors.toList());
    }

    public void addPlayer(UUID player) {
        User user = lp.getUserManager().getUser(player);
        if (user != null) {
            Node node = InheritanceNode.builder(groupName).build();
            user.data().add(node);
            lp.getUserManager().saveUser(user);
        }
    }

    // Entfernt Spieler
    public void removePlayer(UUID player) {
        User user = lp.getUserManager().getUser(player);
        if (user != null) {

        }
    }

    // Ist der Spieler admin
    // Wird in der luckperms group gespeichert mit der permission
    // clan.admin.UUID
    public boolean isPlayerAdmin(UUID player) {
        User user = lp.getUserManager().getUser(player);
        if (user == null) return false;

        return false;
    }

    public void setAdmin(UUID player, boolean admin) {
        User user = lp.getUserManager().getUser(player);
        if (user == null) return;

        Node leaderNode = MetaNode.builder("role", "leader").build();
        if (admin) {
            user.data().add(leaderNode);
        } else {
            user.data().remove(leaderNode);
        }
        lp.getUserManager().saveUser(user);
    }

    public List<UUID> getAdmins() {
        return getPlayers().stream().filter(this::isPlayerAdmin).collect(Collectors.toList());
    }

    /*** Prefix ***/
    private void savePrefix() {
        Group group = lp.getGroupManager().getGroup(groupName);
        if (group != null) {
            group.data().clear(PrefixNode.class::isInstance);
            group.data().add(PrefixNode.builder(getPrefix(), 100).build());
            lp.getGroupManager().saveGroup(group);
        }
    }

    /*** Clan erstellen ***/
    public void createGroup() {
        if (lp.getGroupManager().getGroup(groupName) != null) return;

    }

    // Komplett löschen
    public void deleteClan() {
        Group group = lp.getGroupManager().getGroup(groupName);
        if (group == null) return;

        lp.getGroupManager().deleteGroup(group).join();
    }


    /*** Clan laden ***/
    public void loadClan() {
        Group group = lp.getGroupManager().getGroup(groupName);
        if (group != null) {

        } else {
            this.color = ChatColor.WHITE;
        }
    }

    public static boolean exists(String clanName) {
        LuckPerms lp = LuckPermsProvider.get();
        String groupName = "clan_" + clanName;
        return lp.getGroupManager().getGroup(groupName) != null;
    }
}

