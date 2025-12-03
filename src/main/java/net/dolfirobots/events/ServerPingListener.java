package net.dolfirobots.events;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.WrappedGameProfile;
import com.comphenix.protocol.wrappers.WrappedServerPing;
import net.dolfirobots.Main;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.ServerListPingEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.regex.Pattern;

public class ServerPingListener implements Listener {
    public static void register() {
        ProtocolLibrary.getProtocolManager().addPacketListener(
                new PacketAdapter(PacketAdapter.params(Main.getInstance(), PacketType.Status.Server.SERVER_INFO).optionAsync()) {
                    @Override
                    public void onPacketSending(PacketEvent event) {
                        WrappedServerPing ping = event.getPacket().getServerPings().read(0);

                        if (Main.isMaintenanceMode()) {
                            ping.setVersionName(Color.apply("§cMaintenance"));
                            ping.setVersionProtocol(-1);

                            ping.setPlayersOnline(0);

                            List<WrappedGameProfile> profiles = new ArrayList<>();
                            profiles.add(new WrappedGameProfile(UUID.randomUUID(), Color.apply("§7Currently in Maintenance")));
                            profiles.add(new WrappedGameProfile(UUID.randomUUID(), Color.apply("§7Try it later again!")));
                            ping.setPlayers(profiles);

                            event.getPacket().getServerPings().write(0, ping);
                        }
                    }
                });
    }

    public static class Color {
        private Color() {}

        public static final char COLOR_CHAR = '\u00A7';
        public static final char REPLACEMENT_CHAR = '\u0026';
        public static final String ALL_CODES = "0123456789AaBbCcDdEefKkLlMmNnOoRrXx";
        public static final Pattern STRIP_COLOR_PATTERN = Pattern.compile("(?i)" + String.valueOf(COLOR_CHAR) + "[0-9A-FK-ORX]");
        public static final Pattern STRIP_UNCOLORED_PATTERN = Pattern.compile("(?i)" + String.valueOf(REPLACEMENT_CHAR) + "[0-9A-FK-ORX]");

        public static String strip(String text) {
            return text == null ? null : STRIP_COLOR_PATTERN.matcher(text).replaceAll("");
        }

        public static String stripPlain(String text) {
            return text == null ? null : STRIP_UNCOLORED_PATTERN.matcher(text).replaceAll("");
        }

        public static String apply(String text) {
            char[] chars = text.toCharArray();
            for (int i = 0; i < chars.length - 1; ++i) {
                if (chars[i] == REPLACEMENT_CHAR && ALL_CODES.indexOf(chars[i + 1]) > -1) {
                    chars[i] = COLOR_CHAR;
                    chars[i + 1] = Character.toLowerCase(chars[i + 1]);
                }
            }
            return new String(chars);
        }

        public static String unapply(String text) {
            char[] chars = text.toCharArray();
            for (int i = 0; i < chars.length - 1; ++i) {
                if (chars[i] == COLOR_CHAR && ALL_CODES.indexOf(chars[i + 1]) > -1) {
                    chars[i] = REPLACEMENT_CHAR;
                    chars[i + 1] = Character.toLowerCase(chars[i + 1]);
                }
            }
            return new String(chars);
        }
    }
}
