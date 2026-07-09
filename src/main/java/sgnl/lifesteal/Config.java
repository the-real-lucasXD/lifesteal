package sgnl.lifesteal;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.minecraft.server.level.ServerPlayer;
import org.jspecify.annotations.Nullable;
import sgnl.lifesteal.player.Player;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.UUID;

public class Config {
  public static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
  public static Config INSTANCE = new Config();
  public static Path path;
  
  public static void init(Path configDir) throws Exception {
    path = configDir.resolve("lifesteal-config.json");
    load();
  }
  
  public static void load() throws Exception {
    try {
      if (Files.exists(path)) {
        String json = Files.readString(path);
        INSTANCE = GSON.fromJson(json, Config.class);
      } else {
        save();
      }
    } catch (Exception e) {
      throw new Exception(e);
    }
  }
  
  public static void save() throws Exception {
    try {
      Files.createDirectories(path.getParent());
      Files.writeString(path, GSON.toJson(INSTANCE));
    } catch (Exception e) {
      throw new Exception(e);
    }
  }
  
  public int maxHealth = 20;
  public static int maxHealth() { return INSTANCE.maxHealth; }
  
  public int minHealth = 5;
  public static int minHealth() { return INSTANCE.minHealth; }
  
  public int combatDuration = 1200;
  public int enderPearlsPerCombat = 32;

  public HashMap<String, Player> players = new HashMap<>();

  public static @Nullable Player getPlayer(UUID uuid) {
    return INSTANCE.players.get(uuid.toString());
  }
  
  public static Player getPlayer(ServerPlayer player) {
    if (!INSTANCE.players.containsKey(player.getUUID().toString()))
      INSTANCE.players.put(player.getStringUUID(), new Player(player.getStringUUID()));
    return getPlayer(player.getUUID());
  }

  public static String getVariable(String variable) {
    try (InputStream is = Config.class.getClassLoader().getResourceAsStream("variables/" + variable + ".txt")) {
      if (is == null) return null;
      try (BufferedReader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {
        return reader.readLine();
      }
    } catch (Exception e) {
      Lifesteal.LOGGER.error("An error occurred while reading variable: ", e);
      return null;
    }
  }
}
