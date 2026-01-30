package connection;

import config.DatabaseConfig;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Manages database connections and provides utility methods for database operations.
 */
public class DatabaseConnection {
    private final DatabaseConfig config;
    private Connection editorDbConnection;
    private Connection platformDbConnection;

    public DatabaseConnection() {
        this.config = DatabaseConfig.getInstance();
    }

    /**
     * Connect to both databases
     */
    public void connect() throws SQLException {
        try {
            // Load PostgreSQL driver
            Class.forName("org.postgresql.Driver");
            
            if (config.isVerbose()) {
                System.out.println("Connecting to databases...");
            }

            // Connect to editor_db
            editorDbConnection = DriverManager.getConnection(
                config.getDbUrl() + "editor_db",
                config.getDbUser(),
                config.getDbPassword()
            );
            editorDbConnection.setAutoCommit(false);

            // Connect to platform_db
            platformDbConnection = DriverManager.getConnection(
                config.getDbUrl() + "platform_db",
                config.getDbUser(),
                config.getDbPassword()
            );
            platformDbConnection.setAutoCommit(false);

            if (config.isVerbose()) {
                System.out.println("Connected to editor_db and platform_db successfully!\n");
            }
        } catch (ClassNotFoundException e) {
            throw new SQLException("PostgreSQL JDBC Driver not found", e);
        }
    }

    /**
     * Close all database connections
     */
    public void closeAll() {
        try {
            if (editorDbConnection != null && !editorDbConnection.isClosed()) {
                editorDbConnection.close();
                if (config.isVerbose()) {
                    System.out.println("Closed editor_db connection");
                }
            }
            if (platformDbConnection != null && !platformDbConnection.isClosed()) {
                platformDbConnection.close();
                if (config.isVerbose()) {
                    System.out.println("Closed platform_db connection");
                }
            }
        } catch (SQLException e) {
            System.err.println("Error closing connections: " + e.getMessage());
        }
    }

    /**
     * Commit transactions on both databases
     */
    public void commitAll() throws SQLException {
        if (editorDbConnection != null) {
            editorDbConnection.commit();
        }
        if (platformDbConnection != null) {
            platformDbConnection.commit();
        }
        if (config.isVerbose()) {
            System.out.println("Committed all transactions\n");
        }
    }

    /**
     * Rollback transactions on both databases
     */
    public void rollbackAll() {
        try {
            if (editorDbConnection != null) {
                editorDbConnection.rollback();
            }
            if (platformDbConnection != null) {
                platformDbConnection.rollback();
            }
            System.err.println("Rolled back all transactions");
        } catch (SQLException e) {
            System.err.println("Error during rollback: " + e.getMessage());
        }
    }

    /**
     * Clear all tables in both databases (if configured)
     */
    public void clearAllTables() throws SQLException {
        if (!config.isClearTablesBeforeLoad()) {
            return;
        }

        if (config.isVerbose()) {
            System.out.println("Clearing all tables...");
        }

        // Clear editor_db tables
        clearEditorDbTables();

        // Clear platform_db tables
        clearPlatformDbTables();

        if (config.isVerbose()) {
            System.out.println("All tables cleared successfully!\n");
        }
    }

    /**
     * Clear all tables in editor_db
     */
    private void clearEditorDbTables() throws SQLException {
        try (Statement stmt = editorDbConnection.createStatement()) {
            // Order matters due to foreign key constraints
            stmt.executeUpdate("TRUNCATE TABLE evaluation CASCADE");
            stmt.executeUpdate("TRUNCATE TABLE bug_report CASCADE");
            stmt.executeUpdate("TRUNCATE TABLE game_genres CASCADE");
            stmt.executeUpdate("TRUNCATE TABLE game_platforms CASCADE");
            stmt.executeUpdate("TRUNCATE TABLE patch CASCADE");
            stmt.executeUpdate("TRUNCATE TABLE dlc CASCADE");
            stmt.executeUpdate("TRUNCATE TABLE game CASCADE");
            stmt.executeUpdate("TRUNCATE TABLE editor CASCADE");
            
            if (config.isVerbose()) {
                System.out.println("  - Cleared editor_db tables");
            }
        }
    }

    /**
     * Clear all tables in platform_db
     */
    private void clearPlatformDbTables() throws SQLException {
        try (Statement stmt = platformDbConnection.createStatement()) {
            // Order matters due to foreign key constraints
            stmt.executeUpdate("TRUNCATE TABLE game_genres CASCADE");
            stmt.executeUpdate("TRUNCATE TABLE game_platforms CASCADE");
            stmt.executeUpdate("TRUNCATE TABLE patch CASCADE");
            stmt.executeUpdate("TRUNCATE TABLE dlc CASCADE");
            stmt.executeUpdate("TRUNCATE TABLE game CASCADE");
            stmt.executeUpdate("TRUNCATE TABLE editor CASCADE");
            
            if (config.isVerbose()) {
                System.out.println("  - Cleared platform_db tables");
            }
        }
    }

    // Getters
    public Connection getEditorDbConnection() {
        return editorDbConnection;
    }

    public Connection getPlatformDbConnection() {
        return platformDbConnection;
    }
}
