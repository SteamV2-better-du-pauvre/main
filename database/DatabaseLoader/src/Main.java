import config.DatabaseConfig;
import connection.DatabaseConnection;
import loaders.EditorDbLoader;
import loaders.PlatformDbLoader;

import java.sql.SQLException;

/**
 * Main entry point for the Database Loader application.
 * Loads test data into editor_db and platform_db using Datafaker.
 */
public class Main {
    public static void main(String[] args) {
        DatabaseConnection dbConnection = null;
        
        try {
            // Load and print configuration
            DatabaseConfig config = DatabaseConfig.getInstance();
            System.out.println("\n" + "=".repeat(60));
            System.out.println("DATABASE LOADER - Starting");
            System.out.println("=".repeat(60) + "\n");
            
            config.printConfig();

            // Initialize database connections
            dbConnection = new DatabaseConnection();
            dbConnection.connect();

            // Clear tables if configured
            dbConnection.clearAllTables();

            // Load data into editor_db
            EditorDbLoader editorDbLoader = new EditorDbLoader(dbConnection);
            editorDbLoader.loadData();

            // Sync published data to platform_db
            PlatformDbLoader platformDbLoader = new PlatformDbLoader(dbConnection);
            platformDbLoader.loadData(
                editorDbLoader.getGameGenerator(),
                editorDbLoader.getDlcGenerator(),
                editorDbLoader.getPatchGenerator()
            );

            // Commit all transactions
            dbConnection.commitAll();

            // Print summary
            printSummary(config);

            System.out.println("\n" + "=".repeat(60));
            System.out.println("DATABASE LOADER - Completed Successfully!");
            System.out.println("=".repeat(60) + "\n");

        } catch (SQLException e) {
            System.err.println("\n" + "=".repeat(60));
            System.err.println("ERROR: Database operation failed!");
            System.err.println("=".repeat(60));
            System.err.println("Message: " + e.getMessage());
            System.err.println("SQL State: " + e.getSQLState());
            e.printStackTrace();
            
            if (dbConnection != null) {
                dbConnection.rollbackAll();
            }
            System.exit(1);

        } catch (Exception e) {
            System.err.println("\n" + "=".repeat(60));
            System.err.println("ERROR: Unexpected error occurred!");
            System.err.println("=".repeat(60));
            System.err.println("Message: " + e.getMessage());
            e.printStackTrace();
            
            if (dbConnection != null) {
                dbConnection.rollbackAll();
            }
            System.exit(1);

        } finally {
            if (dbConnection != null) {
                dbConnection.closeAll();
            }
        }
    }

    /**
     * Print a summary of what was loaded
     */
    private static void printSummary(DatabaseConfig config) {
        System.out.println("=".repeat(60));
        System.out.println("DATA LOADING SUMMARY");
        System.out.println("=".repeat(60));
        
        int numEditors = config.getNumEditors();
        int avgGamesPerEditor = (config.getNumGamesPerEditorMin() + config.getNumGamesPerEditorMax()) / 2;
        int estimatedGames = numEditors * avgGamesPerEditor;
        int avgDlcsPerGame = (config.getNumDlcPerGameMin() + config.getNumDlcPerGameMax()) / 2;
        int estimatedDlcs = estimatedGames * avgDlcsPerGame;
        int avgPatchesPerGame = (config.getNumPatchesPerGameMin() + config.getNumPatchesPerGameMax()) / 2;
        int estimatedPatches = estimatedGames * avgPatchesPerGame;
        int avgBugReportsPerGame = (config.getNumBugReportsPerGameMin() + config.getNumBugReportsPerGameMax()) / 2;
        int estimatedBugReports = estimatedGames * avgBugReportsPerGame;
        int avgEvaluationsPerGame = (config.getNumEvaluationsPerGameMin() + config.getNumEvaluationsPerGameMax()) / 2;
        int estimatedEvaluations = estimatedGames * avgEvaluationsPerGame;

        System.out.println("Editors:         ~" + numEditors);
        System.out.println("Games:           ~" + estimatedGames);
        System.out.println("DLCs:            ~" + estimatedDlcs);
        System.out.println("Patches:         ~" + estimatedPatches);
        System.out.println("Bug Reports:     ~" + estimatedBugReports);
        System.out.println("Evaluations:     ~" + estimatedEvaluations);
        System.out.println();
        System.out.println("Published to platform_db: ~" + config.getPublishPercentage() + "% of games/dlcs/patches");
        System.out.println("=".repeat(60) + "\n");
    }
}
