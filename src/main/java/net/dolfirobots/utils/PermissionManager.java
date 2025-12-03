package net.dolfirobots.utils;

import org.bukkit.entity.Player;

public enum PermissionManager {

    COMMAND_WHITELIST("command.whitelist"),
    COMMAND_MAINTENANCE("command.maintenance"),
    COMMAND_BLOCK_BYPASS("command.block_bypass"),
    COMMAND_BLOCK_BYPASS_COREPROTECT("command.block_bypass.coreprotect"),
    COMMAND_SET_SPAWN("command.set_spawn"),
    COMMAND_SPEED("command.speed");

    private final String permission;

    PermissionManager(String permission) {
        this.permission = "dolfiattack." + permission;
    }
    public String toString() {
        return permission;
    }
    public boolean checkPlayer(Player player) {
        return player.hasPermission(permission);
    }
}