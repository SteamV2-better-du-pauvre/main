package loaders;

import config.DatabaseConfig;
import connection.DatabaseConnection;
import generators.DLCGenerator;
import generators.GameGenerator;
import generators.PatchGenerator;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

/**
 * Loader for platform_db database.
 * Syncs published data from editor_db to platform_db (simulating Kafka sync).
 */
public class PlatformDbLoader {
    private final DatabaseConfig config;
    private final Connection editorConnection;
    private final Connection platformConnection;

    public PlatformDbLoader(DatabaseConnection dbConnection) {
        this.config = DatabaseConfig.getInstance();
        this.editorConnection = dbConnection.getEditorDbConnection();
        this.platformConnection = dbConnection.getPlatformDbConnection();
    }

    /**
     * Load published data from editor_db into platform_db
     */
    public void loadData(GameGenerator gameGenerator, DLCGenerator dlcGenerator, 
                        PatchGenerator patchGenerator) throws SQLException {
        if (config.isVerbose()) {
            System.out.println("==================================================");
            System.out.println("Syncing published data to platform_db...");
            System.out.println("==================================================\n");
        }

        try {
            // Step 1: Sync editors (copy all editors)
            syncEditors();

            // Step 2: Sync published games only
            syncPublishedGames(gameGenerator);

            // Step 3: Sync published DLCs only
            syncPublishedDLCs(dlcGenerator);

            // Step 4: Sync published patches only (without is_publish field)
            syncPublishedPatches(patchGenerator);

            if (config.isVerbose()) {
                System.out.println("==================================================");
                System.out.println("platform_db synced successfully!");
                System.out.println("==================================================\n");
            }

        } catch (SQLException e) {
            System.err.println("Error syncing data to platform_db: " + e.getMessage());
            throw e;
        }
    }

    /**
     * Sync all editors from editor_db to platform_db
     */
    private void syncEditors() throws SQLException {
        if (config.isVerbose()) {
            System.out.println("Syncing editors to platform_db...");
        }

        String selectSql = "SELECT id, name, password, type, description FROM editor";
        String insertSql = "INSERT INTO editor (id, name, password, type, description) VALUES (?, ?, ?, ?::type_editor_enum, ?)";

        int count = 0;
        try (PreparedStatement selectStmt = editorConnection.prepareStatement(selectSql);
             PreparedStatement insertStmt = platformConnection.prepareStatement(insertSql);
             ResultSet rs = selectStmt.executeQuery()) {

            while (rs.next()) {
                insertStmt.setObject(1, rs.getObject("id"));
                insertStmt.setString(2, rs.getString("name"));
                insertStmt.setString(3, rs.getString("password"));
                insertStmt.setString(4, rs.getString("type"));
                insertStmt.setString(5, rs.getString("description"));
                insertStmt.executeUpdate();
                count++;
            }
        }

        if (config.isVerbose()) {
            System.out.println("Synced " + count + " editors\n");
        }
    }

    /**
     * Sync only published games from editor_db to platform_db
     */
    private void syncPublishedGames(GameGenerator gameGenerator) throws SQLException {
        if (config.isVerbose()) {
            System.out.println("Syncing published games to platform_db...");
        }

        // Sync games
        String selectGameSql = "SELECT id, editor_id, name, price, num_version FROM game WHERE is_publish = true";
        String insertGameSql = "INSERT INTO game (id, editor_id, name, price, num_version) VALUES (?, ?, ?, ?, ?)";

        int gameCount = 0;
        try (PreparedStatement selectStmt = editorConnection.prepareStatement(selectGameSql);
             PreparedStatement insertStmt = platformConnection.prepareStatement(insertGameSql);
             ResultSet rs = selectStmt.executeQuery()) {

            while (rs.next()) {
                UUID gameId = (UUID) rs.getObject("id");
                
                insertStmt.setObject(1, gameId);
                insertStmt.setObject(2, rs.getObject("editor_id"));
                insertStmt.setString(3, rs.getString("name"));
                insertStmt.setDouble(4, rs.getDouble("price"));
                insertStmt.setDouble(5, rs.getDouble("num_version"));
                insertStmt.executeUpdate();
                gameCount++;

                // Sync platforms and genres for this game
                syncGamePlatforms(gameId);
                syncGameGenres(gameId);
            }
        }

        if (config.isVerbose()) {
            System.out.println("Synced " + gameCount + " published games\n");
        }
    }

