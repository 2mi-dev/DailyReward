package navy.otter.dailyreward.configuration;

import navy.otter.dailyreward.DailyRewardPlugin;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.jetbrains.annotations.NotNull;

public class Configuration {

  // Config keys
  private static class Key {

    private static final String MSG_PREFIX = "msg-prefix";
    private static final String REWARD_FAIL_MSG = "reward-fail-msg";
    private static final String REWARD_SUCCESS_MSG = "reward-success-msg";
    private static final String INVENTORY_FULL_MSG = "inventory-full-msg";
    private static final String REWARD_AMOUNT = "reward-amount";

  }

  private static String msgPrefix;
  private static String rewardFailMsg;
  private static String rewardSuccessMsg;
  private static String inventoryFullMsg;
  private static int rewardAmount;

  public Configuration(@NotNull DailyRewardPlugin plugin) {
    FileConfiguration config = plugin.getConfig();
    config.options().copyDefaults(true);
    plugin.saveConfig();

    String prefixRaw = config.getString(Key.MSG_PREFIX);
    if(prefixRaw != null) {
      msgPrefix = ChatColor.translateAlternateColorCodes('&', prefixRaw);
    } else {
      msgPrefix = "";
    }
    rewardFailMsg = msgPrefix + config.getString(Key.REWARD_FAIL_MSG);
    rewardSuccessMsg = msgPrefix + config.getString(Key.REWARD_SUCCESS_MSG);
    inventoryFullMsg = msgPrefix + config.getString(Key.INVENTORY_FULL_MSG);
    rewardAmount = config.getInt(Key.REWARD_AMOUNT);
  }

  public static String getRewardSuccessMsg() {
    return rewardSuccessMsg;
  }

  public static String getInventoryFullMsg() {
    return inventoryFullMsg;
  }

  public static int getRewardAmount() {
    return rewardAmount;
  }

  public static String getRewardFailMsg() {
    return rewardFailMsg;
  }
}
