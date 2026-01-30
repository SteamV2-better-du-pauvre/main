package generators;

import config.DatabaseConfig;
import net.datafaker.Faker;
import utils.FakerProvider;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

/**
 * Generator for Bug Report entities.
 * Creates bug reports for games (simulating sync from platform).
 */
public class BugReportGenerator {
    private final Faker faker;
    private final FakerProvider fakerProvider;
    private final DatabaseConfig config;

    private static final String[] PLATFORMS = {"PC", "XBOX", "PS5", "SWITCH"};

    public BugReportGenerator() {
        this.fakerProvider = FakerProvider.getInstance();
        this.faker = fakerProvider.getFaker();
        this.config = DatabaseConfig.getInstance();
    }

    /**
     * Generate bug reports for games
     */
    public void generateBugReports(Connection connection, List<UUID> gameIds, List<UUID> patchIds) 
            throws SQLException {
        if (config.isVerbose()) {
            System.out.println("Generating bug reports...");
        }

        int totalReports = 0;

        for (UUID gameId : gameIds) {
            int numReports = fakerProvider.randomInt(
                config.getNumBugReportsPerGameMin(),
                config.getNumBugReportsPerGameMax()
            );

            generateBugReportsForGame(connection, gameId, patchIds, numReports);
            totalReports += numReports;
        }

        if (config.isVerbose()) {
            System.out.println("Successfully generated " + totalReports + " bug reports\n");
        }
    }

    /**
     * Generate bug reports for a specific game
     */
    private void generateBugReportsForGame(Connection connection, UUID gameId, List<UUID> patchIds, 
                                           int numReports) throws SQLException {
        String sql = "INSERT INTO bug_report (id_game, id_patch, description, plateforme) " +
                     "VALUES (?, ?, ?, ?::platform_enum)";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            for (int i = 0; i < numReports; i++) {
                // Some bug reports are associated with patches (50%), others just with games
                UUID patchId = null;
                if (!patchIds.isEmpty() && fakerProvider.randomBooleanWithProbability(50)) {
                    patchId = patchIds.get(fakerProvider.getRandom().nextInt(patchIds.size()));
                }

                String platform = fakerProvider.randomElement(PLATFORMS);
                String description = generateBugDescription();

                pstmt.setObject(1, gameId);
                pstmt.setObject(2, patchId); // Can be null
                pstmt.setString(3, description);
                pstmt.setString(4, platform);
                pstmt.executeUpdate();
            }
        }
    }

    /**
     * Generate a realistic bug description
     */
    private String generateBugDescription() {
        int variant = fakerProvider.getRandom().nextInt(15);
        
        String[] bugTemplates = {
            "Game crashes when entering %s. Error code: %s. This happens consistently on %s.",
            "Severe performance drop in %s area. FPS drops from 60 to 15-20. Tested on %s.",
            "Audio completely cuts out during %s. Requires game restart to fix. Platform: %s.",
            "Character gets stuck in %s geometry. Cannot move or respawn. Affected area: %s.",
            "Quest '%s' cannot be completed. NPC doesn't spawn after %s. Blocking progression.",
            "Visual glitch in %s. Textures appear distorted or missing. Reproducible on %s.",
            "Multiplayer desync issue during %s. Players experience lag and disconnections on %s.",
            "Save file corruption after %s. Lost %s hours of progress. Critical issue.",
            "Controls become unresponsive in %s. Affects %s functionality. Requires restart.",
            "Memory leak detected during %s. RAM usage climbs to %s GB after extended play.",
            "Collision detection broken in %s. Can walk through walls near %s area.",
            "Achievement '%s' doesn't unlock despite meeting requirements. Tried %s times.",
            "UI elements overlapping in %s menu. Text unreadable on %s resolution.",
            "Incorrect damage calculation for %s. Should be %s but shows different value.",
            "Loading screen freeze when accessing %s. Game becomes unresponsive for %s minutes."
        };

        String[] gameAreas = {
            "the main menu", "inventory screen", "character customization", "multiplayer lobby",
            "final boss arena", "tutorial section", "cutscenes", "level 5", "the marketplace",
            "skill tree menu", "settings page", "quest log", "map screen", "crafting menu"
        };

        String[] values = {
            "0x8007045D", "multiple platforms", "high settings", "low graphics mode",
            "completing mission", "3-4", "1920x1080", "critical hits", "2-3", "V1.2.3"
        };

        String template = bugTemplates[variant];
        String area1 = fakerProvider.randomElement(gameAreas);
        String area2 = fakerProvider.randomElement(gameAreas);
        String value = fakerProvider.randomElement(values);

        // Add some additional context
        String additionalContext = faker.lorem().sentence(15);
        
        return String.format(template, area1, value, area2) + " " + additionalContext;
    }
}
