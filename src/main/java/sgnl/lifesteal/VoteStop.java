package sgnl.lifesteal;

import java.util.HashSet;
import java.util.Objects;
import java.util.UUID;

public class VoteStop {
  public static int requirement = 0;
  public static HashSet<String> voted = new HashSet<>();

  public static void check() {
    voted.removeIf(uuid -> !Objects.requireNonNull(Config.getPlayer(UUID.fromString(uuid))).online);
    requirement = (int) Math.ceil((double) Lifesteal.server.getPlayerList().getPlayerCount() / 2);
    if (requirement == voted.size() && requirement > 0) Lifesteal.server.halt(false);
  }
}
