package config;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Configuration class for database connection and data generation parameters.
 * Loads settings from config.properties file.
 */
public class DatabaseConfig {
    private static DatabaseConfig instance;
    private final Properties properties;

    // Database connection properties
    private final String dbUrl;
    private final String dbUser;
    private final String dbPassword;

    // Data generation volumes
    private final int numEditors;
    private final int numGamesPerEditorMin;
    private final int numGamesPerEditorMax;
    private final int numDlcPerGameMin;
    private final int numDlcPerGameMax;
    private final int numPatchesPerGameMin;
    private final int numPatchesPerGameMax;
    private final int numBugReportsPerGameMin;
    private final int numBugReportsPerGameMax;
    private final int numEvaluationsPerGameMin;
    private final int numEvaluationsPerGameMax;

    // Data generation settings
    private final int editorEnterprisePercentage;
    private final int publishPercentage;

    // Loader settings
    private final boolean clearTablesBeforeLoad;
    private final boolean verbose;

    private DatabaseConfig() throws IOException {
        properties = new Properties();
        
        // Try loading from config.properties in the project root
        try (InputStream input = new FileInputStream("config.properties")) {
            properties.load(input);
        } catch (IOException e) {
            // Fallback to classpath
            try (InputStream input = getClass().getClassLoader().getResourceAsStream("config.properties")) {
                if (input == null) {
                    throw new IOException("Unable to find config.properties");
                }
                properties.load(input);
            }
        }

        // Database connection
        this.dbUrl = properties.getProperty("db.url", "jdbc:postgresql://localhost:5432/");
        this.dbUser = properties.getProperty("db.user", "user");
        this.dbPassword = properties.getProperty("db.password", "password");

        // Data generation volumes
        this.numEditors = Integer.parseInt(properties.getProperty("num.editors", "20"));
        this.numGamesPerEditorMin = Integer.parseInt(properties.getProperty("num.games.per.editor.min", "3"));
        this.numGamesPerEditorMax = Integer.parseInt(properties.getProperty("num.games.per.editor.max", "7"));
        this.numDlcPerGameMin = Integer.parseInt(properties.getProperty("num.dlc.per.game.min", "0"));
        this.numDlcPerGameMax = Integer.parseInt(properties.getProperty("num.dlc.per.game.max", "3"));
        this.numPatchesPerGameMin = Integer.parseInt(properties.getProperty("num.patches.per.game.min", "2"));
        this.numPatchesPerGameMax = Integer.parseInt(properties.getProperty("num.patches.per.game.max", "5"));
        this.numBugReportsPerGameMin = Integer.parseInt(properties.getProperty("num.bug.reports.per.game.min", "1"));
        this.numBugReportsPerGameMax = Integer.parseInt(properties.getProperty("num.bug.reports.per.game.max", "10"));
        this.numEvaluationsPerGameMin = Integer.parseInt(properties.getProperty("num.evaluations.per.game.min", "5"));
        this.numEvaluationsPerGameMax = Integer.parseInt(properties.getProperty("num.evaluations.per.game.max", "50"));

        // Data generation settings
        this.editorEnterprisePercentage = Integer.parseInt(properties.getProperty("editor.enterprise.percentage", "80"));
        this.publishPercentage = Integer.parseInt(properties.getProperty("publish.percentage", "80"));

        // Loader settings
        this.clearTablesBeforeLoad = Boolean.parseBoolean(properties.getProperty("clear.tables.before.load", "true"));
        this.verbose = Boolean.parseBoolean(properties.getProperty("verbose", "true"));
    }

    /**
     * Get singleton instance of DatabaseConfig
     */
    public static DatabaseConfig getInstance() {
        if (instance == null) {
            try {
                instance = new DatabaseConfig();
            } catch (IOException e) {
                throw new RuntimeException("Failed to load configuration", e);
            }
        }
        return instance;
    }

    // Getters
    public String getDbUrl() { return dbUrl; }
    public String getDbUser() { return dbUser; }
    public String getDbPassword() { return dbPassword; }
    public int getNumEditors() { return numEditors; }
    public int getNumGamesPerEditorMin() { return numGamesPerEditorMin; }
    public int getNumGamesPerEditorMax() { return numGamesPerEditorMax; }
    public int getNumDlcPerGameMin() { return numDlcPerGameMin; }
    public int getNumDlcPerGameMax() { return numDlcPerGameMax; }
    public int getNumPatchesPerGameMin() { return numPatchesPerGameMin; }
    public int getNumPatchesPerGameMax() { return numPatchesPerGameMax; }
    public int getNumBugReportsPerGameMin() { return numBugReportsPerGameMin; }
    public int getNumBugReportsPerGameMax() { return numBugReportsPerGameMax; }
    public int getNumEvaluationsPerGameMin() { return numEvaluationsPerGameMin; }
    public int getNumEvaluationsPerGameMax() { return numEvaluationsPerGameMax; }
    public int getEditorEnterprisePercentage() { return editorEnterprisePercentage; }
    public int getPublishPercentage() { return publishPercentage; }
    public boolean isClearTablesBeforeLoad() { return clearTablesBeforeLoad; }
    public boolean isVerbose() { return verbose; }

    /**
     * Print configuration summary
     */
    public void printConfig() {
        System.out.println("=== Database Loader Configuration ===");
        System.out.println("Database URL: " + dbUrl);
        System.out.println("Number of editors: " + numEditors);
        System.out.println("Games per editor: " + numGamesPerEditorMin + "-" + numGamesPerEditorMax);
        System.out.println("DLCs per game: " + numDlcPerGameMin + "-" + numDlcPerGameMax);
        System.out.println("Patches per game: " + numPatchesPerGameMin + "-" + numPatchesPerGameMax);
        System.out.println("Bug reports per game: " + numBugReportsPerGameMin + "-" + numBugReportsPerGameMax);
        System.out.println("Evaluations per game: " + numEvaluationsPerGameMin + "-" + numEvaluationsPerGameMax);
        System.out.println("Enterprise percentage: " + editorEnterprisePercentage + "%");
        System.out.println("Publish percentage: " + publishPercentage + "%");
        System.out.println("Clear tables before load: " + clearTablesBeforeLoad);
        System.out.println("=====================================\n");
    }
}
