package net.dolfirobots.events;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.WrappedGameProfile;
import com.comphenix.protocol.wrappers.WrappedServerPing;
import net.dolfirobots.Main;
import org.bukkit.event.Listener;

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
}
