package navy.otter.dailyreward;

import java.io.File;
import java.sql.SQLException;
import java.util.Map;
import java.util.UUID;
import navy.otter.dailyreward.command.DailyRewardCommand;
import navy.otter.dailyreward.configuration.Configuration;
import navy.otter.dailyreward.database.DbController;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class DailyRewardPlugin extends JavaPlugin {

  static DailyRewardPlugin instance;
  static Configuration configuration;
  static Map<UUID,Long> playerMap;

  @Override
  public void onEnable() {
    instance = this;
    configuration = new Configuration(this);
    try {
      DbController.createTableIfNotExists();
      playerMap = DbController.getPersistedData();
    } catch (SQLException e) {
      e.printStackTrace();
      Bukkit.getPluginManager().disablePlugin(this);
      return;
    }
    this.getCommand("vote").setExecutor(new DailyRewardCommand());

  }

  @Override
  public void onDisable() {
    DbController.finalCloseDbConnection();
  }

  public static DailyRewardPlugin getInstance() {
    return instance;
  }

  public static Configuration getConfiguration() {
    return configuration;
  }

  public static Map<UUID,Long> getPlayerMap() {
    return playerMap;
  }

  public static void setPlayerMap(Map<UUID,Long> playerMap) {
    DailyRewardPlugin.playerMap = playerMap;
  }
}