    /**
     * Sync game platforms
     */
    private void syncGamePlatforms(UUID gameId) throws SQLException {
        String selectSql = "SELECT platform FROM game_platforms WHERE game_id = ?";
        String insertSql = "INSERT INTO game_platforms (game_id, platform) VALUES (?, ?::platform_enum)";

        try (PreparedStatement selectStmt = editorConnection.prepareStatement(selectSql);
             PreparedStatement insertStmt = platformConnection.prepareStatement(insertSql)) {
            
            selectStmt.setObject(1, gameId);
            try (ResultSet rs = selectStmt.executeQuery()) {
                while (rs.next()) {
                    insertStmt.setObject(1, gameId);
                    insertStmt.setString(2, rs.getString("platform"));
                    insertStmt.executeUpdate();
                }
            }
        }
    }

    /**
     * Sync game genres
     */
    private void syncGameGenres(UUID gameId) throws SQLException {
        String selectSql = "SELECT genre FROM game_genres WHERE game_id = ?";
        String insertSql = "INSERT INTO game_genres (game_id, genre) VALUES (?, ?::genre_enum)";

        try (PreparedStatement selectStmt = editorConnection.prepareStatement(selectSql);
             PreparedStatement insertStmt = platformConnection.prepareStatement(insertSql)) {
            
            selectStmt.setObject(1, gameId);
            try (ResultSet rs = selectStmt.executeQuery()) {
                while (rs.next()) {
                    insertStmt.setObject(1, gameId);
                    insertStmt.setString(2, rs.getString("genre"));
                    insertStmt.executeUpdate();
                }
            }
        }
    }

    /**
     * Sync only published DLCs from editor_db to platform_db
     * Only syncs DLCs whose parent game is also published
     */
    private void syncPublishedDLCs(DLCGenerator dlcGenerator) throws SQLException {
        if (config.isVerbose()) {
            System.out.println("Syncing published DLCs to platform_db...");
        }

        // Join with game table to ensure parent game is published
        String selectSql = "SELECT d.id, d.game_id, d.editor_id, d.name, d.price, d.num_version " +
                          "FROM dlc d " +
                          "INNER JOIN game g ON d.game_id = g.id " +
                          "WHERE d.is_publish = true AND g.is_publish = true";
        String insertSql = "INSERT INTO dlc (id, game_id, editor_id, name, price, num_version) VALUES (?, ?, ?, ?, ?, ?)";

        int count = 0;
        try (PreparedStatement selectStmt = editorConnection.prepareStatement(selectSql);
             PreparedStatement insertStmt = platformConnection.prepareStatement(insertSql);
             ResultSet rs = selectStmt.executeQuery()) {

            while (rs.next()) {
                insertStmt.setObject(1, rs.getObject("id"));
                insertStmt.setObject(2, rs.getObject("game_id"));
                insertStmt.setObject(3, rs.getObject("editor_id"));
                insertStmt.setString(4, rs.getString("name"));
                insertStmt.setDouble(5, rs.getDouble("price"));
                insertStmt.setDouble(6, rs.getDouble("num_version"));
                insertStmt.executeUpdate();
                count++;
            }
        }

        if (config.isVerbose()) {
            System.out.println("Synced " + count + " published DLCs\n");
        }
    }

    /**
     * Sync only published patches from editor_db to platform_db
     * Only syncs patches whose parent game is also published
     * Note: platform_db doesn't have is_publish field
     */
    private void syncPublishedPatches(PatchGenerator patchGenerator) throws SQLException {
        if (config.isVerbose()) {
            System.out.println("Syncing published patches to platform_db...");
        }

        // Join with game table to ensure parent game is published
        // Note: platform_db has 'description' instead of 'modifications', and no 'is_publish' or 'comment'
        String selectSql = "SELECT p.id, p.is_patch_of_game, p.game_id, p.platform, p.old_version, p.new_version, p.modifications " +
                          "FROM patch p " +
                          "INNER JOIN game g ON p.game_id = g.id " +
                          "WHERE p.is_publish = true AND g.is_publish = true";
        String insertSql = "INSERT INTO patch (id, is_patch_of_game, game_id, platform, old_version, new_version, description) " +
                          "VALUES (?, ?, ?, ?::platform_enum, ?, ?, ?)";

        int count = 0;
        try (PreparedStatement selectStmt = editorConnection.prepareStatement(selectSql);
             PreparedStatement insertStmt = platformConnection.prepareStatement(insertSql);
             ResultSet rs = selectStmt.executeQuery()) {

            while (rs.next()) {
                insertStmt.setObject(1, rs.getObject("id"));
                insertStmt.setBoolean(2, rs.getBoolean("is_patch_of_game"));
                insertStmt.setObject(3, rs.getObject("game_id"));
                insertStmt.setString(4, rs.getString("platform"));
                insertStmt.setDouble(5, rs.getDouble("old_version"));
                insertStmt.setDouble(6, rs.getDouble("new_version"));
                insertStmt.setString(7, rs.getString("modifications")); // Map to description
                insertStmt.executeUpdate();
                count++;
            }
        }

        if (config.isVerbose()) {
            System.out.println("Synced " + count + " published patches\n");
        }
    }
}
