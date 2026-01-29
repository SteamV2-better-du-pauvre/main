package loaders;

import config.DatabaseConfig;
import connection.DatabaseConnection;
import generators.*;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

/**
 * Loader for editor_db database.
 * Orchestrates the generation and insertion of all data into editor_db.
 */
public class EditorDbLoader {
    private final DatabaseConfig config;
    private final Connection connection;

    // Generators
    private final EditorGenerator editorGenerator;
    private final GameGenerator gameGenerator;
    private final DLCGenerator dlcGenerator;
    private final PatchGenerator patchGenerator;
    private final BugReportGenerator bugReportGenerator;
    private final EvaluationGenerator evaluationGenerator;

    public EditorDbLoader(DatabaseConnection dbConnection) {
        this.config = DatabaseConfig.getInstance();
        this.connection = dbConnection.getEditorDbConnection();
        
        // Initialize generators
        this.editorGenerator = new EditorGenerator();
        this.gameGenerator = new GameGenerator();
        this.dlcGenerator = new DLCGenerator();
        this.patchGenerator = new PatchGenerator();
        this.bugReportGenerator = new BugReportGenerator();
        this.evaluationGenerator = new EvaluationGenerator();
    }

    /**
     * Load all data into editor_db
     */
    public void loadData() throws SQLException {
        if (config.isVerbose()) {
            System.out.println("==================================================");
            System.out.println("Loading data into editor_db...");
            System.out.println("==================================================\n");
        }

        try {
            // Step 1: Generate editors
            List<UUID> editorIds = editorGenerator.generateEditors(connection);

            // Step 2: Generate games (with platforms and genres)
            gameGenerator.generateGames(connection, editorIds);
            List<UUID> allGameIds = gameGenerator.getAllGameIds();

            // Step 3: Generate DLCs
            dlcGenerator.generateDLCs(connection, gameGenerator.getEditorGamesMap());

            // Step 4: Generate patches
            patchGenerator.generatePatches(connection, allGameIds);

            // Step 5: Generate bug reports (simulating sync from platform)
            bugReportGenerator.generateBugReports(connection, allGameIds, patchGenerator.getAllPatchIds());

            // Step 6: Generate evaluations (simulating sync from platform)
            evaluationGenerator.generateEvaluations(connection, allGameIds);

            if (config.isVerbose()) {
                System.out.println("==================================================");
                System.out.println("editor_db loaded successfully!");
                System.out.println("==================================================\n");
            }

        } catch (SQLException e) {
            System.err.println("Error loading data into editor_db: " + e.getMessage());
            throw e;
        }
    }

    /**
     * Get the editor generator (for accessing generated data)
     */
    public EditorGenerator getEditorGenerator() {
        return editorGenerator;
    }

    /**
     * Get the game generator (for accessing generated data)
     */
    public GameGenerator getGameGenerator() {
        return gameGenerator;
    }

    /**
     * Get the DLC generator (for accessing generated data)
     */
    public DLCGenerator getDlcGenerator() {
        return dlcGenerator;
    }

    /**
     * Get the patch generator (for accessing generated data)
     */
    public PatchGenerator getPatchGenerator() {
        return patchGenerator;
    }
}
