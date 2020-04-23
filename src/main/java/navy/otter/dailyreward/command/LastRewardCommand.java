package navy.otter.dailyreward.command;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import java.util.TimeZone;
import java.util.UUID;
import navy.otter.dailyreward.DailyRewardPlugin;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class LastRewardCommand implements CommandExecutor {

  Map<UUID,Long> playerMap = DailyRewardPlugin.getPlayerMap();

  @Override
  public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command,
      @NotNull String s, @NotNull String[] strings) {
    if (!(commandSender instanceof Player)) {
      return false;
    }

    Player player = (Player) commandSender;

    Iterator<String> arg = Arrays.asList(strings).iterator();
    String option = arg.hasNext() ? arg.next() : "";

    if(option == null || option.equals("")) {
      return false;
    }

    if (player.hasPermission("dailyreward.lastvote")) {
      String lastVote = getLastVote(option);
      player.sendMessage(ChatColor.translateAlternateColorCodes('&',
          "&7Last vote of " + option + ": " + lastVote));
      return true;
    }
    return false;
  }

  public String getLastVote(String playerName) {
    Player targetPlayer = Bukkit.getPlayer(playerName);
    if(targetPlayer == null) {
      return "Player unknown.";
    }

    long lastPlayerVote = 0;

    if(!playerMap.containsKey(targetPlayer.getUniqueId())) {
      return "No vote registered.";
    } else {
      lastPlayerVote = playerMap.get(targetPlayer.getUniqueId());
    }

    DateFormat df = new SimpleDateFormat("dd-MM-yyyy HH:mm");

    return df.format(new Date(lastPlayerVote));
  }
}
