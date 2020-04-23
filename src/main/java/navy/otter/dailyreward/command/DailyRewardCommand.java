package navy.otter.dailyreward.command;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import navy.otter.dailyreward.DailyRewardPlugin;
import navy.otter.dailyreward.configuration.Configuration;
import navy.otter.dailyreward.database.DbController;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

public class DailyRewardCommand implements CommandExecutor {

  Map<UUID,Long> playerMap = DailyRewardPlugin.getPlayerMap();

  @Override
  public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command,
      @NotNull String s, @NotNull String[] strings) {
    if (!(commandSender instanceof Player)) {
      return false;
    }

    Player player = (Player) commandSender;

    if(!player.hasPermission("dailyreward.vote")) {
      return false;
    }

    long currentTime = System.currentTimeMillis();
    long lastPlayerVoteTime = 0;
    if(playerMap.containsKey(player.getUniqueId())) {
      lastPlayerVoteTime = playerMap.get(player.getUniqueId());
    }

    Calendar currentCal = Calendar.getInstance();
    Calendar playerCal = Calendar.getInstance();
    currentCal.setTime(new Date(currentTime));
    playerCal.setTime(new Date(lastPlayerVoteTime));

    boolean isNextDay = currentCal.get(Calendar.DAY_OF_YEAR) > playerCal.get(Calendar.DAY_OF_YEAR)
        || currentCal.get(Calendar.YEAR) > playerCal.get(Calendar.YEAR);

    if(!isNextDay) {
      player.sendMessage(Configuration.getRewardFailMsg());
      return false;
    }

    if (player.getInventory().firstEmpty() == -1) {
      player.sendMessage(Configuration.getInventoryFullMsg());
      return false;
    }

    ItemStack reward = createReward();

    player.getInventory().addItem(reward);
    playerMap.put(player.getUniqueId(), currentTime);
    player.sendMessage(Configuration.getRewardSuccessMsg());
    Runnable r = () -> {
      try {
        DbController.persistPlayerVote(player, currentTime);
      } catch (SQLException e) {
        e.printStackTrace();
      }
    };
    Bukkit.getScheduler().runTaskAsynchronously(DailyRewardPlugin.getInstance(), r);

    return true;
  }

  public ItemStack createReward() {
    ItemStack reward = new ItemStack(Material.EMERALD, Configuration.getRewardAmount());
    String itemName = ChatColor.translateAlternateColorCodes('&', "&r&aKiwi");
    List<String> lore = new ArrayList<>();
    lore.add(ChatColor.translateAlternateColorCodes('&', "&r&9Vote-Belohnung"));
    reward.addUnsafeEnchantment(Enchantment.DURABILITY, 10);
    ItemMeta rewardMeta = reward.getItemMeta();
    rewardMeta.setDisplayName(itemName);
    rewardMeta.setLore(lore);
    rewardMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
    reward.setItemMeta(rewardMeta);
    return reward;
  }
}
