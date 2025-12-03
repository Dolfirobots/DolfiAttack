package net.dolfirobots.utils;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import net.dolfirobots.chat.Messanger;

import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MojangWebAPI {

    private static final String SERVICE = "MojangWebAPI";
    private static final Gson GSON = new Gson();
    private static final ExecutorService EXECUTOR = Executors.newFixedThreadPool(10);

    public static CompletableFuture<Optional<UUID>> getUUIDAsync(String playerName) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                URL url = new URL("https://api.mojang.com/users/profiles/minecraft/" + playerName);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setConnectTimeout(5000);
                connection.setReadTimeout(5000);

                if (connection.getResponseCode() != 200) {
                    return Optional.empty();
                }

                try (InputStreamReader reader = new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8)) {
                    JsonObject json = GSON.fromJson(reader, JsonObject.class);
                    if (!json.has("id")) return Optional.empty();

                    UUID uuid = UUID.fromString(json.get("id").getAsString().replaceFirst(
                            "(\\w{8})(\\w{4})(\\w{4})(\\w{4})(\\w{12})",
                            "$1-$2-$3-$4-$5"
                    ));
                    return Optional.of(uuid);
                }
            } catch (Exception e) {
                Messanger.sendException(SERVICE, "getUUIDAsync for username §e" + playerName, e);
                return Optional.empty();
            }
        }, EXECUTOR);
    }

    public static CompletableFuture<Optional<String>> getUsername(UUID uuid) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                URL url = new URL("https://sessionserver.mojang.com/session/minecraft/profile/" + uuid.toString().replace("-", ""));
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setConnectTimeout(5000);
                connection.setReadTimeout(5000);

                if (connection.getResponseCode() != 200) {
                    return Optional.empty();
                }

                try (InputStreamReader reader = new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8)) {
                    JsonObject json = GSON.fromJson(reader, JsonObject.class);
                    if (!json.has("name")) return Optional.empty();
                    return Optional.of(json.get("name").getAsString());
                }

            } catch (Exception e) {
                Messanger.sendException(SERVICE, "getUsername for uuid §e" + uuid, e);
                return Optional.empty();
            }
        }, EXECUTOR);
    }

    public static void shutdownExecutor() {
        EXECUTOR.shutdown();
    }
}
