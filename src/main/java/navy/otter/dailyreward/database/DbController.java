package navy.otter.dailyreward.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Logger;
import navy.otter.dailyreward.DailyRewardPlugin;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class DbController {

  static DailyRewardPlugin plugin = DailyRewardPlugin.getInstance();
  private static DbController dbController = new DbController();
  private static Connection connection;
  static Logger log = plugin.getLogger();
  private static final String DB_PATH = "./plugins/DailyReward/sqlite.db";

  static {
    try {
      Class.forName("org.sqlite.JDBC");
    } catch (ClassNotFoundException e) {
      plugin.getLogger().severe("Fehler beim Laden des JDBC-Treibers");
      Bukkit.getPluginManager().disablePlugin(plugin);
      e.printStackTrace();
    }
  }

  private DbController() {
  }

  public static void initDbConnection() throws SQLException {
    try {
      if (connection != null) {
        return;
      }
      connection = DriverManager.getConnection("jdbc:sqlite:" + DB_PATH);
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  public static void closeDbConnection() {
    connection = null;
  }

  public static void createTableIfNotExists() throws SQLException {
    initDbConnection();
    Statement stmt = connection.createStatement();
    stmt.executeUpdate("CREATE TABLE IF NOT EXISTS player_votes ("
        + " 'playerUUID' varchar(40) PRIMARY KEY, 'playerName' varchar(40), voteTime bigint);");
    closeDbConnection();
  }

  public static void resetTable() throws SQLException {
    initDbConnection();
    Statement stmt = connection.createStatement();
    stmt.executeUpdate("DROP TABLE player_votes");
    closeDbConnection();
    createTableIfNotExists();
  }

  public static Map<UUID,Long> getPersistedData() throws SQLException {
    HashMap<UUID, Long> playerStatus = new HashMap<>();
    initDbConnection();
    Statement stmt = connection.createStatement();
    ResultSet rs = stmt.executeQuery("SELECT * FROM player_votes");

    while(rs.next()) {
      String uuidString = rs.getString(1);
      long voteTime = rs.getLong(3);

      UUID uuid = UUID.fromString(uuidString);

      playerStatus.put(uuid, voteTime);
    }
    closeDbConnection();
    return playerStatus;
  }

  public static void persistPlayerVote(Player player, long timestamp) throws SQLException {
    String playerUuid = player.getUniqueId().toString();
    String playerName = player.getName();

    initDbConnection();
    PreparedStatement stmt = connection.prepareStatement("REPLACE INTO player_votes"
        + " VALUES (?, ?, ?);");
    stmt.setString(1, playerUuid);
    stmt.setString(2, playerName);
    stmt.setLong(3, timestamp);

    stmt.execute();
    closeDbConnection();
  }

  public static void finalCloseDbConnection() {
    try {
      if (connection != null && !connection.isClosed()) {
        connection.close();
        if (connection.isClosed()) {
          log.info("Connection to Database closed");
        }
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }
}
