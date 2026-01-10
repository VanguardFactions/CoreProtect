package net.coreprotect.api;

import net.coreprotect.config.Config;
import net.coreprotect.config.ConfigHandler;
import net.coreprotect.database.Database;
import net.coreprotect.database.statement.UserStatement;
import net.coreprotect.utility.ItemUtils;
import net.coreprotect.utility.StringUtils;
import net.coreprotect.utility.WorldUtils;
import org.bukkit.block.Block;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class ChestAPI {

    public static List<String[]> performLookup(Block block, int offset) {
        List<String[]> result = new ArrayList<>();

        if (!Config.getGlobal().API_ENABLED) {
            return result;
        }

        if (block == null) {
            return result;
        }

        try (Connection connection = Database.getConnection(false, 1000)) {
            if (connection == null) {
                return result;
            }

            int x = block.getX();
            int y = block.getY();
            int z = block.getZ();
            int time = (int) (System.currentTimeMillis() / 1000L);
            int worldId = WorldUtils.getWorldId(block.getWorld().getName());
            int checkTime = 0;

            if (offset > 0) {
                checkTime = time - offset;
            }

            try (Statement statement = connection.createStatement()) {
                String query = "SELECT time,user,action,type,data,amount,metadata,rolled_back FROM " + ConfigHandler.prefix + "container " + WorldUtils.getWidIndex("container") + "WHERE wid = '" + worldId + "' AND (x = '" + block.getX() + "') AND (z = '" + block.getZ() + "') AND y = '" + y + "' AND time > '" + checkTime + "' ORDER BY rowid DESC";

                try (ResultSet results = statement.executeQuery(query)) {
                    while (results.next()) {
                        int resultUserId = results.getInt("user");
                        int resultAction = results.getInt("action");
                        int resultType = results.getInt("type");
                        int resultData = results.getInt("data");
                        long resultTime = results.getLong("time");
                        int resultAmount = results.getInt("amount");
                        int resultRolledBack = results.getInt("rolled_back");
                        byte[] resultMetadata = results.getBytes("metadata");
                        String tooltip = ItemUtils.getEnchantments(resultMetadata, resultType, resultAmount);

                        if (ConfigHandler.playerIdCacheReversed.get(resultUserId) == null) {
                            UserStatement.loadName(connection, resultUserId);
                        }

                        tooltip = resultAmount + "\n" + tooltip;

                        String resultUser = ConfigHandler.playerIdCacheReversed.get(resultUserId);

                        String[] lookupData = new String[] {String.valueOf(resultTime), resultUser, String.valueOf(x), String.valueOf(y), String.valueOf(z), String.valueOf(resultType), String.valueOf(resultData), String.valueOf(resultAction), String.valueOf(resultRolledBack), String.valueOf(worldId), tooltip };

                        result.add(StringUtils.toStringArray(lookupData));
                    }
                }
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }
}
